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

package com.example;

import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

/**
 * Sample controller demonstrating an HTTP endpoint acquiring data from a reactive GCP Pub/Sub
 * stream.
 *
 * @since 1.2
 */
@Controller
@ResponseBody
public class ReactiveController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Max number of messages returned from one call. Requests above this limit will be truncated to
   * this limit.
   */
  public static final int MAX_RESPONSE_ITEMS = 100;

  @Autowired
  PubSubReactiveFactory reactiveFactory;

  @GetMapping(value = "/getMessages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> getMessages() {

    Flux<AcknowledgeablePubsubMessage> flux =
        this.reactiveFactory.poll("exampleSubscription", 1000);

    AtomicInteger count = new AtomicInteger(0);

    return flux.limitRate(10)
        .take(MAX_RESPONSE_ITEMS, true)
        .map(
            message -> {
              message.ack();
              log.info("Received message number: " + count.getAndIncrement());
              return new String(
                  message.getPubsubMessage().getData().toByteArray(), Charset.defaultCharset());
            });
  }
}
