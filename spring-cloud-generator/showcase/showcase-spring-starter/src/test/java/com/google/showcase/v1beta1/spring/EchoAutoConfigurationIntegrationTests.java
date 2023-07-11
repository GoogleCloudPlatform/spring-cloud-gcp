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

import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.showcase.v1beta1.EchoClient;
import com.google.showcase.v1beta1.EchoRequest;
import com.google.showcase.v1beta1.EchoResponse;
import com.google.showcase.v1beta1.EchoSettings;
import com.google.showcase.v1beta1.Severity;
import com.google.showcase.v1beta1.stub.HttpJsonEchoStub;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Tests for Echo autoconfiguration. */
@EnabledIfSystemProperty(named = "it.showcase", matches = "true")
class EchoAutoConfigurationIntegrationTests {
  private static MockHttpService mockHttpService =
      new MockHttpService(HttpJsonEchoStub.getMethodDescriptors(), "localhost:7469");

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, EchoSpringAutoConfiguration.class));

  @Test
  void testEchoClientRest() {
    System.out.println("Running integration test");
    this.contextRunner
        // https://github.com/googleapis/gapic-showcase#step-2-make-a-request
        .withUserConfiguration(LocalHttpJsonTransportConfig.class)
        .withPropertyValues("com.google.showcase.v1beta1.echo.use-rest=true")
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);

              EchoResponse expectedResponse =
                  EchoResponse.newBuilder()
                      .setContent("content951530617")
                      .setSeverity(Severity.forNumber(0))
                      .build();
              mockHttpService.addResponse(expectedResponse);

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
  static class LocalHttpJsonTransportConfig {
    @Bean
    TransportChannelProvider defaultEchoTransportChannelProvider() {
      TransportChannelProvider transportChannelProvider =
          EchoSettings.defaultHttpJsonTransportProviderBuilder()
              .setHttpTransport(mockHttpService)
              .setEndpoint("http://localhost:7469")
              .build();
      return transportChannelProvider;
    }
  }
}
