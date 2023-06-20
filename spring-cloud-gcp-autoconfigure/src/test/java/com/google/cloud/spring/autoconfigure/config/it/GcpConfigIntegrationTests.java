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

package com.google.cloud.spring.autoconfigure.config.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.autoconfigure.config.GcpConfigBootstrapConfiguration;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/** Integration tests for config auto-configuration. */
@EnabledIfSystemProperty(named = "it.config", matches = "true")
class GcpConfigIntegrationTests {

  private ConfigurableApplicationContext context;

  @AfterEach
  void close() {
    if (this.context != null) {
      this.context.close();
    }
  }

  @Test
  void testConfiguration() {
    this.context =
        new SpringApplicationBuilder()
            .sources(GcpContextAutoConfiguration.class, GcpConfigBootstrapConfiguration.class)
            .web(WebApplicationType.NONE)
            .properties(
                "spring.cloud.gcp.config.enabled=true",
                "spring.application.name=myapp",
                "spring.profiles.active=dontexist,prod",
                "spring.cloud.bootstrap.enabled=true",
                "spring.cloud.gcp.sql.enabled=false")
            .run();

    assertThat(this.context.getEnvironment().getProperty("myapp.queue-size")).isEqualTo("200");
    assertThat(this.context.getEnvironment().getProperty("myapp.feature-x-enabled"))
        .isEqualTo("true");
  }
}
