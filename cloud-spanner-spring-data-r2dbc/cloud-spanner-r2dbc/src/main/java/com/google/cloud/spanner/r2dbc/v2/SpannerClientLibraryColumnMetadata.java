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

import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.StructField;
import io.r2dbc.spi.ColumnMetadata;
import java.util.Objects;

/**
 * {@link ColumnMetadata} implementation for Cloud Spanner.
 */
class SpannerClientLibraryColumnMetadata implements ColumnMetadata {

  private final StructField structField;

  SpannerClientLibraryColumnMetadata(StructField structField) {
    this.structField = structField;
  }

  @Override
  public String getName() {
    return this.structField.getName();
  }

  @Override
  public Type getNativeTypeMetadata() {
    return this.structField.getType();
  }

  @Override
  public Class<?> getJavaType() {
    return ClientLibraryDecoder.getDefaultJavaType(this.structField.getType());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpannerClientLibraryColumnMetadata that = (SpannerClientLibraryColumnMetadata) o;
    return Objects.equals(this.structField, that.structField);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.structField);
  }

  @Override
  public io.r2dbc.spi.Type getType() {
    throw new UnsupportedOperationException();
  }

}
