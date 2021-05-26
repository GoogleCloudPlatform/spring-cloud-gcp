/*
 * Copyright 2020-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.api;

import com.google.cloud.spanner.TimestampBound;
import reactor.core.publisher.Mono;

/**
 * Interface representing custom Cloud Spanner Connection transaction options that do not fit the
 * current R2DBC SPI model.
 *
 * <p>Cast a Cloud Spanner-provided `Connection` object to this interface to take advantage of the
 * readonly transaction types: read-only strongly consistent and read-only stale.
 */
public interface SpannerConnection {
  /**
   * Allows starting a readonly Cloud Spanner transaction with given staleness settings.
   *
   * @param timestampBound staleness settings
   *
   * @return {@link Mono} signaling readonly transaction is ready for use
   */
  Mono<Void> beginReadonlyTransaction(TimestampBound timestampBound);

  /**
   * Allows starting a readonly Cloud Spanner transaction with strong consistency.
   *
   * @return {@link Mono} signaling readonly transaction is ready for use
   */
  Mono<Void> beginReadonlyTransaction();

  boolean isInReadonlyTransaction();
}
