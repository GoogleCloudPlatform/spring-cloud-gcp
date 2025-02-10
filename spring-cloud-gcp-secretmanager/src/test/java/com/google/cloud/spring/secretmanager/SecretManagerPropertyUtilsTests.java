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

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SecretManagerPropertyUtilsTests {

  private static final GcpProjectIdProvider DEFAULT_PROJECT_ID_PROVIDER = () -> "defaultProject";

  static Stream<Arguments> prefixes() {
    return Stream.of(
        Arguments.of("sm://"),
        Arguments.of("sm@")
    );
  }

  @Test
  void testNonSecret() {
    String property = "spring.cloud.datasource";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier).isNull();
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testInvalidSecretFormat_missingSecretId(String prefix) {
    String property = prefix + "";

    assertThatThrownBy(
            () ->
                SecretManagerPropertyUtils.getSecretVersionName(
                    property, DEFAULT_PROJECT_ID_PROVIDER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The GCP Secret Manager secret id must not be empty");
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testShortProperty_secretId(String prefix) {
    String property = prefix + "the-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testShortProperty_projectSecretId(String prefix) {
    String property = prefix + "the-secret/the-version";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("the-version");
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testShortProperty_projectSecretIdVersion(String prefix) {
    String property = prefix + "my-project/the-secret/2";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("2");
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testLongProperty_projectSecret(String prefix) {
    String property = prefix + "projects/my-project/secrets/the-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testLongProperty_projectSecretVersion(String prefix) {
    String property = prefix + "projects/my-project/secrets/the-secret/versions/3";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("3");
  }

  @Test
  void testNonRegionalSecret() {
    String property = "spring.cloud.datasource";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier).isNull();
  }

  @Test
  void testInvalidSecretFormat_missingRegionalSecretId() {
    String property = "sm://";

    assertThatThrownBy(
            () ->
                    SecretManagerPropertyUtils.getSecretVersionName(
                            property, DEFAULT_PROJECT_ID_PROVIDER, "us-central1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("The GCP Secret Manager secret id must not be empty");
  }

  @Test
  void testShortProperty_regionalSecretId() {
    String property = "sm://the-reg-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getLocation()).isEqualTo("us-central1");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-reg-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @Test
  void testShortProperty_projectRegionalSecretId() {
    String property = "sm://the-reg-secret/the-reg-version";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(secretIdentifier.getLocation()).isEqualTo("us-central1");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-reg-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("the-reg-version");
  }

  @Test
  void testShortProperty_projectRegionalSecretIdVersion() {
    String property = "sm://my-project/the-reg-secret/3";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getLocation()).isEqualTo("us-central1");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-reg-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("3");
  }

  @Test
  void testLongProperty_projectRegionalSecret() {
    String property = "sm://projects/my-project/secrets/the-reg-secret";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getLocation()).isEqualTo("us-central1");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-reg-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("latest");
  }

  @Test
  void testLongProperty_projectRegionalSecretVersion() {
    String property = "sm://projects/my-project/secrets/the-reg-secret/versions/2";
    SecretVersionName secretIdentifier =
        SecretManagerPropertyUtils.getSecretVersionName(
            property,
            DEFAULT_PROJECT_ID_PROVIDER,
            "us-central1"
        );

    assertThat(secretIdentifier.getProject()).isEqualTo("my-project");
    assertThat(secretIdentifier.getLocation()).isEqualTo("us-central1");
    assertThat(secretIdentifier.getSecret()).isEqualTo("the-reg-secret");
    assertThat(secretIdentifier.getSecretVersion()).isEqualTo("2");
  }
}
