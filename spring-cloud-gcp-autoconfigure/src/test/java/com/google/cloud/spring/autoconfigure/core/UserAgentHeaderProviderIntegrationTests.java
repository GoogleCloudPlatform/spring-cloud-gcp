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

package com.google.cloud.spring.autoconfigure.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.core.UserAgentHeaderProvider;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * This needs to be an integration test and in a different module because the JAR MANIFEST has to be
 * available for this.getClass().getPackage().getImplementationVersion() to work properly.
 */
class UserAgentHeaderProviderIntegrationTests {

  static final String USER_AGENT_HEADER_NAME = "user-agent";

  /** This test is check if the generated user-agent header is in the right format. */
  @Test
  void testGetHeaders() {
    UserAgentHeaderProvider subject = new UserAgentHeaderProvider(this.getClass());

    String versionRegex = "\\d+\\.\\d+\\.\\d+(\\-RC\\d+)?(\\-SNAPSHOT)?";
    assertThat(subject.getHeaders()).containsKey(USER_AGENT_HEADER_NAME);
    assertThat(subject.getHeaders()).containsEntry(USER_AGENT_HEADER_NAME, subject.getUserAgent());
    assertThat(subject.getHeaders().get(USER_AGENT_HEADER_NAME))
        .matches(
            Pattern.compile("Spring/" + versionRegex + " spring-cloud-gcp-core/" + versionRegex));
    assertThat(subject.getHeaders()).hasSize(1);
  }
}
