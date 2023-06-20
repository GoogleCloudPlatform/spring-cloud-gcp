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

package com.google.cloud.spring.pubsub.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.integration.history.MessageHistory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

/** Tests for the Pub/Sub message header. */
class PubSubHeaderMapperTests {

  @Test
  void testFilterGoogleClientHeaders() {
    PubSubHeaderMapper mapper = new PubSubHeaderMapper();
    Map<String, Object> originalHeaders = new HashMap<>();
    originalHeaders.put("my header", "pantagruel's nativity");
    MessageHeaders internalHeaders = new MessageHeaders(originalHeaders);

    originalHeaders.put("googclient_deliveryattempt", "header attached when DLQ is enabled");
    originalHeaders.put("googclient_anyHeader", "any other possible headers");

    Map<String, String> filteredHeaders = new HashMap<>();
    mapper.fromHeaders(internalHeaders, filteredHeaders);
    assertThat(filteredHeaders).hasSize(1).containsEntry("my header", "pantagruel's nativity");
  }

  @Test
  void testFilterHeaders() {
    PubSubHeaderMapper mapper = new PubSubHeaderMapper();
    Map<String, Object> originalHeaders = new HashMap<>();
    originalHeaders.put("my header", "pantagruel's nativity");
    originalHeaders.put(NativeMessageHeaderAccessor.NATIVE_HEADERS, "deerhunter");
    originalHeaders.put(MessageHistory.HEADER_NAME, "I've traveled to the moon");
    MessageHeaders internalHeaders = new MessageHeaders(originalHeaders);

    Map<String, String> filteredHeaders = new HashMap<>();
    mapper.fromHeaders(internalHeaders, filteredHeaders);
    assertThat(filteredHeaders).hasSize(1).containsEntry("my header", "pantagruel's nativity");
  }

  @Test
  void testDontFilterHeaders() {
    PubSubHeaderMapper mapper = new PubSubHeaderMapper();
    mapper.setOutboundHeaderPatterns("*");
    Map<String, Object> originalHeaders = new HashMap<>();
    originalHeaders.put("my header", "pantagruel's nativity");
    MessageHeaders internalHeaders = new MessageHeaders(originalHeaders);

    Map<String, String> filteredHeaders = new HashMap<>();
    mapper.fromHeaders(internalHeaders, filteredHeaders);
    assertThat(filteredHeaders).hasSize(3);
  }

  @Test
  void testToHeaders() {
    PubSubHeaderMapper mapper = new PubSubHeaderMapper();
    Map<String, String> originalHeaders = new HashMap<>();
    originalHeaders.put(MessageHeaders.ID, "pantagruel's nativity");
    originalHeaders.put(MessageHeaders.TIMESTAMP, "the moon is down");
    originalHeaders.put("my header", "don't touch it");

    Map<String, Object> internalHeaders = mapper.toHeaders(originalHeaders);
    assertThat(internalHeaders).hasSize(3);
  }

  @Test
  void testSetInboundHeaderPatterns() {
    PubSubHeaderMapper mapper = new PubSubHeaderMapper();

    mapper.setInboundHeaderPatterns("x-*");

    Map<String, String> originalHeaders = new HashMap<>();
    String headerValue = "the moon is down";
    originalHeaders.put("x-" + MessageHeaders.TIMESTAMP, headerValue);
    originalHeaders.put("my header", "don't touch it");

    Map<String, Object> internalHeaders = mapper.toHeaders(originalHeaders);

    assertThat(internalHeaders)
        .hasSize(1)
        .containsEntry("x-" + MessageHeaders.TIMESTAMP, headerValue)
        .doesNotContainKey("my header");
  }

  @Test
  void testSetInboundHeaderPatternsNullPatterns() {

    PubSubHeaderMapper mapper = new PubSubHeaderMapper();

    assertThatThrownBy(() -> mapper.setInboundHeaderPatterns(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Header patterns can't be null.");
  }

  @Test
  void testSetInboundHeaderPatternsNullPatternElements() {

    PubSubHeaderMapper mapper = new PubSubHeaderMapper();

    assertThatThrownBy(() -> mapper.setInboundHeaderPatterns(new String[1]))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No header pattern can be null.");
  }
}
