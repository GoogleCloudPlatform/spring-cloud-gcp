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

package com.example;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/** An example source for the sample app. */
@RestController
public class SourceExample {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired private Sinks.Many<Message<UserMessage>> postOffice;

  @PostMapping("/newMessage")
  public Message sendMessage() {
    UserMessage userMessage = new UserMessage("hello", "USER", LocalDateTime.now());
    Message<UserMessage> message1 = MessageBuilder.withPayload(userMessage)
            .setHeader("foo2", "bar2")
            .setHeader("foo3", "bar3")
            .build();
    this.postOffice.tryEmitNext(message1);
    return message1;
  }

  @Configuration
  public static class SourceConfig {

    @Bean
    public Sinks.Many<Message<UserMessage>> postOffice() {
      return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    Supplier<Flux<Message<UserMessage>>> generateUserMessages(Sinks.Many<Message<UserMessage>> postOffice) {
      return postOffice::asFlux;
    }
  }
}
