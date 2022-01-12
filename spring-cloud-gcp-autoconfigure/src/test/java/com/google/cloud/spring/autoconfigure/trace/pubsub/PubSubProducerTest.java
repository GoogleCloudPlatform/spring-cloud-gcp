/*
 * Copyright 2017-2020 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;

class PubSubProducerTest {
  PubsubMessage.Builder message = PubsubMessage.newBuilder();

  PubSubProducerRequest request = new PubSubProducerRequest(message, "myTopic");

  @Test
  void operation() {
    assertThat(request.operation()).isEqualTo("send");
  }

  @Test
  void topic() {
    assertThat(request.channelKind()).isEqualTo("topic");
    assertThat(request.channelName()).isEqualTo("myTopic");
  }
}
