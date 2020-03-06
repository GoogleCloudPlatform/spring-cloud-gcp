/*
 * Copyright 2019-2020 Google LLC
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
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.google.protobuf.Value.KindCase;
import com.google.spanner.v1.PartialResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Converts a stream of {@link PartialResultSet} to a stream of {@link SpannerRow}.
 */
public class PartialResultRowExtractor implements Function<PartialResultSet, List<SpannerRow>> {

  private SpannerRowMetadata metadata = null;
  private int rowSize;
  private boolean prevIsChunk;
  private List<Value> currentRow = new ArrayList<>();
  private Object incompletePiece;
  private KindCase incompletePieceKind;

  private void appendToRow(Value val, List<SpannerRow> rows) {
    this.currentRow.add(val);
    if (this.currentRow.size() == this.rowSize) {
      rows.add(new SpannerRow(this.currentRow, this.metadata));
      this.currentRow = new ArrayList<>();
    }
  }

  /**
   * Assembles as many complete rows as possible, given previous incomplete fields and a new {@link
   * PartialResultSet}.
   *
   * @param partialResultSet a not yet processed result set
   *
   * @return the resulting rows from the input {@link PartialResultSet}.
   */
  public List<SpannerRow> emitRows(PartialResultSet partialResultSet) {
    if (partialResultSet.getValuesList().isEmpty()) {
      return Collections.emptyList();
    }

    List<SpannerRow> rows = new ArrayList<>();
    ensureMetadataAvailable(partialResultSet);
    int availableCount = partialResultSet.getValuesCount();

    if (this.prevIsChunk) {
      concatFirstIncompletePiece(partialResultSet);
    }

    /* if there are more values then it means the incomplete piece is complete.
    Also, if this PR isn't chunked then it is also complete. */
    if (availableCount > 1 || !partialResultSet.getChunkedValue()) {
      emitCompleteFirstValue(partialResultSet, rows);
    }

    emitMiddleWholePieces(partialResultSet, rows, availableCount);

    Value lastVal = partialResultSet.getValues(availableCount - 1);
    if (!this.prevIsChunk && partialResultSet.getChunkedValue()) {
      initializeIncompletePiece(lastVal);
    } else if (availableCount > 1 && !partialResultSet.getChunkedValue()) {
      appendToRow(lastVal, rows);
    }

    this.prevIsChunk = partialResultSet.getChunkedValue();
    return rows;
  }

  private void initializeIncompletePiece(Value lastVal) {
    this.incompletePieceKind = lastVal.getKindCase();
    this.incompletePiece =
            lastVal.getKindCase() == KindCase.STRING_VALUE ? lastVal.getStringValue() :
                    new ArrayList<>(lastVal.getListValue().getValuesList());
  }

  private void emitCompleteFirstValue(PartialResultSet partialResultSet, List<SpannerRow> rows) {
    Value val = this.prevIsChunk ? this.incompletePieceKind == KindCase.STRING_VALUE
        ? Value.newBuilder().setStringValue((String) this.incompletePiece)
        .build()
        : Value.newBuilder()
            .setListValue(
                ListValue.newBuilder()
                    .addAllValues((List<Value>) this.incompletePiece))
            .build()
        : partialResultSet.getValues(0);
    appendToRow(val, rows);
    this.prevIsChunk = false;
  }

  private void emitMiddleWholePieces(PartialResultSet partialResultSet, List<SpannerRow> rows,
      int availableCount) {
    /* Only the final value can be chunked, and only the first value can be a part of a
    previous chunk, so the pieces in the middle are always whole values. */
    for (int i = 1; i < availableCount - 1; i++) {
      appendToRow(partialResultSet.getValues(i), rows);
    }
  }

  private void concatFirstIncompletePiece(PartialResultSet partialResultSet) {
    Value firstPiece = partialResultSet.getValues(0);
    // Concat code from client lib
    if (this.incompletePieceKind == KindCase.STRING_VALUE) {
      this.incompletePiece = this.incompletePiece + firstPiece.getStringValue();
    } else {
      concatLists((List<Value>) this.incompletePiece,
          firstPiece.getListValue().getValuesList());
    }
  }

  private void ensureMetadataAvailable(PartialResultSet partialResultSet) {
    if (this.metadata == null) {
      if (!partialResultSet.hasMetadata()) {
        throw new IllegalStateException("The first partial result set for a query must contain the "
            + "metadata but it was null.");
      }
      this.metadata = new SpannerRowMetadata(partialResultSet.getMetadata());
      this.rowSize = partialResultSet.getMetadata().getRowType().getFieldsCount();
    }
  }

  // Client lib definition. These kind-cases are mergeable for PartialResultSet.
  private boolean isMergeable(KindCase kind) {
    return kind == KindCase.STRING_VALUE || kind == KindCase.LIST_VALUE;
  }

  /**
   * Used to merge List-column value chunks. From Client lib.
   */
  private void concatLists(List<com.google.protobuf.Value> a, List<com.google.protobuf.Value> b) {
    if (a.size() == 0 || b.size() == 0) {
      a.addAll(b);
    } else {
      com.google.protobuf.Value last = a.get(a.size() - 1);
      com.google.protobuf.Value first = b.get(0);
      KindCase lastKind = last.getKindCase();
      KindCase firstKind = first.getKindCase();
      if (isMergeable(lastKind) && lastKind == firstKind) {
        com.google.protobuf.Value merged = null;
        if (lastKind == KindCase.STRING_VALUE) {
          String lastStr = last.getStringValue();
          String firstStr = first.getStringValue();
          merged =
              com.google.protobuf.Value.newBuilder().setStringValue(lastStr + firstStr).build();
        } else { // List
          List<Value> mergedList = new ArrayList<>(last.getListValue().getValuesList());
          concatLists(mergedList, first.getListValue().getValuesList());
          merged =
              com.google.protobuf.Value.newBuilder()
                  .setListValue(ListValue.newBuilder().addAllValues(mergedList))
                  .build();
        }
        a.set(a.size() - 1, merged);
        a.addAll(b.subList(1, b.size()));
      } else {
        a.addAll(b);
      }
    }
  }

  @Override
  public List<SpannerRow> apply(PartialResultSet partialResultSet) {
    return emitRows(partialResultSet);
  }
}
