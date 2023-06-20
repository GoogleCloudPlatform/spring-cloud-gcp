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

package com.google.cloud.spanner.r2dbc.v2;

import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

class SpannerClientLibraryRow implements Row {
  private Struct rowFields;

  public SpannerClientLibraryRow(Struct rowFields) {
    Assert.requireNonNull(rowFields, "rowFields must not be null");
    this.rowFields = rowFields;
  }

  @Override
  public <T> T get(int index, Class<T> type) {
    return ClientLibraryDecoder.decode(this.rowFields, index, type);
  }

  @Override
  public <T> T get(String name, Class<T> type) {
    return ClientLibraryDecoder.decode(this.rowFields, this.rowFields.getColumnIndex(name), type);
  }

  public RowMetadata generateMetadata() {
    Assert.requireNonNull(this.rowFields.getType(), "rowFields type must not be null");
    return new SpannerClientLibraryRowMetadata(this.rowFields.getType().getStructFields());
  }

  @Override
  public RowMetadata getMetadata() {
    throw new UnsupportedOperationException();
  }
}
