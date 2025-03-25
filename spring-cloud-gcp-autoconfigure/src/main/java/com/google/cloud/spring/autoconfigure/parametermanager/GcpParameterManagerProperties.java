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

import static com.google.cloud.spring.autoconfigure.parametermanager.GcpParameterManagerProperties.PREFIX;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(PREFIX)
public class GcpParameterManagerProperties implements CredentialsSupplier {
  /** Configuration prefix for Parameter Manager properties. */
  public static final String PREFIX = "spring.cloud.gcp.parametermanager";

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.CLOUD_PLATFORM.getUrl());

  /** Overrides the GCP Project ID specified in the Core module. */
  private String projectId;

  /**
   * Whether the parameter manager will allow a default parameter value when accessing a
   * non-existing parameter.
   *
   * <p>When set to false, the parameter manager will throw a {@link
   * com.google.api.gax.rpc.NotFoundException}.
   */
  private boolean allowDefaultParameter;

  public Credentials getCredentials() {
    return credentials;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public boolean isAllowDefaultParameter() {
    return allowDefaultParameter;
  }

  public void setAllowDefaultParameter(boolean allowDefaultParameter) {
    this.allowDefaultParameter = allowDefaultParameter;
  }
}
