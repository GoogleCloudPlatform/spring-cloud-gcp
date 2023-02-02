/*
 * Copyright 2017-2023 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.secretmanager;

import static com.google.cloud.spring.autoconfigure.secretmanager.GcpSecretManagerEnvironmentPostProcessor.IMPORT_PROPERTY;
import static com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolver.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class GcpSecretManagerEnvironmentPostProcessorUnitTests {
  private SpringApplicationBuilder application;

  @BeforeEach
  void init() {
    application = new SpringApplicationBuilder(SecretManagerCompatibilityTests.class)
        .web(WebApplicationType.NONE)
        .properties("spring.cloud.gcp.sql.enabled=false");
  }

  @Test
  void testDefaultConfigLocations() {
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(IMPORT_PROPERTY)).isEqualTo(PREFIX);
    }
  }

  @Test
  void testCustomConfigLocations() {
    application.properties("spring.config.import=file:./");
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(IMPORT_PROPERTY)).contains(";" + PREFIX);
    }
  }

  @Test
  void testSecretManagerTemplateExistsByDefault() {
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      assertThat(applicationContext.getBean(SecretManagerTemplate.class)).isNotNull();
    }
  }

  @Test
  void testSecretManagerTemplateDoesNotExistIfDisabled() {
    application.properties("spring.cloud.gcp.secretmanager.enabled=false");
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      assertThatThrownBy(() -> applicationContext.getBean(SecretManagerTemplate.class))
          .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
    }
  }
}
