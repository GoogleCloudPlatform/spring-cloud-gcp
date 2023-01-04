/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.language.v1.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.TransportChannel;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.threeten.bp.Duration;

@ExtendWith(MockitoExtension.class)
class LanguageAutoConfigurationTests {

  private static final String SERVICE_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key.json";
  private static final String TOP_LEVEL_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key-2.json";
  private static final String SERVICE_CREDENTIAL_CLIENT_ID = "45678";
  private static final String TOP_LEVEL_CREDENTIAL_CLIENT_ID = "12345";
  private static final String SERVICE_OVERRIDE_CLIENT_ID = "56789";

  @Mock private TransportChannel mockTransportChannel;
  @Mock private ApiCallContext mockApiCallContext;
  @Mock private TransportChannelProvider mockTransportChannelProvider;
  @Mock private CredentialsProvider mockCredentialsProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, LanguageServiceSpringAutoConfiguration.class));

  @Test
  void testLanguageServiceClientCreated() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          assertThat(client).isNotNull();
        });
  }

}
