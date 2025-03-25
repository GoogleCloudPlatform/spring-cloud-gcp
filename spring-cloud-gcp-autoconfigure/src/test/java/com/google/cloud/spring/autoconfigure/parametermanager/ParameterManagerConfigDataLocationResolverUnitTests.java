package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
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

/** Unit tests for {@link ParameterManagerConfigDataLocationResolver} */
class ParameterManagerConfigDataLocationResolverUnitTests {

  private final ParameterManagerConfigDataLocationResolver resolver =
      new ParameterManagerConfigDataLocationResolver();
  private final ConfigDataLocationResolverContext context =
      mock(ConfigDataLocationResolverContext.class);
  private final DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
  private final GcpParameterManagerProperties parameterManagerProperties = mock(GcpParameterManagerProperties.class);
  private final GcpProperties gcpProperties = mock(GcpProperties.class);

  @Test
  void isResolvableReturnsFalseWithIncorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test@"))).isFalse();
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("pm:"))).isFalse();
  }

  @Test
  void isResolvableReturnsTrueWithCorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("pm@"))).isTrue();
  }

  @Test
  void createParameterManagerClientWithPresentClientTest() {
    ParameterManagerClient client = mock(ParameterManagerClient.class);
    ParameterManagerConfigDataLocationResolver.setParameterManagerClient(client);
    assertThat(ParameterManagerConfigDataLocationResolver.createParameterManagerClient(context))
        .isEqualTo(client);
  }

  @Test
  void resolveReturnsConfigDataLocation() {
    List<ParameterManagerConfigDataResource> locations =
        resolver.resolve(context, ConfigDataLocation.of("pm@my-parameter/v1"));
    assertThat(locations).hasSize(1);
    assertThat(locations)
        .first()
        .extracting("location")
        .isEqualTo(ConfigDataLocation.of("pm@my-parameter/v1"));
    ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
    when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
    assertThatCode(() -> defaultBootstrapContext.close(applicationContext))
        .doesNotThrowAnyException();
  }

  @BeforeEach
  void registerBean() {
    CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
    ParameterManagerClient parameterManagerClient = mock(ParameterManagerClient.class);

    defaultBootstrapContext.register(
        GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
    defaultBootstrapContext.register(
        GcpParameterManagerProperties.class,
        BootstrapRegistry.InstanceSupplier.of(parameterManagerProperties));
    defaultBootstrapContext.register(
        CredentialsProvider.class, BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
    defaultBootstrapContext.register(
        ParameterManagerClient.class,
        BootstrapRegistry.InstanceSupplier.of(parameterManagerClient));

    when(context.getBinder()).thenReturn(new Binder());
    when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
  }

  @Test
  void testParameterManagerProjectIdTakesPrecedence() {
    when(parameterManagerProperties.getProjectId()).thenReturn("parameter-manager-property-id");
    when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
    GcpProjectIdProvider projectIdProvider =
        ParameterManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider.getProjectId()).isEqualTo("parameter-manager-property-id");
  }

  @Test
  void testProjectIdUseCoreWhenNoParameterManagerProjectId() {
    when(gcpProperties.getProjectId()).thenReturn("gcp-project-id");
    GcpProjectIdProvider projectIdProvider =
        ParameterManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider.getProjectId()).isEqualTo("gcp-project-id");
  }

  @Test
  void testProjectIdFallBackToDefault() {
    GcpProjectIdProvider projectIdProvider =
        ParameterManagerConfigDataLocationResolver.createProjectIdProvider(context);
    assertThat(projectIdProvider).isInstanceOf(DefaultGcpProjectIdProvider.class);
  }
}
