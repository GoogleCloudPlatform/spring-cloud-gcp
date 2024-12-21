package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.BootstrapRegistry.InstanceSupplier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * Unit tests to check compatibility of Secret Manager for regional endpoints.
 */
class SecretManagerRegionalCompatibilityTests {

  private static final String PROJECT_NAME = "regional-secret-manager-project";
  private static final String LOCATION = "us-central1";
  private SpringApplicationBuilder application;
  private SecretManagerServiceClient client;

  @BeforeEach
  void init() {
    application = new SpringApplicationBuilder(SecretManagerRegionalCompatibilityTests.class)
        .web(WebApplicationType.NONE)
        .properties(
            "spring.cloud.gcp.secretmanager.project-id=" + PROJECT_NAME,
            "spring.cloud.gcp.sql.enabled=false",
            "spring.cloud.gcp.secretmanager.location=" + LOCATION);

    client = mock(SecretManagerServiceClient.class);
    SecretVersionName secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(PROJECT_NAME)
            .setLocation(LOCATION)
            .setSecret("my-reg-secret")
            .setSecretVersion("latest")
            .build();
    when(client.accessSecretVersion(secretVersionName))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(
                    SecretPayload.newBuilder().setData(ByteString.copyFromUtf8("newRegSecret")))
                .build());
    secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(PROJECT_NAME)
            .setLocation(LOCATION)
            .setSecret("fake-reg-secret")
            .setSecretVersion("latest")
            .build();
    when(client.accessSecretVersion(secretVersionName))
        .thenThrow(NotFoundException.class);
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.secretmanager.SecretManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  @Test
  void testRegionalConfigurationWhenDefaultSecretIsNotAllowed() {
    application.properties(
            "spring.config.import=sm://")
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClient.class,
                InstanceSupplier.of(client)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("sm://my-reg-secret")).isEqualTo("newRegSecret");
      assertThatThrownBy(() -> environment.getProperty("sm://fake-reg-secret"))
          .isExactlyInstanceOf(NotFoundException.class);
    }
  }

  @Test
  void testRegionalConfigurationWhenDefaultSecretIsAllowed() {
    application.properties(
            "spring.cloud.gcp.secretmanager.allow-default-secret=true",
            "spring.config.import=sm://")
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClient.class,
                InstanceSupplier.of(client)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("sm://my-reg-secret")).isEqualTo("newRegSecret");
      assertThat(environment.getProperty("sm://fake-reg-secret")).isNull();
    }
  }
}
