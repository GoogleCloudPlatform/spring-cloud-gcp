/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.logging.extensions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.logstash.logback.marker.Markers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests for {@link LogstashLoggingEventEnhancer}. */
class LogstashLoggingEventEnhancerTests {

  private ILoggingEvent loggingEvent;

  private LogstashLoggingEventEnhancer enhancer;

  @BeforeEach
  void setup() {
    enhancer = new LogstashLoggingEventEnhancer();

    loggingEvent = Mockito.mock(ILoggingEvent.class);
    when(loggingEvent.getMarkerList())
        .thenReturn(List.of(Markers.append("k1", "v1").and(Markers.append("k2", "v2"))));
  }

  @Test
  void testEnhanceJson() {
    Map<String, Object> jsonMap = new HashMap<>();
    enhancer.enhanceJsonLogEntry(jsonMap, loggingEvent);
    assertThat(jsonMap).containsEntry("k1", "v1").containsEntry("k2", "v2");
  }

  @Test
  void testEnhanceLogEntry() {
    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(Payload.StringPayload.of("hello world"));
    enhancer.enhanceLogEntry(logEntryBuilder, loggingEvent);

    Map<String, String> labels = logEntryBuilder.build().getLabels();
    assertThat(labels).containsEntry("k1", "v1").containsEntry("k2", "v2");
  }
}
