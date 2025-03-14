package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Unit tests for {@link SecretManagerConfigDataLocationResolver}.
 */
class SecretManagerConfigDataLocationResolverUnitTests {

  private final SecretManagerConfigDataLocationResolver resolver = new SecretManagerConfigDataLocationResolver();
  private final ConfigDataLocationResolverContext context = mock(
      ConfigDataLocationResolverContext.class);
  private final DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
  private final GcpSecretManagerProperties secretManagerProperties = mock(GcpSecretManagerProperties.class);
  private final GcpProperties gcpProperties = mock(GcpProperties.class);

  static Stream<Arguments> prefixes() {
    return Stream.of(
        Arguments.of("sm://"),
        Arguments.of("sm@")
    );
  }

  @Test
  void isResolvableReturnsFalseWithIncorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void isResolvableReturnsFalseWithCorrectPrefix(String prefix) {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of(prefix))).isTrue();
  }

  @Test
  void createSecretManagerClientWithPresetClientTest() {
    SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
    SecretManagerConfigDataLocationResolver.setSecretManagerServiceClient(client);
    assertThat(
        SecretManagerConfigDataLocationResolver.createSecretManagerClient(context))
        .isEqualTo(client);
  }

  @ParameterizedTest
  @MethodSource("prefixes")
  void resolveReturnsConfigDataLocation(String prefix) {
    List<SecretManagerConfigDataResource> locations = resolver.resolve(context,
        ConfigDataLocation.of(prefix + "my-secret"));
    assertThat(locations).hasSize(1);
    assertThat(locations).first().extracting("location")
        .isEqualTo(ConfigDataLocation.of(prefix + "my-secret"));
    ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
    when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
    assertThatCode(
        () -> defaultBootstrapContext.close(applicationContext)).doesNotThrowAnyException();
  }

  @BeforeEach
  void registerBean() {
    CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
    SecretManagerServiceClient secretManagerServiceClient = mock(SecretManagerServiceClient.class);

    defaultBootstrapContext.register(
        GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
    defaultBootstrapContext.register(
        GcpSecretManagerProperties.class,
        BootstrapRegistry.InstanceSupplier.of(secretManagerProperties));
    defaultBootstrapContext.register(CredentialsProvider.class,
        BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
    defaultBootstrapContext.register(SecretManagerServiceClient.class,
        BootstrapRegistry.InstanceSupplier.of(secretManagerServiceClient));

    when(context.getBinder()).thenReturn(new Binder());
    when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
  }

  @Test
  void SecretManagerProjectIdTakesPrecedence() {
    when(secretManagerProperties.getProjectId()).thenReturn("secret-manager-property-id");
    GcpProjectIdProvider projectIdProvider =
        SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider.getProjectId()).isEqualTo("secret-manager-property-id");
  }

  @Test
  void testProjectIDUseCoreWhenBoSecretManagerProjectId() {
    when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
    GcpProjectIdProvider projectIdProvider =
        SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider.getProjectId()).isEqualTo("gcp-project-id");
  }

  @Test
  void testProjectIDFallBackToDefault() {
    GcpProjectIdProvider projectIdProvider =
        SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider).isInstanceOf(DefaultGcpProjectIdProvider.class);
  }
}
