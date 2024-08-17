/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.spring.autoconfigure.kms.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.kms.GcpKmsAutoConfiguration;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.kms.KmsTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@EnabledIfSystemProperty(named = "it.kms", matches = "true")
public class KmsAutoConfigurationIntegrationTests {

  private static GcpProjectIdProvider projectIdProvider;

  private final ApplicationContextRunner contextRunnerWithUniverseDomain =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.universe-domain=googleapis.com")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));

  private final ApplicationContextRunner contextRunnerWithEndpoint =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.endpoint=cloudkms.googleapis.com:443")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));

  @BeforeAll
  static void setUp() {
    projectIdProvider = new DefaultGcpProjectIdProvider();
  }

  @Test
  void testValidUniverseDomain_encryptDecryptText() {
    this.contextRunnerWithUniverseDomain.run(
        context -> {
          String kmsStr = "us-east1/integration-test-key-ring/test-key";
          KmsTemplate kmsTemplate = context.getBean(KmsTemplate.class);
          byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");
          String decryptedText = kmsTemplate.decryptText(kmsStr, encryptedBytes);
          assertThat(decryptedText).isEqualTo("1234");
        });
  }

  @Test
  void testValidEndpoint_encryptDecryptText() {
    this.contextRunnerWithEndpoint.run(
        context -> {
          String kmsStr = "us-east1/integration-test-key-ring/test-key";
          KmsTemplate kmsTemplate = context.getBean(KmsTemplate.class);
          byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");
          String decryptedText = kmsTemplate.decryptText(kmsStr, encryptedBytes);
          assertThat(decryptedText).isEqualTo("1234");
        });
  }
}
