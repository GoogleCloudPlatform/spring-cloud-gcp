/*
 * Copyright 2016-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.trace.brave.propagation;

import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stackdriver Trace propagation.
 *
 * <p>Tries to extract a trace ID and span ID using the B3 key set, such as {@code X-B3-TraceId},
 * {@code X-B3-SpanId}, etc. If not present, tries the {@code x-cloud-trace-context} key.
 *
 * <p>Uses {@link B3Propagation} injection, to inject the tracing context using B3 headers.
 */
public class StackdriverTracePropagation implements Propagation<String> {
  /** 128 trace ID lower-hex encoded into 32 characters (required) */
  public static final String TRACE_ID_NAME = "x-cloud-trace-context";

  /**
   * @param primary typically constructed by {@link B3Propagation#newFactoryBuilder()}
   */
  public static Propagation.Factory newFactory(Propagation.Factory primary) {
    if (primary == null) {
      throw new NullPointerException("primary == null");
    }
    return new Factory(primary);
  }

  static final class Factory extends Propagation.Factory {
    final Propagation.Factory primary;

    Factory(Propagation.Factory primary) {
      this.primary = primary;
    }

    @Override
    public Propagation<String> get() {
      return new StackdriverTracePropagation(primary.get());
    }

    @Deprecated
    public <K> Propagation<K> create(KeyFactory<K> keyFactory) {
      return StringPropagationAdapter.create(get(), keyFactory);
    }

    @Override
    public boolean supportsJoin() {
      return false;
    }

    @Override
    public boolean requires128BitTraceId() {
      return true;
    }

    @Override
    public TraceContext decorate(TraceContext context) {
      return primary.decorate(context);
    }
  }

  final Propagation<String> primary;
  final List<String> keyNames;

  StackdriverTracePropagation(Propagation<String> primary) {
    this.primary = primary;
    ArrayList<String> keyNames = new ArrayList<>(primary.keys());
    keyNames.add(TRACE_ID_NAME);
    this.keyNames = Collections.unmodifiableList(keyNames);
  }

  @Override
  public List<String> keys() {
    return keyNames;
  }

  @Override
  public <R> Injector<R> injector(Setter<R, String> setter) {
    return primary.injector(setter);
  }

  @Override
  public <R> Extractor<R> extractor(Getter<R, String> getter) {
    if (getter == null) {
      throw new NullPointerException("getter == null");
    }
    return new CloudTraceContextExtractor<>(primary, getter);
  }
}
