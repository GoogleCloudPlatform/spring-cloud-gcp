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

import brave.propagation.Propagation;
import com.google.pubsub.v1.PubsubMessage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PubSubConsumerRequestSetterTest extends PropagationSetterTest<PubSubConsumerRequest> {
  PubSubConsumerRequest request =
      new PubSubConsumerRequest(PubsubMessage.newBuilder(), "mySubscription");

  @Override
  protected PubSubConsumerRequest request() {
    return request;
  }

  @Override
  protected Propagation.Setter<PubSubConsumerRequest, String> setter() {
    return PubSubConsumerRequest.SETTER;
  }

  @Override
  protected Iterable<String> read(PubSubConsumerRequest request, String key) {
    return StreamSupport.stream(request.delegate.getAttributesMap().entrySet().spliterator(), false)
        .filter(entry -> entry.getKey().equals(key))
        .map(entry -> entry.getValue())
        .collect(Collectors.toList());
  }
}
