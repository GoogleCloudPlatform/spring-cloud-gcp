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

package com.google.cloud.spanner.r2dbc;

import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import io.r2dbc.spi.ColumnMetadata;

/**
 * {@link ColumnMetadata} implementation for Cloud Spanner.
 */
public class SpannerColumnMetadata implements ColumnMetadata {

  private final Field columnField;

  public SpannerColumnMetadata(Field columnField) {
    this.columnField = columnField;
  }

  @Override
  public String getName() {
    return this.columnField.getName();
  }

  @Override
  public Type getNativeTypeMetadata() {
    return this.columnField.getType();
  }
}
