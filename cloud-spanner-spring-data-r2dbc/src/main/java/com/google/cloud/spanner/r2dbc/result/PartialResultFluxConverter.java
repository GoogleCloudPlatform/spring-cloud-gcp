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
import com.google.spanner.v1.PartialResultSet;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.FluxSink;

/**
 * Provides a stream of Cloud Spanner rows based on the input stream of {@link PartialResultSet}s.
 */
public class PartialResultFluxConverter implements CoreSubscriber<PartialResultSet> {

  private final PartialResultRowExtractor rowExtractor = new PartialResultRowExtractor();

  private Subscription spannerSubscription;

  private final FluxSink<SpannerRow> sink;

  /**
   * Creates a converter from a given {@link PartialResultSet} flux.
   * @param sink the flux sink
   */
  public PartialResultFluxConverter(FluxSink<SpannerRow> sink) {
    this.sink = sink;
  }

  @Override
  public void onSubscribe(Subscription s) {
    spannerSubscription = s;
    // initial result
    spannerSubscription.request(1);
  }

  @Override
  public void onNext(PartialResultSet partialResultSet) {
    PartialResultFluxConverter.this.rowExtractor.emitRows(partialResultSet).forEach(sink::next);

    // no demand management yet; just request one at a time
    spannerSubscription.request(1);
  }

  @Override
  public void onError(Throwable t) {
    sink.error(t);
  }

  @Override
  public void onComplete() {
    sink.complete();
  }
}
