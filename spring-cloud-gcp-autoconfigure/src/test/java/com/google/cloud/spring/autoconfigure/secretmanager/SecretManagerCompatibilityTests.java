package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.BootstrapRegistry.InstanceSupplier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Unit tests to check compatibility of Secret Manager.
 */
class SecretManagerCompatibilityTests {

  private static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";

  /**
   * Users with the legacy configuration (i.e., bootstrap phrase) should get {@link
   * com.google.cloud.spring.secretmanager.SecretManagerTemplate} autoconfiguration and properties
   * resolved. Note that {@link SecretManagerBootstrapConfigurationTests.TestBootstrapConfiguration}
   * is automatically picked to create a mock {@link SecretManagerServiceClient} object as it is
   * specified in spring.factories.
   */
  @Test
  void testLegacyConfiguration() {
    SpringApplicationBuilder legacyApplication = new SpringApplicationBuilder(
        GcpSecretManagerBootstrapConfiguration.class)
        .properties(
            "spring.cloud.gcp.secretmanager.project-id=" + PROJECT_NAME,
            "spring.cloud.bootstrap.enabled=true",
            "spring.cloud.gcp.sql.enabled=false")
        .web(WebApplicationType.NONE);
    try (ConfigurableApplicationContext applicationContext = legacyApplication.run()) {
      String secret = applicationContext.getEnvironment().getProperty("sm://my-secret");
      assertThat(secret).isEqualTo("hello");
    }
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.secretmanager.SecretManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  @Test
  void testNewConfiguration() {
    SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(PROJECT_NAME)
            .setSecret("my-secret")
            .setSecretVersion("latest")
            .build();

    when(client.accessSecretVersion(secretVersionName))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(
                    SecretPayload.newBuilder().setData(ByteString.copyFromUtf8("newSecret")))
                .build());

    SpringApplicationBuilder newApplication = new SpringApplicationBuilder(
        GcpSecretManagerBootstrapConfiguration.class)
        .properties(
            "spring.cloud.gcp.secretmanager.project-id=" + PROJECT_NAME,
            "spring.cloud.gcp.secretmanager.legacy=false",
            "spring.config.import=sm://",
            "spring.cloud.gcp.sql.enabled=false")
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClient.class,
                InstanceSupplier.of(client)
            )
        )
        .web(WebApplicationType.NONE);
    try (ConfigurableApplicationContext applicationContext = newApplication.run()) {
      String secret = applicationContext.getEnvironment().getProperty("sm://my-secret");
      assertThat(secret).isEqualTo("newSecret");
    }
  }
}
