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

import com.example.model.UserMessage;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Configuration for Spring Cloud Stream source.
 *
 * <p>What makes this class a valid Spring Cloud Stream source is the presence of a {@link Supplier}
 * bean.
 *
 * <p>The {@link Sinks.Many} is used only as a local communication mechanism between the {@link
 * FrontendController} and this Spring Cloud Stream source.
 *
 * @since 1.2
 */
@Configuration
public class Source {

  @Autowired private Sinks.Many<UserMessage> postOffice;

  @Bean
  Supplier<Flux<UserMessage>> generateUserMessages() {
    return () -> postOffice.asFlux();
  }
}
