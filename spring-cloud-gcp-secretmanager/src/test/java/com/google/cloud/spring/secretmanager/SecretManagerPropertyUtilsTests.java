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

package com.google.cloud.spring.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.junit.jupiter.api.Test;

class SecretManagerPropertyUtilsTests {

  private static final GcpProjectIdProvider DEFAULT_PROJECT_ID_PROVIDER = () -> "defaultProject";

  @Test
  void testNonSecret() {
    String property = "spring.cloud.datasource";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier).isNull();
  }

  @Test
  void testInvalidSecretFormat_missingSecretId() {
    String property = "sm://";

    assertThatThrownBy(
            () ->
                SecretManagerPropertyUtils.getSecretVersionName(
                    property, DEFAULT_PROJECT_ID_PROVIDER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The GCP Secret Manager secret id must not be empty");
  }

  @Test
  void testShortProperty_secretId() {
    String property = "sm://the-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @Test
  void testShortProperty_projectSecretId() {
    String property = "sm://the-secret/the-version";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("the-version");
  }

  @Test
  void testShortProperty_projectSecretIdVersion() {
    String property = "sm://my-project/the-secret/2";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("2");
  }

  @Test
  void testLongProperty_projectSecret() {
    String property = "sm://projects/my-project/secrets/the-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @Test
  void testLongProperty_projectSecretVersion() {
    String property = "sm://projects/my-project/secrets/the-secret/versions/3";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("3");
  }
}
