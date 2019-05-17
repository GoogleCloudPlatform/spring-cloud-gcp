/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc.result;

import com.google.cloud.spanner.r2dbc.SpannerRow;
import com.google.cloud.spanner.r2dbc.SpannerRowMetadata;
import com.google.protobuf.Value;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.StructType;
import java.util.ArrayList;
import java.util.List;

/**
 * NOT thread-safe. But it likely does not need to be.
 */
public class PartialResultRowExtractor {

  // TODO: this should also track the latest resume_token and return it upon request


  // this probably does not even need an atomic reference. Double check gRPC java listener
  // implementation, but it should be accessed by a single thread.
  private SpannerRowMetadata metadata = null;

  private int numFieldsPerRow;

  private List<Value> incompleteRow;

  private Value incompleteField;


  /**
   * Assembles as many complete rows as possible, given previous incomplete fields and a new
   * {@link PartialResultSet}.
   * @param partialResultSet a not yet processed result set
   * @return an ordered list of full rows, each containing the row metadata
   */
  public List<SpannerRow> extractCompleteRows(PartialResultSet partialResultSet) {
    List<SpannerRow> fullRows = new ArrayList<>();

    if (partialResultSet.hasMetadata()) {
      metadata = new SpannerRowMetadata(partialResultSet.getMetadata());
      StructType rowType = partialResultSet.getMetadata().getRowType();
      this.numFieldsPerRow = rowType.getFieldsCount();
    }
    // TODO: handle the case where metdata is not available yet
    if (metadata == null) {
      throw new RuntimeException("Metadata failed to arrive with the first PartialResultSet");
    }


    // TODO: handle partials left over at the end of previous row
    // TODO: account for chunked values (field split between partial result sets).
    List<Value> values = partialResultSet.getValuesList();

    int startIndex = 0;
    int endIndex = this.numFieldsPerRow;

    // handle full rows
    while (endIndex <= values.size()) {
      System.out.println("looking up columns from " + startIndex + " to " + endIndex);

      List<Value> singleRowValues = values.subList(startIndex, endIndex);
      // TODO: add row metadata
      fullRows.add(new SpannerRow(singleRowValues, metadata));

      startIndex += this.numFieldsPerRow;
      endIndex += this.numFieldsPerRow;

    }

    // TODO: store partial row + last field, if chunked.

    return fullRows;
  }

}
