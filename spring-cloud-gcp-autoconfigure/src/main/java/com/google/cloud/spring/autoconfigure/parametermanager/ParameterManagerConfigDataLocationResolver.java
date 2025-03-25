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

import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.arrow.util.VisibleForTesting;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

public class ParameterManagerConfigDataLocationResolver
    implements ConfigDataLocationResolver<ParameterManagerConfigDataResource> {

  /** ConfigData Prefix for Google Cloud Parameter Manager. */
  public static final String PARAMETER_MANAGER_PREFIX = "pm@";

  /** A static client to avoid creating another client after refreshing. */
  private static ParameterManagerClient parameterManagerClient;

  private static void registerParameterManagerBeans(ConfigDataLocationResolverContext context) {
    // Register the Parameter Manager properties.
    registerBean(
        context, GcpParameterManagerProperties.class, getParameterManagerProperties(context));
    // Register the Parameter Manager client.
    registerAndPromoteBean(
        context,
        ParameterManagerClient.class,
        // lazy register the client solely for unit test.
        BootstrapRegistry.InstanceSupplier.from(() -> createParameterManagerClient(context)));
    // Register the GCP Project ID provider.
    registerAndPromoteBean(
        context,
        GcpProjectIdProvider.class,
        BootstrapRegistry.InstanceSupplier.of(createProjectIdProvider(context)));
    // Register the Parameter Manager template.
    registerAndPromoteBean(
        context,
        ParameterManagerTemplate.class,
        BootstrapRegistry.InstanceSupplier.of(createParameterManagerTemplate(context)));
  }

  private static GcpParameterManagerProperties getParameterManagerProperties(
      ConfigDataLocationResolverContext context) {
    return context
        .getBinder()
        .bind(GcpParameterManagerProperties.PREFIX, GcpParameterManagerProperties.class)
        .orElse(new GcpParameterManagerProperties());
  }

  private static GcpProjectIdProvider createProjectIdProvider(
      ConfigDataLocationResolverContext context) {
    GcpParameterManagerProperties properties =
        context.getBootstrapContext().get(GcpParameterManagerProperties.class);
    return properties.getProjectId() != null
        ? properties::getProjectId
        : new DefaultGcpProjectIdProvider();
  }

  @VisibleForTesting
  static synchronized ParameterManagerClient createParameterManagerClient(
      ConfigDataLocationResolverContext context) {
    if (parameterManagerClient != null && !parameterManagerClient.isTerminated()) {
      return parameterManagerClient;
    }

    try {
      GcpParameterManagerProperties properties =
          context.getBootstrapContext().get(GcpParameterManagerProperties.class);
      DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider(properties);
      ParameterManagerSettings settings =
          ParameterManagerSettings.newBuilder()
              .setCredentialsProvider(credentialsProvider)
              .setHeaderProvider(
                  new UserAgentHeaderProvider(ParameterManagerConfigDataLoader.class))
              .build();
      parameterManagerClient = ParameterManagerClient.create(settings);

      return parameterManagerClient;
    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to create the Parameter Manager Client for ConfigData loading.", e);
    }
  }

  private static ParameterManagerTemplate createParameterManagerTemplate(
      ConfigDataLocationResolverContext context) {
    ParameterManagerClient client = context.getBootstrapContext().get(ParameterManagerClient.class);
    GcpProjectIdProvider projectIdProvider =
        context.getBootstrapContext().get(GcpProjectIdProvider.class);
    GcpParameterManagerProperties properties =
        context.getBootstrapContext().get(GcpParameterManagerProperties.class);

    return new ParameterManagerTemplate(client, projectIdProvider)
        .setAllowDefaultParameterValue(properties.isAllowDefaultParameter());
  }

  /**
   * Registers a bean in the Bootstrap Registry.
   *
   * <p>The Bootstrap Registry is a temporary context which exists for creating the ConfigData
   * property sources.
   */
  private static <T> void registerBean(
      ConfigDataLocationResolverContext context, Class<T> type, T instance) {
    context
        .getBootstrapContext()
        .registerIfAbsent(type, BootstrapRegistry.InstanceSupplier.of(instance));
  }

  /**
   * Registers the bean in the Bootstrap Registry *and* promotes it to be in the standard
   * application context.
   */
  private static <T> void registerAndPromoteBean(
      ConfigDataLocationResolverContext context,
      Class<T> type,
      BootstrapRegistry.InstanceSupplier<T> supplier) {
    context.getBootstrapContext().registerIfAbsent(type, supplier);
    context
        .getBootstrapContext()
        .addCloseListener(
            event -> {
              T instance = event.getBootstrapContext().get(type);
              String beanName = "gcp-parametermanager-config-data-" + type.getSimpleName();
              ConfigurableListableBeanFactory factory =
                  event.getApplicationContext().getBeanFactory();
              if (!factory.containsSingleton(beanName)) {
                factory.registerSingleton(beanName, instance);
              }
            });
  }

  @VisibleForTesting
  static void setParameterManagerClient(ParameterManagerClient client) {
    parameterManagerClient = client;
  }

  @Override
  public boolean isResolvable(
      ConfigDataLocationResolverContext context, ConfigDataLocation location) {
    return location.hasPrefix(PARAMETER_MANAGER_PREFIX);
  }

  @Override
  public List<ParameterManagerConfigDataResource> resolve(
      ConfigDataLocationResolverContext context, ConfigDataLocation location)
      throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
    registerParameterManagerBeans(context);

    return Collections.singletonList(new ParameterManagerConfigDataResource(location));
  }
}
