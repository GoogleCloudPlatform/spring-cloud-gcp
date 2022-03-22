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

package com.google.cloud.spring.secretmanager.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
class SecretManagerPropertySourceIntegrationTests {

  private ConfigurableApplicationContext context =
      new SpringApplicationBuilder(SecretManagerTestConfiguration.class, TestConfiguration.class)
          .web(WebApplicationType.NONE)
          .properties("spring.cloud.bootstrap.enabled=true")
          .run();

  private static final String TEST_SECRET_ID = "spring-cloud-gcp-it-secret";

  @BeforeAll
  static void prepare() {
    // Create the test secret if it does not already currently exist.
    ConfigurableApplicationContext setupContext =
        new SpringApplicationBuilder(SecretManagerTestConfiguration.class)
            .web(WebApplicationType.NONE)
            .run();

    SecretManagerTemplate template =
        setupContext.getBeanFactory().getBean(SecretManagerTemplate.class);
    if (!template.secretExists(TEST_SECRET_ID)) {
      template.createSecret(TEST_SECRET_ID, "the secret data.");
    }
  }

  @Test
  void testConfiguration() {
    assertThat(context.getEnvironment().getProperty("sm://" + TEST_SECRET_ID))
        .isEqualTo("the secret data.");

    byte[] byteArraySecret =
        context.getEnvironment().getProperty("sm://" + TEST_SECRET_ID + "/latest", byte[].class);
    assertThat(byteArraySecret).isEqualTo("the secret data.".getBytes());
  }

  @Test
  void testValueAnnotation() {
    String secret = context.getBean("secret", String.class);
    assertThat(secret).isEqualTo("the secret data.");
  }

  @Test
  void testMissingSecret() {
    assertThatThrownBy(
            () -> context.getEnvironment().getProperty("sm://missing-secret/10", String.class))
        .hasCauseInstanceOf(StatusRuntimeException.class)
        .hasMessageContaining("NOT_FOUND");
  }

  @Configuration
  static class TestConfiguration {

    @Value("${sm://" + TEST_SECRET_ID + "}")
    private String secret;

    @Bean
    public String secret() {
      return secret;
    }
  }
}
