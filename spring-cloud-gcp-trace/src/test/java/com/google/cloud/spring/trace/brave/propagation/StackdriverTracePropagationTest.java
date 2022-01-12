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

import static org.assertj.core.api.Assertions.assertThat;

import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class StackdriverTracePropagationTest {
  static final String XCLOUD_TRACE_ID = "c108dc108dc108dc108dc108dc108d00";
  static final String XCLOUD_VALUE = XCLOUD_TRACE_ID + "/1234";

  static final String B3_HEADER = "b3";
  static final String B3_TRACE_ID = "b3b3b3b3b3b34da6a3ce929d0e0e4736";
  static final String B3_VALUE = B3_TRACE_ID + "-00f067aa0ba902b7-1";

  Propagation<String> propagation =
      StackdriverTracePropagation.newFactory(B3Propagation.FACTORY).get();
  TraceContext.Extractor<Map<String, String>> extractor = propagation.extractor(Map::get);

  @Test
  public void b3TakesPrecedenceOverCloudHeader() {

    Map<String, String> headers = new HashMap<>();
    headers.put(StackdriverTracePropagation.TRACE_ID_NAME, XCLOUD_VALUE);
    headers.put(B3_HEADER, B3_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(B3_TRACE_ID);
  }

  @Test
  public void cloudReturnedWhenB3Missing() {
    Map<String, String> headers = new HashMap<>();
    headers.put(StackdriverTracePropagation.TRACE_ID_NAME, XCLOUD_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(XCLOUD_TRACE_ID);
  }

  @Test
  public void b3ReturnedWhenCloudHeaderMissing() {
    Map<String, String> headers = new HashMap<>();
    headers.put(B3_HEADER, B3_VALUE);

    TraceContextOrSamplingFlags ctx = extractor.extract(headers);

    assertThat(ctx.context().traceIdString()).isEqualTo(B3_TRACE_ID);
  }

  @Test
  public void emptyContextReturnedWhenNoHeadersPresent() {
    TraceContextOrSamplingFlags ctx = extractor.extract(new HashMap<>());

    assertThat(ctx).isSameAs(TraceContextOrSamplingFlags.EMPTY);
  }
}
