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

package com.google.cloud.spring.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/** Tests for the {@link DefaultCredentialsProvider}. */
class DefaultCredentialsProviderTests {

  @Test
  void testResolveScopesDefaultScopes() {
    List<String> scopes = DefaultCredentialsProvider.resolveScopes(null);
    assertThat(scopes.size()).isGreaterThan(1);
    assertThat(scopes).contains(GcpScope.PUBSUB.getUrl());
  }

  @Test
  void testResolveScopesOverrideScopes() {
    List<String> scopes =
        DefaultCredentialsProvider.resolveScopes(Collections.singletonList("myscope"));
    assertThat(scopes).hasSize(1).contains("myscope");
  }

  @Test
  void testResolveScopesStarterScopesPlaceholder() {
    List<String> scopes =
        DefaultCredentialsProvider.resolveScopes(Arrays.asList("DEFAULT_SCOPES", "myscope"));
    assertThat(scopes)
        .hasSize(GcpScope.values().length + 1)
        .contains(GcpScope.PUBSUB.getUrl())
        .contains("myscope");
  }

  @Test
  void testImpersonatedServiceAccountGetterSetter() {
    Credentials credentials = new Credentials();
    assertThat(credentials.getImpersonatedServiceAccount()).isNull();

    credentials.setImpersonatedServiceAccount("sa@project.iam.gserviceaccount.com");
    assertThat(credentials.getImpersonatedServiceAccount())
        .isEqualTo("sa@project.iam.gserviceaccount.com");
  }

  @Test
  void testEncodedKeyTakesPrecedenceOverImpersonation() throws IOException {
    Credentials credentials = new Credentials();
    credentials.setEncodedKey(encodedFakeKey());
    credentials.setImpersonatedServiceAccount("sa@project.iam.gserviceaccount.com");

    DefaultCredentialsProvider provider = new DefaultCredentialsProvider(() -> credentials);

    assertThat(provider.getCredentials()).isInstanceOf(ServiceAccountCredentials.class);
    assertThat(((ServiceAccountCredentials) provider.getCredentials()).getClientEmail())
        .isEqualTo("test@fake-project.iam.gserviceaccount.com");
  }

  @Test
  void testLocationTakesPrecedenceOverImpersonation() throws IOException {
    Credentials credentials = new Credentials();
    credentials.setLocation(new ClassPathResource("fake-credential-key.json"));
    credentials.setImpersonatedServiceAccount("sa@project.iam.gserviceaccount.com");

    DefaultCredentialsProvider provider = new DefaultCredentialsProvider(() -> credentials);

    assertThat(provider.getCredentials()).isInstanceOf(ServiceAccountCredentials.class);
    assertThat(((ServiceAccountCredentials) provider.getCredentials()).getClientEmail())
        .isEqualTo("test@fake-project.iam.gserviceaccount.com");
  }

  private static String encodedFakeKey() throws IOException {
    byte[] keyBytes =
        Files.readAllBytes(Paths.get("src/test/resources/fake-credential-key.json"));
    return Base64.getEncoder().encodeToString(new String(keyBytes, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8));
  }
}
