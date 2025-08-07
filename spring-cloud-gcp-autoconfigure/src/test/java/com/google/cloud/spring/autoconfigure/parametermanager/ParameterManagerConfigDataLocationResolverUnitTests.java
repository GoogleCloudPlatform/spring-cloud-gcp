/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerClientFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
  private final GcpParameterManagerProperties parameterManagerProperties =
      mock(GcpParameterManagerProperties.class);
  private final GcpProperties gcpProperties = mock(GcpProperties.class);

  @Nested
  class WithStandardBeans {
    @Test
    void isResolvableReturnsFalseWithIncorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
    }

    void isResolvableReturnsFalseWithCorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm@"))).isTrue();
    }

    void resolveReturnsConfigDataLocation() {
      List<ParameterManagerConfigDataResource> locations =
          resolver.resolve(context, ConfigDataLocation.of("sm@my-parameter"));
      assertThat(locations).hasSize(1);
      assertThat(locations)
          .first()
          .extracting("location")
          .isEqualTo(ConfigDataLocation.of("sm@my-parameter"));
      ConfigurableApplicationContext applicationContext =
          mock(ConfigurableApplicationContext.class);
      when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
      assertThatCode(() -> defaultBootstrapContext.close(applicationContext))
          .doesNotThrowAnyException();
    }

    @Test
    void createParameterManagerClientWithPresetClientTest() {
      ParameterManagerClient client = mock(ParameterManagerClient.class);
      ParameterManagerConfigDataLocationResolver.setParameterManagerClient(client);
      assertThat(ParameterManagerConfigDataLocationResolver.createParameterManagerClient(context))
          .isEqualTo(client);
    }

    @Test
    void createParameterManagerClientFactoryWithPresetClientTest() {
      ParameterManagerClientFactory parameterManagerClientFactory =
          mock(ParameterManagerClientFactory.class);
      ParameterManagerConfigDataLocationResolver.setParameterManagerClientFactory(
          parameterManagerClientFactory);
      assertThat(
              ParameterManagerConfigDataLocationResolver.createParameterManagerClientFactory(
                  context))
          .isEqualTo(parameterManagerClientFactory);
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

    @BeforeEach
    void registerBean() {
      Credentials credentials = mock(Credentials.class);
      CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
      ParameterManagerClient parameterManagerServiceClient = mock(ParameterManagerClient.class);

      when(gcpProperties.getCredentials()).thenReturn(credentials);
      when(parameterManagerProperties.getCredentials()).thenReturn(credentials);

      defaultBootstrapContext.register(
          GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
      defaultBootstrapContext.register(
          GcpParameterManagerProperties.class,
          BootstrapRegistry.InstanceSupplier.of(parameterManagerProperties));
      defaultBootstrapContext.register(
          CredentialsProvider.class, BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
      defaultBootstrapContext.register(
          ParameterManagerClient.class,
          BootstrapRegistry.InstanceSupplier.of(parameterManagerServiceClient));
      when(context.getBinder()).thenReturn(new Binder());
      when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
    }
  }

  @Nested
  class WithFactoryBean {
    @Test
    void isResolvableReturnsFalseWithIncorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
    }

    void isResolvableReturnsFalseWithCorrectPrefix() {
      assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm@"))).isTrue();
    }

    void resolveReturnsConfigDataLocation() {
      List<ParameterManagerConfigDataResource> locations =
          resolver.resolve(context, ConfigDataLocation.of("sm@my-parameter"));
      assertThat(locations).hasSize(1);
      assertThat(locations)
          .first()
          .extracting("location")
          .isEqualTo(ConfigDataLocation.of("sm@my-parameter"));
      ConfigurableApplicationContext applicationContext =
          mock(ConfigurableApplicationContext.class);
      when(applicationContext.getBeanFactory()).thenReturn(new DefaultListableBeanFactory());
      assertThatCode(() -> defaultBootstrapContext.close(applicationContext))
          .doesNotThrowAnyException();
    }

    @Test
    void createParameterManagerClientWithPresetClientTest() {
      ParameterManagerClient client = mock(ParameterManagerClient.class);
      ParameterManagerConfigDataLocationResolver.setParameterManagerClient(client);
      assertThat(ParameterManagerConfigDataLocationResolver.createParameterManagerClient(context))
          .isEqualTo(client);
    }

    @Test
    void createParameterManagerClientFactoryWithPresetClientTest() {
      ParameterManagerClientFactory parameterManagerClientFactory =
          mock(ParameterManagerClientFactory.class);
      ParameterManagerConfigDataLocationResolver.setParameterManagerClientFactory(
          parameterManagerClientFactory);
      assertThat(
              ParameterManagerConfigDataLocationResolver.createParameterManagerClientFactory(
                  context))
          .isEqualTo(parameterManagerClientFactory);
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

    @BeforeEach
    void registerBean() {
      Credentials credentials = mock(Credentials.class);
      CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
      ParameterManagerClient parameterManagerServiceClient = mock(ParameterManagerClient.class);
      ParameterManagerClientFactory parameterManagerClientFactory =
          mock(ParameterManagerClientFactory.class);

      when(gcpProperties.getCredentials()).thenReturn(credentials);
      when(parameterManagerProperties.getCredentials()).thenReturn(credentials);

      defaultBootstrapContext.register(
          GcpProperties.class, BootstrapRegistry.InstanceSupplier.of(gcpProperties));
      defaultBootstrapContext.register(
          GcpParameterManagerProperties.class,
          BootstrapRegistry.InstanceSupplier.of(parameterManagerProperties));
      defaultBootstrapContext.register(
          CredentialsProvider.class, BootstrapRegistry.InstanceSupplier.of(credentialsProvider));
      defaultBootstrapContext.register(
          ParameterManagerClient.class,
          BootstrapRegistry.InstanceSupplier.of(parameterManagerServiceClient));
      defaultBootstrapContext.register(
          ParameterManagerClientFactory.class,
          BootstrapRegistry.InstanceSupplier.of(parameterManagerClientFactory));

      when(context.getBinder()).thenReturn(new Binder());
      when(context.getBootstrapContext()).thenReturn(defaultBootstrapContext);
    }
  }
}
