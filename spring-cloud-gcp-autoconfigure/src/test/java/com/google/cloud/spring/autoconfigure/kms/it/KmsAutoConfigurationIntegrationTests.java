package com.google.cloud.spring.autoconfigure.kms.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.gax.rpc.UnauthenticatedException;
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

  private final ApplicationContextRunner contextRunnerWithValidUniverse =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.universe-domain=googleapis.com")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));

  private final ApplicationContextRunner contextRunnerWithInvalidUniverse =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.universe-domain=example.com")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));

  private final ApplicationContextRunner contextRunnerWithValidEndpoint =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.endpoint=cloudkms.googleapis.com:443")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));
  private final ApplicationContextRunner contextRunnerWithInvalidEndpoint =
      new ApplicationContextRunner()
          .withPropertyValues("spring.cloud.gcp.kms.endpoint=kms.example.com:123")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpKmsAutoConfiguration.class));

  @BeforeAll
  static void setUp() {
    projectIdProvider = new DefaultGcpProjectIdProvider();
  }

  @Test
  void testValidUniverseDomain_encryptDecryptText() {
    this.contextRunnerWithValidUniverse.run(
        context -> {
          String kmsStr = "us-east1/integration-test-key-ring/test-key";
          KmsTemplate kmsTemplate = context.getBean(KmsTemplate.class);
          byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");
          String decryptedText = kmsTemplate.decryptText(kmsStr, encryptedBytes);
          assertThat(decryptedText).isEqualTo("1234");
        });
  }

  @Test
  void testInvalidUniverse_encryptDecryptText() {
    this.contextRunnerWithInvalidUniverse.run(
        context -> {
          String kmsStr = "us-east1/integration-test-key-ring/test-key";
          KmsTemplate kmsTemplate = context.getBean(KmsTemplate.class);
          UnauthenticatedException exception =
              assertThrows(
                  UnauthenticatedException.class, () -> kmsTemplate.encryptText(kmsStr, "1234"));
          assertThat(exception)
              .hasMessageContaining(
                  "The configured universe domain (example.com) does not match the universe domain found in the credentials");
        });
  }

  @Test
  void testValidEndpoint_encryptDecryptText() {
    this.contextRunnerWithValidEndpoint.run(
        context -> {
          String kmsStr = "us-east1/integration-test-key-ring/test-key";
          KmsTemplate kmsTemplate = context.getBean(KmsTemplate.class);
          byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");
          String decryptedText = kmsTemplate.decryptText(kmsStr, encryptedBytes);
          assertThat(decryptedText).isEqualTo("1234");
        });
  }
}
