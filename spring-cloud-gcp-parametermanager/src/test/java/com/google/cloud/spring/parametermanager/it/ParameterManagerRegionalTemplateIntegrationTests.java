/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.parametermanager.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Integration tests for {@link ParameterManagerTemplate} */
@EnabledIfSystemProperty(named = "it.parametermanager", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ParameterManagerRegionalTestConfiguration.class})
public class ParameterManagerRegionalTemplateIntegrationTests {

  @Autowired ParameterManagerTemplate parameterManagerTemplate;

  private String parameterId;
  private String versionId;

  @BeforeEach
  void createParameter() {
    this.parameterId = String.format("test-parameter-%s", UUID.randomUUID());
    this.versionId = String.format("test-version-%s", UUID.randomUUID());

    parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, "{'message': 'Hello World'}");
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              String paramterString =
                  parameterManagerTemplate.getParameterString(
                      "pm@us-central1/" + parameterId + "/" + versionId);
              assertThat(paramterString).isEqualTo("{'message': 'Hello World'}");
            });
  }

  @AfterEach
  void deleteParameter() {
    if (parameterManagerTemplate.parameterVersionExists("us-central1", this.parameterId, "v1")) {
      parameterManagerTemplate.deleteParameterVersion("us-central1", this.parameterId, "v1");
    }

    parameterManagerTemplate.deleteParameterVersion("us-central1", parameterId, versionId);
    parameterManagerTemplate.deleteParameter("us-central1", parameterId);
  }

  @Test
  void testReadWriteParameters() {
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              String paramterString =
                  parameterManagerTemplate.getParameterString(
                      "pm@us-central1/" + parameterId + "/" + versionId);
              assertThat(paramterString).isEqualTo("{'message': 'Hello World'}");

              byte[] parameterBytes =
                  parameterManagerTemplate.getParameterBytes(
                      "pm@us-central1/" + parameterId + "/" + versionId);
              assertThat(parameterBytes).isEqualTo("{'message': 'Hello World'}".getBytes());
            });
  }

  @Test
  void testReadMissingParameter() {
    assertThatThrownBy(
            () ->
                parameterManagerTemplate.getParameterString(
                    "pm@us-central1/" + "test-NON-EXISTING-parameter" + "/" + versionId))
        .isInstanceOf(com.google.api.gax.rpc.NotFoundException.class);
  }

  @Test
  void testUpdateParameter() {
    String newVersionId = "v1";
    parameterManagerTemplate.createParameter(
        "us-central1", this.parameterId, newVersionId, "{'message': 'Hello New World'}");
    await()
        .atMost(Duration.ofSeconds(10))
        .untilAsserted(
            () -> {
              String paramterString =
                  parameterManagerTemplate.getParameterString(
                      "pm@us-central1/" + parameterId + "/" + newVersionId);
              assertThat(paramterString).isEqualTo("{'message': 'Hello New World'}");

              byte[] parameterBytes =
                  parameterManagerTemplate.getParameterBytes(
                      "pm@us-central1/" + this.parameterId + "/" + newVersionId);
              assertThat(parameterBytes).isEqualTo("{'message': 'Hello New World'}".getBytes());
            });
  }
}
