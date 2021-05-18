/*
 * Copyright 2021-2021 Google LLC
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

import com.google.cloud.spanner.AsyncResultSet;
import com.google.cloud.spanner.AsyncResultSet.CallbackResponse;
import com.google.cloud.spanner.AsyncResultSet.ReadyCallback;
import reactor.core.publisher.FluxSink;

/**
 * Implementation of client library {@link ReadyCallback} that adapts {@link AsyncResultSet} to
 * a reactive multi-value sink.
 *
 * <p>If insufficient demand is detected, the underlying {@link AsyncResultSet} will be paused.
 * It will be resumed once additional demand is requested.
 *
 * <p>Make sure to not attach onCancel/onDispose callbacks to the passed in {@link FluxSink}, since
 * there can be only one of each, and {@code ReactiveResultSetCallback} already manages them.
 *
 */
class ReactiveResultSetCallback implements ReadyCallback {
  private FluxSink<SpannerClientLibraryRow> sink;

  private AsyncResultSet spannerResultSet;

  private boolean paused = false;

  /**
   * Instantiates the callback managing the full lifecycle of the passed-in {@code FluxSink}.
   *
   * @param sink a valid subscriber sink
   * @param resultSet the underlying {@code AsyncResultSet} that {@code ReactiveResultSetCallback}
   *                 will cancel or close, as necessary.
   *                  Note that this is the same object that will be passed to {@code cursorReady}
   *                  call for row retrieval
   */
  ReactiveResultSetCallback(FluxSink<SpannerClientLibraryRow> sink, AsyncResultSet resultSet) {

    this.sink = sink;
    this.spannerResultSet = resultSet;

    this.sink.onRequest(this::resumeOnAddedDemand);
    this.sink.onCancel(this.spannerResultSet::cancel);
    this.sink.onDispose(this.spannerResultSet::close);
  }

  @Override
  public CallbackResponse cursorReady(AsyncResultSet resultSet) {

    try {
      synchronized (this) {
        if (this.sink.requestedFromDownstream() < 1) {

          // TODO: when googleapis/java-spanner#1192 is released, remove the nested condition,
          // and return PAUSE regardless of previous state of this.paused. Validate TCK.
          if (!this.paused) {
            this.paused = true;
            return CallbackResponse.PAUSE;
          }
        }
      }

      switch (resultSet.tryNext()) {
        case DONE:
          this.sink.complete();
          return CallbackResponse.DONE;
        case OK:
          this.sink.next(new SpannerClientLibraryRow(resultSet.getCurrentRowAsStruct()));
          return CallbackResponse.CONTINUE;
        default:
          // ResultSet returning NOT_READY or null.
          return CallbackResponse.CONTINUE;
      }
    } catch (Exception t) {
      this.sink.error(t);
      return CallbackResponse.DONE;
    }
  }

  synchronized void resumeOnAddedDemand(long request) {
    if (this.paused && this.sink.requestedFromDownstream() > 0) {
      this.spannerResultSet.resume();
      this.paused = false;
    }
  }

}
