package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerServiceClientFactory;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.bootstrap.BootstrapRegistry;
import org.springframework.boot.bootstrap.DefaultBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ConfigurableApplicationContext;

/** Unit tests for {@link SecretManagerConfigDataLocationResolver}. */
class SecretManagerConfigDataLocationResolverUnitTests {

  private final SecretManagerConfigDataLocationResolver resolver =
      new SecretManagerConfigDataLocationResolver();
  private final ConfigDataLocationResolverContext context =
      mock(ConfigDataLocationResolverContext.class);
  private final DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
  private final GcpSecretManagerProperties secretManagerProperties =
      mock(GcpSecretManagerProperties.class);
  private final GcpProperties gcpProperties = mock(GcpProperties.class);

  static Stream<Arguments> prefixes() {
    return Stream.of(Arguments.of("sm://"), Arguments.of("sm@"));
  }

  @Nested
  class WithStandardBeans {
    @Test
    void isResolvableReturnsFalseWithIncorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
    }

    @ParameterizedTest
    @MethodSource(
        "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolverUnitTests#prefixes")
    void isResolvableReturnsFalseWithCorrectPrefix(String prefix) {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of(prefix))).isTrue();
    }

    @ParameterizedTest
    @MethodSource(
        "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolverUnitTests#prefixes")
    void resolveReturnsConfigDataLocation(String prefix) {
      List<SecretManagerConfigDataResource> locations =
          resolver.resolve(context, ConfigDataLocation.of(prefix + "my-secret"));
      assertThat(locations).hasSize(1);
      assertThat(locations)
          .first()
          .extracting("location")
          .isEqualTo(ConfigDataLocation.of(prefix + "my-secret"));
      ConfigurableApplicationContext applicationContext =
          mock(ConfigurableApplicationContext.class);
      when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
      assertThatCode(() -> defaultBootstrapContext.close(applicationContext))
          .doesNotThrowAnyException();
    }

    @Test
    void createSecretManagerClientWithPresetClientTest() {
      SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
      SecretManagerConfigDataLocationResolver.setSecretManagerServiceClient(client);
      assertThat(SecretManagerConfigDataLocationResolver.createSecretManagerClient(context))
          .isEqualTo(client);
    }

    @Test
    void createSecretManagerClientFactoryWithPresetClientTest() {
      SecretManagerServiceClientFactory secretManagerServiceClientFactory =
          mock(SecretManagerServiceClientFactory.class);
      SecretManagerConfigDataLocationResolver.setSecretManagerServiceClientFactory(
          secretManagerServiceClientFactory);
      assertThat(
              SecretManagerConfigDataLocationResolver.createSecretManagerServiceClientFactory(
                  context))
          .isEqualTo(secretManagerServiceClientFactory);
    }

    @Test
    void testSecretManagerProjectIdTakesPrecedence() {
      when(secretManagerProperties.getProjectId()).thenReturn("secret-manager-property-id");
      when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider.getProjectId()).isEqualTo("secret-manager-property-id");
    }

    @Test
    void testProjectIdUseCoreWhenNoSecretManagerProjectId() {
      when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider.getProjectId()).isEqualTo("gcp-project-id");
    }

    @Test
    void testProjectIdFallBackToDefault() {
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider).isInstanceOf(DefaultGcpProjectIdProvider.class);
    }

    @BeforeEach
    void registerBean() {
      Credentials credentials = mock(Credentials.class);
      CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
      SecretManagerServiceClient secretManagerServiceClient =
          mock(SecretManagerServiceClient.class);

      when(gcpProperties.getCredentials()).thenReturn(credentials);
      when(secretManagerProperties.getCredentials()).thenReturn(credentials);

      defaultBootstrapContext.register(
          GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
      defaultBootstrapContext.register(
          GcpSecretManagerProperties.class,
          BootstrapRegistry.InstanceSupplier.of(secretManagerProperties));
      defaultBootstrapContext.register(
          CredentialsProvider.class, BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
      defaultBootstrapContext.register(
          SecretManagerServiceClient.class,
          BootstrapRegistry.InstanceSupplier.of(secretManagerServiceClient));

      when(context.getBinder()).thenReturn(new Binder());
      when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
    }
  }

  @Nested
  class WhenSecretManagerDisabled {

    private final DefaultBootstrapContext emptyBootstrapContext = new DefaultBootstrapContext();

    @BeforeEach
    void setUp() {
      when(context.getBinder())
          .thenReturn(
              new Binder(
                  new MapConfigurationPropertySource(
                      Map.of("spring.cloud.gcp.secretmanager.enabled", "false"))));
      when(context.getBootstrapContext()).thenReturn(emptyBootstrapContext);
    }

    @ParameterizedTest
    @MethodSource(
        "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolverUnitTests#prefixes")
    void resolveSkipsBeanRegistration(String prefix) {
      List<SecretManagerConfigDataResource> resources =
          resolver.resolve(context, ConfigDataLocation.of(prefix));

      assertThat(resources).hasSize(1);
      assertThat(resources.get(0).isEnabled()).isFalse();
      assertThat(emptyBootstrapContext.isRegistered(CredentialsProvider.class)).isFalse();
      assertThat(emptyBootstrapContext.isRegistered(SecretManagerServiceClient.class)).isFalse();
      assertThat(emptyBootstrapContext.isRegistered(SecretManagerServiceClientFactory.class))
          .isFalse();
      assertThat(emptyBootstrapContext.isRegistered(SecretManagerTemplate.class)).isFalse();
    }
  }

  @Nested
  class WithFactoryBean {
    @Test
    void isResolvableReturnsFalseWithIncorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
    }

    @ParameterizedTest
    @MethodSource(
        "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolverUnitTests#prefixes")
    void isResolvableReturnsFalseWithCorrectPrefix(String prefix) {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of(prefix))).isTrue();
    }

    @ParameterizedTest
    @MethodSource(
        "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolverUnitTests#prefixes")
    void resolveReturnsConfigDataLocation(String prefix) {
      List<SecretManagerConfigDataResource> locations =
          resolver.resolve(context, ConfigDataLocation.of(prefix + "my-secret"));
      assertThat(locations).hasSize(1);
      assertThat(locations)
          .first()
          .extracting("location")
          .isEqualTo(ConfigDataLocation.of(prefix + "my-secret"));
      ConfigurableApplicationContext applicationContext =
          mock(ConfigurableApplicationContext.class);
      when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
      assertThatCode(() -> defaultBootstrapContext.close(applicationContext))
          .doesNotThrowAnyException();
    }

    @Test
    void createSecretManagerClientWithPresetClientTest() {
      SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
      SecretManagerConfigDataLocationResolver.setSecretManagerServiceClient(client);
      assertThat(SecretManagerConfigDataLocationResolver.createSecretManagerClient(context))
          .isEqualTo(client);
    }

    @Test
    void createSecretManagerClientFactoryWithPresetClientTest() {
      SecretManagerServiceClientFactory secretManagerServiceClientFactory =
          mock(SecretManagerServiceClientFactory.class);
      SecretManagerConfigDataLocationResolver.setSecretManagerServiceClientFactory(
          secretManagerServiceClientFactory);
      assertThat(
              SecretManagerConfigDataLocationResolver.createSecretManagerServiceClientFactory(
                  context))
          .isEqualTo(secretManagerServiceClientFactory);
    }

    @Test
    void testSecretManagerProjectIdTakesPrecedence() {
      when(secretManagerProperties.getProjectId()).thenReturn("secret-manager-property-id");
      when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider.getProjectId()).isEqualTo("secret-manager-property-id");
    }

    @Test
    void testProjectIdUseCoreWhenNoSecretManagerProjectId() {
      when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider.getProjectId()).isEqualTo("gcp-project-id");
    }

    @Test
    void testProjectIdFallBackToDefault() {
      GcpProjectIdProvider projectIdProvider =
          SecretManagerConfigDataLocationResolver.createProjectIdProvider(context);
      assertThat(projectIdProvider).isInstanceOf(DefaultGcpProjectIdProvider.class);
    }

    @BeforeEach
    void registerBean() {
      Credentials credentials = mock(Credentials.class);
      CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
      SecretManagerServiceClient secretManagerServiceClient =
          mock(SecretManagerServiceClient.class);
      SecretManagerServiceClientFactory secretManagerServiceClientFactory =
          mock(SecretManagerServiceClientFactory.class);

      when(gcpProperties.getCredentials()).thenReturn(credentials);
      when(secretManagerProperties.getCredentials()).thenReturn(credentials);

      defaultBootstrapContext.register(
          GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
      defaultBootstrapContext.register(
          GcpSecretManagerProperties.class,
          BootstrapRegistry.InstanceSupplier.of(secretManagerProperties));
      defaultBootstrapContext.register(
          CredentialsProvider.class, BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
      defaultBootstrapContext.register(
          SecretManagerServiceClient.class,
          BootstrapRegistry.InstanceSupplier.of(secretManagerServiceClient));
      defaultBootstrapContext.register(
          SecretManagerServiceClientFactory.class,
          BootstrapRegistry.InstanceSupplier.of(secretManagerServiceClientFactory));

      when(context.getBinder()).thenReturn(new Binder());
      when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
    }
  }
}
