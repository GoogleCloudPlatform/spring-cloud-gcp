/*
 * Copyright 2022-2023 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata;

import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.r2dbc.v2.SpannerConstants;
import com.google.common.base.Preconditions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.TransactionDefinition;

public class SpannerTransactionDefinition implements TransactionDefinition {

  private final TransactionDefinition delegate;
  private final TimestampBound timestampBound;

  public SpannerTransactionDefinition(TransactionDefinition delegate,
      TimestampBound timestampBound) {
    Preconditions.checkArgument(timestampBound != null, "TimestampBound shouldn't be null");
    this.delegate = delegate;
    this.timestampBound = timestampBound;
  }

  @Override
  public <T> T getAttribute(Option<T> option) {
    if (SpannerConstants.TIMESTAMP_BOUND.equals(option)) {
      return (T) this.timestampBound;
    }
    return this.delegate.getAttribute(option);
  }
}
