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

import com.google.cloud.logging.LogEntry;
import org.junit.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TraceIdLoggingEnhancer}.
 *
 * @author Mike Eltsufin
 */
public class TraceIdLoggingEnhancerTests {

	@Test
	public void testNoTraceIdAnywhere() {
		TraceIdLoggingEnhancer enhancer = new TraceIdLoggingEnhancer();
		LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

		enhancer.enhanceLogEntry(logEntryBuilder);

		LogEntry logEntry = logEntryBuilder.build();

		assertThat(logEntry.getTrace()).isNull();
		assertThat(logEntry.getSpanId()).isNull();
	}

	@Test
	public void testThreadLocalMDC() {
		TraceIdLoggingEnhancer enhancer = new TraceIdLoggingEnhancer();
		LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

		MDC.put(StackdriverTraceConstants.MDC_FIELD_TRACE_ID, "tid123");
		MDC.put(StackdriverTraceConstants.MDC_FIELD_SPAN_ID, "sid123");

		enhancer.enhanceLogEntry(logEntryBuilder);

		LogEntry logEntry = logEntryBuilder.build();

		assertThat(logEntry.getTrace()).endsWith("tid123");
		assertThat(logEntry.getSpanId()).endsWith("sid123");
	}

	@Test
	public void testThreadLocalTraceId() {
		TraceIdLoggingEnhancer enhancer = new TraceIdLoggingEnhancer();
		TraceIdLoggingEnhancer.setCurrentTraceId("tid123");
		LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(null);

		enhancer.enhanceLogEntry(logEntryBuilder);

		LogEntry logEntry = logEntryBuilder.build();

		assertThat(logEntry.getTrace()).endsWith("tid123");
		assertThat(logEntry.getSpanId()).isNull();

		TraceIdLoggingEnhancer.setCurrentTraceId(null);
	}
}
