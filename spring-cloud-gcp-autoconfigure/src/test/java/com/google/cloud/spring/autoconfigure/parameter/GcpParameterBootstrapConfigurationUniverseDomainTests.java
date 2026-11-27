/*
 * Copyright 2026 Google LLC
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

package com.google.cloud.spring.autoconfigure.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.autoconfigure.TestUtils;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link GcpParameterBootstrapConfiguration}. */
class GcpParameterBootstrapConfigurationUniverseDomainTests {

  @Test
  void defaultsToGoogleapis() {
    assertThat(GcpParameterBootstrapConfiguration.resolveUniverseDomain(null, e -> null))
        .isEqualTo("googleapis.com");
  }

  @Test
  void usesEnvWhenPropertyUnset() {
    assertThat(GcpParameterBootstrapConfiguration.resolveUniverseDomain(null, e -> "tpc.example"))
        .isEqualTo("tpc.example");
  }

  @Test
  void propertyOverridesEnv() {
    assertThat(
            GcpParameterBootstrapConfiguration.resolveUniverseDomain("p.example", e -> "tpc.example"))
        .isEqualTo("p.example");
  }

  private static ParameterManagerSettings settings(
      String location, String universeDomain, String envValue) {
    GcpParameterProperties props = new GcpParameterProperties();
    props.setLocation(location);
    props.setUniverseDomain(universeDomain);
    Function<String, String> env =
        name -> "GOOGLE_CLOUD_UNIVERSE_DOMAIN".equals(name) ? envValue : null;
    try {
      return GcpParameterBootstrapConfiguration.buildSettings(
              props, () -> TestUtils.MOCK_CREDENTIALS, env)
          .build();
    } catch (java.io.IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  void regionalEndpointUsesDefaultUniverse() {
    ParameterManagerSettings s = settings("us-central1", null, null);
    assertThat(s.getUniverseDomain()).isEqualTo("googleapis.com");
    assertThat(s.getEndpoint()).isEqualTo("parametermanager.us-central1.rep.googleapis.com:443");
  }

  @Test
  void regionalEndpointUsesEnvUniverse() {
    ParameterManagerSettings s = settings("us-central1", null, "tpc.example");
    assertThat(s.getUniverseDomain()).isEqualTo("tpc.example");
    assertThat(s.getEndpoint()).isEqualTo("parametermanager.us-central1.rep.tpc.example:443");
  }

  @Test
  void regionalEndpointUsesExplicitUniverseOverEnv() {
    ParameterManagerSettings s = settings("europe-west1", "p.example", "tpc.example");
    assertThat(s.getUniverseDomain()).isEqualTo("p.example");
    assertThat(s.getEndpoint()).isEqualTo("parametermanager.europe-west1.rep.p.example:443");
  }

  @Test
  void globalUsesExplicitUniverseWithoutRegionalEndpoint() {
    ParameterManagerSettings s = settings("global", "p.example", null);
    assertThat(s.getUniverseDomain()).isEqualTo("p.example");
    assertThat(s.getEndpoint()).isEqualTo("parametermanager.p.example:443");
  }
}
