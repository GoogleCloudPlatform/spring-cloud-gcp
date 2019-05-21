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

package com.google.cloud.spanner.r2dbc.util;

import java.util.List;
import java.util.function.Function;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.FluxSink;

/**
 * A {@link org.reactivestreams.Subscriber} that given a function {@code Function<T, List<R>>
 * converter}, maps and forwards a stream  of {@code T} objects to a sink of {@code R} objects.
 */
public class ConvertingFluxAdapter<T, R> implements CoreSubscriber<T> {

  private final FluxSink<R> sink;
  private final Function<T, List<R>> converter;

  private Subscription subscription;

  /**
   * Constructor.
   *
   * @param sink the flux sink to which converted object will be forwarded
   * @param converter the converter that will map input objects to lists of output objects
   */
  public ConvertingFluxAdapter(FluxSink<R> sink, Function<T, List<R>> converter) {
    this.sink = sink;
    this.converter = converter;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
    // initial input request
    this.subscription.request(1);
  }

  @Override
  public void onNext(T input) {
    converter.apply(input).forEach(sink::next);

    // no demand management yet; just request one at a time
    subscription.request(1);
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
