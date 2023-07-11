/*
 * Copyright 2023 Google LLC
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

package com.google.showcase.v1beta1.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.showcase.v1beta1.EchoClient;
import com.google.showcase.v1beta1.EchoRequest;
import com.google.showcase.v1beta1.EchoResponse;
import com.google.showcase.v1beta1.EchoSettings;
import com.google.showcase.v1beta1.Severity;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Tests for Echo autoconfiguration. */
@EnabledIfSystemProperty(named = "it.showcase", matches = "true")
class EchoAutoConfigurationIntegrationTests {

  private static TransportChannelProvider echoGrpcTestTransportProvider;
  private static TransportChannelProvider echoRestTestTransportProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, EchoSpringAutoConfiguration.class));

  @Test
  void testEchoClientGrpc() {
    System.out.println("Running integration test");
    this.contextRunner
        .withUserConfiguration(LocalGrpcTransportConfig.class)
        .run(
            ctx -> {
              TransportChannelProvider transportChannelProvider =
                  ctx.getBean(TransportChannelProvider.class);
              System.out.println(transportChannelProvider);

              EchoSettings echoSettings = ctx.getBean(EchoSettings.class);
              TransportChannelProvider transportChannelProviderFromSettings =
                  echoSettings.getTransportChannelProvider();
              System.out.println(transportChannelProviderFromSettings);

              EchoClient client = ctx.getBean(EchoClient.class);
              System.out.println(client.getSettings().getTransportChannelProvider());
              // https://github.com/googleapis/gapic-showcase#step-2-make-a-request

              EchoResponse expectedResponse =
                  EchoResponse.newBuilder()
                      .setContent("content951530617")
                      .setSeverity(Severity.forNumber(0))
                      .build();

              EchoRequest request =
                  EchoRequest.newBuilder()
                      .setSeverity(Severity.forNumber(0))
                      .setHeader("header-1221270899")
                      .setOtherHeader("otherHeader-2026585667")
                      .build();

              EchoResponse actualResponse = client.echo(request);
              assertThat(actualResponse).isEqualTo(expectedResponse);
            });
  }

  @Configuration
  static class LocalGrpcTransportConfig {
    @Bean
    TransportChannelProvider defaultEchoTransportChannelProvider() {
      return LocalChannelProvider.create("localhost:7469");
    }
  }

  @Configuration
  static class LocalHttpJsonTransportConfig {
    @Bean
    TransportChannelProvider defaultEchoTransportChannelProvider() throws GeneralSecurityException {
      TransportChannelProvider transportChannelProvider =
          EchoSettings.defaultHttpJsonTransportProviderBuilder()
              .setHttpTransport(new NetHttpTransport.Builder().doNotValidateCertificate().build())
              .setEndpoint("http://localhost:7469")
              .build();
      return transportChannelProvider;
    }
  }
}
