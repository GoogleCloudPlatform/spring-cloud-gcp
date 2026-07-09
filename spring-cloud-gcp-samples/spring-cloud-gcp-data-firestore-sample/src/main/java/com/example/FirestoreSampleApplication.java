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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/** Sample application for Spring Data Firestore. */
@SpringBootApplication
@EnableTransactionManagement
@ImportRuntimeHints(FirestoreSampleApplication.FirestoreSampleRuntimeHints.class)
public class FirestoreSampleApplication {

  /**
   * Register reflection hints for Firestore POJOs to support GraalVM Native Image deserialization.
   */
  static class FirestoreSampleRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      hints
          .reflection()
          .registerTypes(
              Arrays.asList(
                  TypeReference.of(User.class),
                  TypeReference.of(Pet.class),
                  TypeReference.of(PhoneNumber.class)),
              hint ->
                  hint.withMembers(
                      MemberCategory.DECLARED_FIELDS,
                      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                      MemberCategory.INVOKE_DECLARED_METHODS));
    }
  }

  @Bean
  @ConditionalOnProperty(
      value = "spring.cloud.gcp.firestore.emulator.enabled",
      havingValue = "true")
  public CredentialsProvider googleCredentials() {
    return NoCredentialsProvider.create();
  }

  public static void main(String[] args) {
    SpringApplication.run(FirestoreSampleApplication.class, args);
  }

  @Bean
  public RouterFunction<ServerResponse> indexRouter(
      @Value("classpath:/static/index.html") final Resource indexHtml) {

    // Serve static index.html at root, for convenient message publishing.
    return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
  }
}
