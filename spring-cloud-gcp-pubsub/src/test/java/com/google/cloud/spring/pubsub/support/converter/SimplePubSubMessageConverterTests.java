/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.pubsub.support.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spring.core.util.MapBuilder;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

/** Tests for the simple message converter. */
class SimplePubSubMessageConverterTests {

  private static final String TEST_STRING = "test";

  private static final Map<String, String> TEST_HEADERS =
      new MapBuilder<String, String>().put("key1", "value1").put("key2", "value2").build();

  @Test
  void testToString() {
    doToTestForType(String.class, a -> a);
  }

  @Test
  void testFromString() {
    doFromTest(TEST_STRING);
  }

  @Test
  void testToByteString() {
    doToTestForType(ByteString.class, a -> new String(a.toByteArray()));
  }

  @Test
  void testFromByteString() {
    doFromTest(ByteString.copyFrom(TEST_STRING.getBytes()));
  }

  @Test
  void testToByteArray() {
    doToTestForType(byte[].class, a -> new String(a));
  }

  @Test
  void testFromByteArray() {
    doFromTest(TEST_STRING.getBytes());
  }

  @Test
  void testToByteBuffer() {
    doToTestForType(ByteBuffer.class, a -> new String(a.array()));
  }

  @Test
  void testFromByteBuffer() {
    doFromTest(ByteBuffer.wrap(TEST_STRING.getBytes()));
  }

  @Test
  void testToUnknown() {

    assertThatThrownBy(() -> doToTestForType(Integer.class, a -> toString()))
            .isInstanceOf(PubSubMessageConversionException.class)
            .hasMessage("Unable to convert Pub/Sub message to " + "payload of type java.lang.Integer.");
  }

  @Test
  void testFromUnknown() {

    assertThatThrownBy(() ->   doFromTest(Integer.class))
            .isInstanceOf(PubSubMessageConversionException.class)
            .hasMessage("Unable to convert payload of type java.lang.Class " + "to byte[] for sending to Pub/Sub.");
  }

  @Test
  void testNullHeaders() {
    SimplePubSubMessageConverter converter = new SimplePubSubMessageConverter();
    PubsubMessage pubsubMessage = converter.toPubSubMessage(TEST_STRING, null);

    assertThat(pubsubMessage.getData().toString(Charset.defaultCharset())).isEqualTo(TEST_STRING);
    assertThat(pubsubMessage.getAttributesMap()).isEqualTo(new HashMap<>());
  }

  private <T> void doToTestForType(Class<T> type, Converter<T, String> toString) {
    SimplePubSubMessageConverter converter = new SimplePubSubMessageConverter();

    // test extraction from PubsubMessage to T

    String extractedMessage =
        toString.convert(
            (T)
                converter.fromPubSubMessage(
                    PubsubMessage.newBuilder()
                        .setData(ByteString.copyFrom(TEST_STRING.getBytes()))
                        .putAllAttributes(TEST_HEADERS)
                        .build(),
                    type));

    assertThat(extractedMessage).isEqualTo(TEST_STRING);
  }

  private <T> void doFromTest(T value) {
    SimplePubSubMessageConverter converter = new SimplePubSubMessageConverter();

    // test conversion of T to PubsubMessage
    PubsubMessage convertedPubSubMessage = converter.toPubSubMessage(value, TEST_HEADERS);
    assertThat(new String(convertedPubSubMessage.getData().toByteArray())).isEqualTo(TEST_STRING);
    assertThat(convertedPubSubMessage.getAttributesMap()).isEqualTo(TEST_HEADERS);
  }

  @Test
  void testOrderingKeyHeader() throws JSONException {
    SimplePubSubMessageConverter converter = new SimplePubSubMessageConverter();
    PubsubMessage pubsubMessage =
        converter.toPubSubMessage(
            "test payload", Collections.singletonMap(GcpPubSubHeaders.ORDERING_KEY, "key1"));
    assertThat(pubsubMessage).isNotNull();
    assertThat(pubsubMessage.getOrderingKey()).isEqualTo("key1");
    assertThat(pubsubMessage.getAttributesCount()).isZero();
  }
}
