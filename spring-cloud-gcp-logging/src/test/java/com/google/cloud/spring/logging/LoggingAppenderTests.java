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

package com.google.cloud.spring.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LoggingAppender}.
 *
 * @since 1.2
 */
class LoggingAppenderTests {

  private final Logging logging = mock(Logging.class);

  private class TestLoggingAppender extends LoggingAppender {

    private LoggingOptions loggingOptions;

    @Override
    protected LoggingOptions getLoggingOptions() {
      if (loggingOptions == null) {
        this.loggingOptions = super.getLoggingOptions().toBuilder()
            // Duplicates logic in base class.
            // Workaround for https://github.com/googleapis/sdk-platform-java/issues/2821
            .setHeaderProvider(new UserAgentHeaderProvider(this.getClass()))
            // Inject mock
            .setServiceFactory((options) -> logging)
            .build();
      }
      return loggingOptions;
    }
  }

  @Test
  void testGetLoggingOptions() {
    LoggingAppender loggingAppender = new TestLoggingAppender();
    loggingAppender.setCredentialsFile("src/test/resources/fake-project-key.json");
    assertThat(loggingAppender.getLoggingOptions().getCredentials()).isNotNull();
    assertThat(loggingAppender.getLoggingOptions().getProjectId()).isEqualTo("fake-project");
    assertThat(loggingAppender.getLoggingOptions().getUserAgent())
        .isNotNull()
        .contains("spring-cloud-gcp-logging")
        .contains("Spring");
  }

  @Test
  void testSetLogDestinationProjectId() {
    LoggingAppender loggingAppender = new TestLoggingAppender();
    loggingAppender.setCredentialsFile("src/test/resources/fake-project-key.json");
    loggingAppender.setLogDestinationProjectId("my-log-destination-project");
    assertThat(loggingAppender.getLoggingOptions().getProjectId())
        .isEqualTo("my-log-destination-project");
  }
}
