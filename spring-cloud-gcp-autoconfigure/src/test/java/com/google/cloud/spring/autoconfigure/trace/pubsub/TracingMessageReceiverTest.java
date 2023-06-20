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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.spring.core.util.MapBuilder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;

class TracingMessageReceiverTest extends PubSubTestBase {

  MessageReceiver mockMesageReceiver = mock(MessageReceiver.class);

  TracingMessageReceiver tracingMessageReceiver =
      new TracingMessageReceiver(mockMesageReceiver, pubSubTracing, "testSubscription");

  @Test
  void testReceiverMessage() {
    PubsubMessage.Builder pubSubMessageBuilder =
        PubsubMessage.newBuilder()
            .putAllAttributes(
                new MapBuilder<String, String>()
                    .put(
                        "b3",
                        "80f198ee56343ba864fe8b2a57d3eff7-e457b5a2e4d86bd1-1-05e3ac9a4f6e3b90")
                    .build())
            .setData(ByteString.copyFrom("test".getBytes()));
    AckReplyConsumer mockAckReplyConsumer = mock(AckReplyConsumer.class);

    tracingMessageReceiver.receiveMessage(pubSubMessageBuilder.build(), mockAckReplyConsumer);
    // we expect tracing header to be stripped
    verify(mockMesageReceiver, times(1))
        .receiveMessage(
            eq(pubSubMessageBuilder.clearAttributes().build()), refEq(mockAckReplyConsumer));
  }
}
