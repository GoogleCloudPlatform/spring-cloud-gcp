/*
 * Copyright 2017-2022 the original author or authors.
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

import static com.google.cloud.spring.trace.brave.propagation.StackdriverTracePropagation.TRACE_ID_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StackdriverTracePropagationTest {
  static final String XCLOUD_TRACE_ID = "c108dc108dc108dc108dc108dc108d00";
  static final String XCLOUD_VALUE = XCLOUD_TRACE_ID + "/1234";

  static final String B3_HEADER = "b3";
  static final String B3_TRACE_ID = "b3b3b3b3b3b34da6a3ce929d0e0e4736";
  static final String B3_VALUE = B3_TRACE_ID + "-00f067aa0ba902b7-1";

  Propagation<String> propagation =
      StackdriverTracePropagation.newFactory(B3Propagation.FACTORY).get();
  TraceContext.Extractor<Map<String, String>> extractor = propagation.extractor(Map::get);

  @Test
  void b3TakesPrecedenceOverCloudHeader() {

    Map<String, String> headers = new HashMap<>();
    headers.put(TRACE_ID_NAME, XCLOUD_VALUE);
    headers.put(B3_HEADER, B3_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(B3_TRACE_ID);
  }

  @Test
  void cloudReturnedWhenB3Missing() {
    Map<String, String> headers = new HashMap<>();
    headers.put(TRACE_ID_NAME, XCLOUD_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(XCLOUD_TRACE_ID);
  }

  @Test
  void b3ReturnedWhenCloudHeaderMissing() {
    Map<String, String> headers = new HashMap<>();
    headers.put(B3_HEADER, B3_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(B3_TRACE_ID);
  }

  @Test
  void emptyContextReturnedWhenNoHeadersPresent() {
    TraceContextOrSamplingFlags ctx = extractor.extract(new HashMap<>());

    assertThat(ctx).isSameAs(TraceContextOrSamplingFlags.EMPTY);
  }

  @Test
  void newFactoryFailsWhenWrappedFactoryNull() {
    assertThatThrownBy(() ->  StackdriverTracePropagation.newFactory(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("primary == null");
  }

  @Test
  void joinNotSupported() {
    Propagation.Factory factory = StackdriverTracePropagation.newFactory(B3Propagation.FACTORY);
    assertThat(factory.supportsJoin()).isFalse();
  }

  @Test
  void requres128BitTraceId() {
    Propagation.Factory factory = StackdriverTracePropagation.newFactory(B3Propagation.FACTORY);
    assertThat(factory.requires128BitTraceId()).isTrue();
  }

  @Test
  void stackdriverPropagationAddsCloudTraceIdToWrappedKeys() {
    Propagation<String> mockPropagation = mock(Propagation.class);
    when(mockPropagation.keys()).thenReturn(Arrays.asList("key1"));

    Propagation<String> cloudPropagation = new StackdriverTracePropagation(mockPropagation);
    assertThat(cloudPropagation.keys()).containsExactly("key1", TRACE_ID_NAME);
  }

  @Test
  void injectorDelegatesToWrappedPropagation() {
    Propagation<String> mockPropagation = mock(Propagation.class);
    Injector<Object> mockInjector = mock(Injector.class);
    when(mockPropagation.injector(any())).thenReturn(mockInjector);

    Propagation<String> cloudPropagation = new StackdriverTracePropagation(mockPropagation);
    assertThat(cloudPropagation.injector((request, key, value) -> {}))
        .isSameAs(mockInjector);
  }

  @Test
  void extractorRequiresGetter() {
    assertThatThrownBy(() -> propagation.extractor(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("getter == null");
  }

  @Test
  void propagationExtractorReturnsCloudTraceContextExtractor() {
    Propagation<String> mockPropagation = mock(Propagation.class);
    Propagation<String> cloudPropagation = new StackdriverTracePropagation(mockPropagation);
    Getter<String, String> getter = (request, key) -> "unused";

    Extractor extractor = cloudPropagation.extractor(getter);
    assertThat(extractor).isInstanceOf(CloudTraceContextExtractor.class);
  }


}
