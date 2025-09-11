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

import java.util.List;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResource;

/**
 * A safe delegating ConfigDataLocationResolver that loads ParameterManagerConfigDataLocationResolver
 * only if Parameter Manager dependency is present on the classpath.
 */
public class ParameterManagerConfigDataLocationResolverLoader
    implements ConfigDataLocationResolver<ConfigDataResource> {
  private static final boolean PARAMETER_MANAGER_PRESENT =
      isClassPresent("com.google.cloud.spring.parametermanager.ParameterManagerPropertyUtils");

  private final ConfigDataLocationResolver<ConfigDataResource> delegate;

  public ParameterManagerConfigDataLocationResolverLoader() {
    this.delegate = PARAMETER_MANAGER_PRESENT ? instantiateRealResolver() : null;
  }

  private ConfigDataLocationResolver<ConfigDataResource> instantiateRealResolver() {
    try {
      Class<?> clazz = Class.forName(
          "com.google.cloud.spring.autoconfigure.parametermanager.ParameterManagerConfigDataLocationResolver");
      return (ConfigDataLocationResolver<ConfigDataResource>) clazz
          .getDeclaredConstructor().newInstance();
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Failed to instantiate ParameterManagerConfigDataLocationResolver", ex);
    }
  }

  /**
   * Checks if the property can be resolved by the Parameter Manager resolver.
   * For the check, we rely on the presence of the ParameterManagerSyntaxUtils class, which is an
   * optional dependency.
   * Since optional dependencies may not be present at runtime, we explicitly check for its
   * existence before resolving the property.
   * If it's not present, it means this config resolver is not meant to be used.
   *
   * @return true if the delegate resolver is initialized and the location has the expected
   *     Parameter Manager prefix (e.g., {@code pm@}); false otherwise.
   */
  @Override
  public boolean isResolvable(
      ConfigDataLocationResolverContext context, ConfigDataLocation location) {
    return delegate != null && delegate.isResolvable(context, location);
  }

  /**
   * Checks if the specified class is present in this runtime.
   *
   * @param clazzFullName the full name of the class for the existence check
   * @return true if present
   */
  private static boolean isClassPresent(String clazzFullName) {
    try {
      Class.forName(clazzFullName);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public List<ConfigDataResource> resolve(
      ConfigDataLocationResolverContext context, ConfigDataLocation location) {
    if (delegate == null) {
      // This should technically never happen if isResolvable is checked first,
      // but safe guard.
      throw new IllegalStateException("Parameter Manager support is not available on the classpath.");
    }
    return delegate.resolve(context, location);
  }
}
