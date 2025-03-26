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

package com.google.cloud.spring.autoconfigure.paramconfig;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Configuration for {@link GoogleParamConfigPropertySourceLocator}.
 *
 * @since 1.1
 */
@ConfigurationProperties(GcpParamConfigProperties.PREFIX)
public class GcpParamConfigProperties implements CredentialsSupplier, EnvironmentAware {

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.CLOUD_PLATFORM.getUrl());

  public static final String PREFIX = "spring.cloud.gcp.paramconfig";
  /**
   * Enables Spring Cloud GCP ParamConfig.
   */
  private boolean enabled;

  /**
   * Name of the application.
   */
  @Value("${spring.application.name:application}")
  private String name;

  /**
   * Comma-delimited string of profiles under which the app is running. Gets its default value from
   * the {@code spring.profiles.active} property, falling back on the {@code
   * spring.profiles.default} property.
   */
  private String profile;

  /**
   * Overrides the GCP project ID specified in the Core module.
   */
  private String location;

  /**
   * Overrides the GCP project ID specified in the Core module.
   */
  private String projectId;

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setProfile(String profile) {
    String[] profiles = profile.split(",");
    if (profiles.length > 0) {
      this.profile = profiles[profiles.length - 1];
    } else {
      this.profile = "default";
    }
  }

  public String getProfile() {
    return this.profile;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  @Override
  public void setEnvironment(Environment environment) {
    if (this.profile == null) {
      String[] profiles = environment.getActiveProfiles();
      if (profiles.length == 0) {
        profiles = environment.getDefaultProfiles();
      }

      if (profiles.length > 0) {
        this.profile = profiles[profiles.length - 1];
      } else {
        this.profile = "default";
      }
    }
    if (this.location == null) {
      this.location = "global";
    }
  }
}
