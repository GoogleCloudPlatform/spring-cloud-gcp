package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
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
import org.springframework.core.env.ConfigurableEnvironment;

/** Unit tests to check compatibility of Parameter Manager. */
class ParameterManagerCompatibilityTests {

  private static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";
  private SpringApplicationBuilder application;
  private ParameterManagerClient client;

  static Stream<Arguments> prefixes() {
    return Stream.of(
    Arguments.of("sm://", "spring.cloud.gcp.project-id="),
        Arguments.of("sm://", "spring.cloud.gcp.parametermanager.project-id="),
        Arguments.of("sm@", "spring.cloud.gcp.project-id="),
        Arguments.of("sm@", "spring.cloud.gcp.parametermanager.project-id="));
  }

  @BeforeEach
  void init() {
    application =
        new SpringApplicationBuilder(ParameterManagerCompatibilityTests.class)
            .web(WebApplicationType.NONE)
            .properties(
                "spring.cloud.gcp.parametermanager.project-id=" + PROJECT_NAME,
                "spring.cloud.gcp.sql.enabled=false");

    client = mock(ParameterManagerClient.class);
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(PROJECT_NAME, "global", "my-param", "v1");
    when(client.getParameterVersion(parameterVersionName))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("new Parameter Version"))
                        .build())
                .build());
    parameterVersionName = ParameterVersionName.of(PROJECT_NAME, "global", "fake-param", "v1");
    when(client.getParameterVersion(parameterVersionName)).thenThrow(NotFoundException.class);
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.parametermanager.ParameterManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  @ParameterizedTest
  @MethodSource("prefixes")
  void testConfigurationWhenDefaultSecretIsNotAllowed(String prefix, String projectIdPropertyName) {
    application
        .properties(projectIdPropertyName + PROJECT_NAME, "spring.config.import=" + prefix)
        .addBootstrapRegistryInitializer(
            (registry) ->
                registry.registerIfAbsent(
                    ParameterManagerClient.class, InstanceSupplier.of(client)));
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(prefix + "global/my-param/v1"))
          .isEqualTo("new Parameter Version");
      assertThatThrownBy(() -> environment.getProperty(prefix + "global/fake-param/v1"))
          .isExactlyInstanceOf(NotFoundException.class);
    }
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void testConfigurationWhenDefaultSecretIsAllowed(String prefix, String projectIdPropertyName) {
    application
        .properties(
            projectIdPropertyName + PROJECT_NAME,
            "spring.cloud.gcp.parametermanager.allow-default-parameter=true",
            "spring.config.import=" + prefix)
        .addBootstrapRegistryInitializer(
            (registry) ->
                registry.registerIfAbsent(
                    ParameterManagerClient.class, InstanceSupplier.of(client)));
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty(prefix + "global/my-param/v1"))
          .isEqualTo("new Parameter Version");
      assertThat(environment.getProperty(prefix + "global/fake-param/v1")).isNull();
    }
  }
}
