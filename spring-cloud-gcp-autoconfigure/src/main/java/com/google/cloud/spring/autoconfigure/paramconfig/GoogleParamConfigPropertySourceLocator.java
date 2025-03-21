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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.yaml.snakeyaml.Yaml;

/**
 * Custom {@link PropertySourceLocator} for Google Cloud Parameter Manager API.
 *
 * @since 1.1
 */
public class GoogleParamConfigPropertySourceLocator implements PropertySourceLocator {

  private static final String PROPERTY_SOURCE_NAME = "spring-cloud-gcp";

  private final ParameterManagerClient parameterManagerClient;

  private String projectId;

  private Credentials credentials;

  private String name;

  private String profile;

  private String location;

  private boolean enabled;

  public GoogleParamConfigPropertySourceLocator(
      GcpProjectIdProvider projectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpParamConfigProperties gcpParamConfigProperties,
      ParameterManagerClient parameterManagerClient)
      throws IOException {

    this.parameterManagerClient = parameterManagerClient;
    Assert.notNull(gcpParamConfigProperties, "Google ParamConfig properties must not be null");

    if (gcpParamConfigProperties.isEnabled()) {
      Assert.notNull(credentialsProvider, "Credentials provider cannot be null");
      Assert.notNull(projectIdProvider, "Project ID provider cannot be null");
      this.credentials =
          gcpParamConfigProperties.getCredentials().hasKey()
              ? new DefaultCredentialsProvider(gcpParamConfigProperties).getCredentials()
              : credentialsProvider.getCredentials();
      this.projectId =
          (gcpParamConfigProperties.getProjectId() != null)
              ? gcpParamConfigProperties.getProjectId()
              : projectIdProvider.getProjectId();
      Assert.notNull(this.credentials, "Credentials must not be null");

      Assert.notNull(this.projectId, "Project ID must not be null");

      this.name = gcpParamConfigProperties.getName();
      this.profile = gcpParamConfigProperties.getProfile();
      this.location = gcpParamConfigProperties.getLocation();
      this.enabled = gcpParamConfigProperties.isEnabled();
      Assert.notNull(this.name, "ParamConfig name must not be null");
      Assert.notNull(this.profile, "ParamConfig version must not be null");
    }
  }

  ParameterVersion getRemoteEnvironment() throws Exception {
    // Fetch the parameter from the parameter manager
    try {
      ParameterVersionName parameterVersionName =
          ParameterVersionName.of(projectId, this.location, this.name, this.profile);
      ParameterVersion response = this.parameterManagerClient.getParameterVersion(parameterVersionName.toString());

      if (response == null) {
        throw new HttpClientErrorException(
          HttpStatusCode.valueOf(500), "Invalid response from Parameter Manager API");
      }
      return response;
    } catch (Exception ex) {
      throw new Exception("Something went wrong!", ex);
    }
  }

  @Override
  public PropertySource<?> locate(Environment environment) {
    if (!this.enabled) {
      return new MapPropertySource(PROPERTY_SOURCE_NAME, Collections.emptyMap());
    }
    Map<String, Object> config;
    try {
      ParameterVersion googleParamConfigEnvironment = getRemoteEnvironment();
      config = convertStringToMap(googleParamConfigEnvironment.getPayload().getData().toStringUtf8());
      Assert.notNull(googleParamConfigEnvironment, "Configuration not in expected format.");
    } catch (Exception ex) {
      String message =
          "Error loading configuration";
      throw new RuntimeException(message, ex);
    }
    return new MapPropertySource(PROPERTY_SOURCE_NAME, config);
  }

  public String getProjectId() {
    return this.projectId;
  }

  public static Map<String, Object> convertStringToMap(String data) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(data, Map.class);
    } catch (Exception e) {
      System.out.println("Error parsing JSON: " + e.getMessage());
      try {
        Yaml yaml = new Yaml();
        return yaml.load(data);
      } catch (Exception ex) {
        throw new RuntimeException("Error parsing Properties", e);
      }
    }
  }
}
