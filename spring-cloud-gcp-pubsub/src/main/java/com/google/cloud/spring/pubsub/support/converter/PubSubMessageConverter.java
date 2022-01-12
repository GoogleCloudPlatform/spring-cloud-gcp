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

package com.google.cloud.spring.pubsub.support.converter;

import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.Map;

/** Interface for converters that can convert POJOs to and from Pub/Sub messages. */
public interface PubSubMessageConverter {

  /**
   * Create a {@code PubsubMessage} given an object for the payload and a map of headers.
   *
   * @param payload the object to place into the message payload
   * @param headers the headers of the message
   * @return the PubsubMessage ready to be sent
   */
  PubsubMessage toPubSubMessage(Object payload, Map<String, String> headers);

  /**
   * Convert the payload of a given {@code PubsubMessage} to a desired Java type.
   *
   * @param message the message containing the payload of the object
   * @param payloadType the desired type of the object
   * @param <T> the type of the payload
   * @return the object converted from the message's payload
   */
  <T> T fromPubSubMessage(PubsubMessage message, Class<T> payloadType);

  default PubsubMessage byteStringToPubSubMessage(ByteString payload, Map<String, String> headers) {
    PubsubMessage.Builder pubsubMessageBuilder = PubsubMessage.newBuilder().setData(payload);

    if (headers != null) {
      pubsubMessageBuilder.putAllAttributes(headers);

      if (headers.containsKey(GcpPubSubHeaders.ORDERING_KEY)) {
        pubsubMessageBuilder.removeAttributes(GcpPubSubHeaders.ORDERING_KEY);
        pubsubMessageBuilder.setOrderingKey(headers.get(GcpPubSubHeaders.ORDERING_KEY));
      }
    }

    return pubsubMessageBuilder.build();
  }
}
