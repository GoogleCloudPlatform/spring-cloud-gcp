package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.autoconfigure.TestUtils;
import com.google.cloud.spring.secretmanager.SecretManagerServiceClientFactory;
import com.google.protobuf.ByteString;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.BootstrapRegistry.InstanceSupplier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Unit tests to check compatibility of Secret Manager.
 */
class SecretManagerCompatibilityTests {

  private static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";
  private SpringApplicationBuilder application;
  private SecretManagerServiceClientFactory secretManagerServiceClientFactory;

  static Stream<Arguments> prefixes() {
    return Stream.of(
        Arguments.of("sm://"),
        Arguments.of("sm@")
    );
  }

  @BeforeEach
  void init() {
    application = new SpringApplicationBuilder(SecretManagerCompatibilityTests.class)
        .web(WebApplicationType.NONE)
        .properties(
            "spring.cloud.gcp.secretmanager.project-id=" + PROJECT_NAME,
            "spring.cloud.gcp.sql.enabled=false")
        .sources(TestConfig.class);

    SecretManagerServiceClient secretManagerServiceClient = mock(SecretManagerServiceClient.class);
    secretManagerServiceClientFactory = mock(SecretManagerServiceClientFactory.class);
    when(secretManagerServiceClientFactory.getClient(null)).thenReturn(secretManagerServiceClient);
    when(secretManagerServiceClientFactory.getClient()).thenReturn(secretManagerServiceClient);
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(PROJECT_NAME)
            .setSecret("my-secret")
            .setSecretVersion("latest")
            .build();
    when(secretManagerServiceClientFactory.getClient().accessSecretVersion(secretVersionName))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(
                    SecretPayload.newBuilder().setData(ByteString.copyFromUtf8("newSecret")))
                .build());
    secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(PROJECT_NAME)
            .setSecret("fake-secret")
            .setSecretVersion("latest")
            .build();
    when(secretManagerServiceClientFactory.getClient().accessSecretVersion(secretVersionName))
        .thenThrow(NotFoundException.class);
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.secretmanager.SecretManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  @ParameterizedTest
  @MethodSource("prefixes")
  void testConfigurationWhenDefaultSecretIsNotAllowed(String prefix) {
    application.properties(
            "spring.config.import=" + prefix)
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClientFactory.class,
                InstanceSupplier.of(secretManagerServiceClientFactory)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(prefix + "my-secret")).isEqualTo("newSecret");
      assertThatThrownBy(() -> environment.getProperty(prefix + "fake-secret"))
          .isExactlyInstanceOf(NotFoundException.class);
    }
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testConfigurationWhenDefaultSecretIsAllowed(String prefix) {
    application.properties(
            "spring.cloud.gcp.secretmanager.allow-default-secret=true",
            "spring.config.import=" + prefix)
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClientFactory.class,
                InstanceSupplier.of(secretManagerServiceClientFactory)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(prefix + "my-secret")).isEqualTo("newSecret");
      assertThat(environment.getProperty(prefix + "fake-secret")).isNull();
    }
  }

  static class TestConfig {

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> TestUtils.MOCK_CREDENTIALS;
    }
  }
}
