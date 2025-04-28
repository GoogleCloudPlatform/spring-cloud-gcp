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

package com.google.cloud.spring.autoconfigure.secretmanager;

import java.util.List;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.util.ClassUtils;

/**
 * A safe delegating ConfigDataLocationResolver that loads SecretManagerConfigDataLocationResolver
 * only if Secret Manager dependency is present on the classpath.
 */
public class SecretManagerConfigDataLocationResolverLoader
    implements ConfigDataLocationResolver<ConfigDataResource> {
  private static final boolean SECRET_MANAGER_PRESENT =
      ClassUtils.isPresent(
          "com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils",
          SecretManagerConfigDataLocationResolverLoader.class.getClassLoader());

  private final ConfigDataLocationResolver<ConfigDataResource> delegate;

  public SecretManagerConfigDataLocationResolverLoader() {
    this.delegate = SECRET_MANAGER_PRESENT ? instantiateRealResolver() : null;
  }

  private ConfigDataLocationResolver<ConfigDataResource> instantiateRealResolver() {
    try {
      Class<?> clazz = Class.forName(
          "com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolver");
      return (ConfigDataLocationResolver<ConfigDataResource>) clazz
          .getDeclaredConstructor().newInstance();
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Failed to instantiate SecretManagerConfigDataLocationResolver", ex);
    }
  }

  @Override
  public boolean isResolvable(
      ConfigDataLocationResolverContext context, ConfigDataLocation location) {
    return delegate != null && delegate.isResolvable(context, location);
  }

  @Override
  public List<ConfigDataResource> resolve(
      ConfigDataLocationResolverContext context, ConfigDataLocation location) {
    if (delegate == null) {
      // This should technically never happen if isResolvable is checked first,
      // but safe guard.
      throw new IllegalStateException("Secret Manager support is not available on the classpath.");
    }
    return delegate.resolve(context, location);
  }
}
