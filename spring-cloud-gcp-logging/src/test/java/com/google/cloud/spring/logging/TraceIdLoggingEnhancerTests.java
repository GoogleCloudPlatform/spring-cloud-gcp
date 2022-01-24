/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.logging.LogEntry;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** Tests for {@link TraceIdLoggingEnhancer}. */
class TraceIdLoggingEnhancerTests {

  TraceIdLoggingEnhancer enhancer = new TraceIdLoggingEnhancer();

  @BeforeEach
  void before() {
    enhancer.setProjectIdProvider(() -> "gcp-project");
    MDC.clear();
    TraceIdLoggingEnhancer.setCurrentTraceId(null);
  }

  @Test
  void testNoTraceIdAnywhere() {
    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

    enhancer.enhanceLogEntry(logEntryBuilder);

    LogEntry logEntry = logEntryBuilder.build();

    assertThat(logEntry.getTrace()).isNull();
    assertThat(logEntry.getSpanId()).isNull();
  }

  @Test
  void testLoggingEventMdc() {

    ILoggingEvent mockLoggingEvent = mock(ILoggingEvent.class);
    Map<String, String> map = new HashMap<>();
    map.put(StackdriverTraceConstants.MDC_FIELD_TRACE_ID, "tid123");
    map.put(StackdriverTraceConstants.MDC_FIELD_SPAN_ID, "sid123");
    when(mockLoggingEvent.getMDCPropertyMap()).thenReturn(map);

    MDC.put(StackdriverTraceConstants.MDC_FIELD_TRACE_ID, "tid-mdc");
    MDC.put(StackdriverTraceConstants.MDC_FIELD_SPAN_ID, "sid-mdc");

    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);
    enhancer.enhanceLogEntry(logEntryBuilder, mockLoggingEvent);

    LogEntry logEntry = logEntryBuilder.build();

    assertThat(logEntry.getTrace()).isEqualTo("projects/gcp-project/traces/tid123");
    assertThat(logEntry.getSpanId()).isEqualTo("sid123");
  }

  @Test
  void testThreadLocalMdc() {
    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

    MDC.put(StackdriverTraceConstants.MDC_FIELD_TRACE_ID, "tid123");
    MDC.put(StackdriverTraceConstants.MDC_FIELD_SPAN_ID, "sid123");

    enhancer.enhanceLogEntry(logEntryBuilder);

    LogEntry logEntry = logEntryBuilder.build();

    assertThat(logEntry.getTrace()).isEqualTo("projects/gcp-project/traces/tid123");
    assertThat(logEntry.getSpanId()).isEqualTo("sid123");
  }

  @Test
  void testThreadLocalTraceId() {
    TraceIdLoggingEnhancer.setCurrentTraceId("tid123");
    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

    enhancer.enhanceLogEntry(logEntryBuilder);

    LogEntry logEntry = logEntryBuilder.build();

    assertThat(logEntry.getTrace()).isEqualTo("projects/gcp-project/traces/tid123");
    assertThat(logEntry.getSpanId()).isNull();
  }
}
