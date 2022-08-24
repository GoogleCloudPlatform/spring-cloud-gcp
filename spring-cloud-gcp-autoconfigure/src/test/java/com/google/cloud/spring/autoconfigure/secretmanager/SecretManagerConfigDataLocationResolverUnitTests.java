package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @Test
  void isResolvableReturnsFalseWithIncorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
  }

  @Test
  void isResolvableReturnsFalseWithCorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm://"))).isTrue();
  }

  @Test
  void createSecretManagerClientWithPresetClientTest() {
    SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
    SecretManagerConfigDataLocationResolver.setSecretManagerServiceClient(client);
    assertThat(
        SecretManagerConfigDataLocationResolver.createSecretManagerClient(context))
        .isEqualTo(client);
  }

  @Test
  void resolveReturnsConfigDataLocation() {
    List<SecretManagerConfigDataResource> locations = resolver.resolve(context,
        ConfigDataLocation.of("sm://my-secret"));
    assertThat(locations).hasSize(1);
    assertThat(locations).first().extracting("location")
        .isEqualTo(ConfigDataLocation.of("sm://my-secret"));
    ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
    when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
    assertThatCode(
        () -> defaultBootstrapContext.close(applicationContext)).doesNotThrowAnyException();
  }

  @BeforeEach
  void registerBean() {
    GcpSecretManagerProperties properties = mock(GcpSecretManagerProperties.class);
    CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
    SecretManagerServiceClient secretManagerServiceClient = mock(SecretManagerServiceClient.class);

    defaultBootstrapContext.register(GcpSecretManagerProperties.class,
        BootstrapRegistry.InstanceSupplier.of(properties));
    defaultBootstrapContext.register(CredentialsProvider.class,
        BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
    defaultBootstrapContext.register(SecretManagerServiceClient.class,
        BootstrapRegistry.InstanceSupplier.of(secretManagerServiceClient));

    when(context.getBinder()).thenReturn(new Binder());
    when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
  }
}
