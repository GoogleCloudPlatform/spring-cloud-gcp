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

package com.google.cloud.spring.autoconfigure.parameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.RenderParameterVersionResponse;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * Tests for the parameter property source locator.
 */
class GoogleParameterPropertySourceLocatorTest {

  private GcpParameterProperties gcpParameterProperties;

  private Map<String, Object> expectedProperties;

  private GoogleParameterPropertySourceLocator googleParameterPropertySourceLocator;

  private GcpProjectIdProvider projectIdProvider;

  private CredentialsProvider credentialsProvider;

  private ParameterManagerClient parameterManagerClient;

  @BeforeEach
  void setUp() {
    this.gcpParameterProperties = mock(GcpParameterProperties.class);
    when(this.gcpParameterProperties.getName()).thenReturn("test");
    when(this.gcpParameterProperties.isEnabled()).thenReturn(true);
    com.google.cloud.spring.core.Credentials configCredentials =
        mock(com.google.cloud.spring.core.Credentials.class);
    when(this.gcpParameterProperties.getCredentials()).thenReturn(configCredentials);
    when(this.gcpParameterProperties.getProfile()).thenReturn("default");
    when(this.gcpParameterProperties.getLocation()).thenReturn("global");
    this.expectedProperties = new HashMap<>();
    this.expectedProperties.put("property-int", 10);
    this.expectedProperties.put("property-bool", true);
    this.projectIdProvider = () -> "projectid";
    this.credentialsProvider = () -> mock(Credentials.class);
    this.parameterManagerClient = mock(ParameterManagerClient.class);
  }

  @Test
  void locateReturnsMapPropertySourceJson() throws Exception {
    RenderParameterVersionResponse version = RenderParameterVersionResponse.newBuilder()
        .setRenderedPayload(
            ByteString.copyFromUtf8("{\"property-int\": 10, \"property-bool\": true}"))
        .build();
    when(this.parameterManagerClient.renderParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            RenderParameterVersionResponse.newBuilder()
                .setRenderedPayload(
                    ByteString.copyFromUtf8("{\"property-int\": 10, \"property-bool\": true}"))
                .build());

    this.googleParameterPropertySourceLocator =
        spy(
            new GoogleParameterPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParameterPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParameterPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(propertySource.getProperty("property-int")).isEqualTo(10);
    assertThat((Boolean) propertySource.getProperty("property-bool")).isTrue();
    assertThat(this.googleParameterPropertySourceLocator.getProjectId()).isEqualTo("projectid");
  }

  @Test
  void locateReturnsMapPropertySourceUnformatted() throws Exception {
    RenderParameterVersionResponse version = RenderParameterVersionResponse.newBuilder()
        .setRenderedPayload(
            ByteString.copyFromUtf8("This is unformatted payload"))
        .build();
    when(this.parameterManagerClient.renderParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            RenderParameterVersionResponse.newBuilder()
                .setRenderedPayload(
                    ByteString.copyFromUtf8("This is unformatted payload"))
                .build());

    this.googleParameterPropertySourceLocator =
        spy(
            new GoogleParameterPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParameterPropertySourceLocator)
        .getRemoteEnvironment();
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> this.googleParameterPropertySourceLocator.locate(new StandardEnvironment()))
        .withMessageContaining("Error loading configuration");
  }

  @Test
  void locateReturnsMapPropertySource_disabled() throws Exception {
    RenderParameterVersionResponse version = RenderParameterVersionResponse.newBuilder()
        .setRenderedPayload(
            ByteString.copyFromUtf8("get after it."))
        .build();
    when(this.gcpParameterProperties.isEnabled()).thenReturn(false);
    when(this.parameterManagerClient.renderParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            RenderParameterVersionResponse.newBuilder()
                .setRenderedPayload(
                    ByteString.copyFromUtf8("get after it."))
                .build());

    // Configure the behavior of the mock objects
    this.googleParameterPropertySourceLocator =
        spy(
            new GoogleParameterPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParameterPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParameterPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(((MapPropertySource) propertySource).getPropertyNames()).isEmpty();
  }

  @Test
  void locateReturnsMapPropertySourceYaml() throws Exception {
    RenderParameterVersionResponse version = RenderParameterVersionResponse.newBuilder()
        .setRenderedPayload(
            ByteString.copyFromUtf8("property-int: 10\nproperty-bool: true\nnested_property:\n   nested_int: 5"))
        .build();
    when(this.parameterManagerClient.renderParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            RenderParameterVersionResponse.newBuilder()
                .setRenderedPayload(
                    ByteString.copyFromUtf8("property-int: 10\nproperty-bool: true\nnested_property:\n   nested_int: 5"))
                .build());
    Map<String, Object> expectedMap = Map.of("nested_int", 5);

    this.googleParameterPropertySourceLocator =
        spy(
            new GoogleParameterPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParameterPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParameterPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(propertySource.getProperty("property-int")).isEqualTo(10);
    assertThat(propertySource.getProperty("nested_property")).isEqualTo(expectedMap);
    assertThat((Boolean) propertySource.getProperty("property-bool")).isTrue();
    assertThat(this.googleParameterPropertySourceLocator.getProjectId()).isEqualTo("projectid");
  }

  @Test
  void disabledPropertySourceReturnsNull() throws Exception {
    when(this.gcpParameterProperties.isEnabled()).thenReturn(false);
    this.googleParameterPropertySourceLocator =
        spy(
            new GoogleParameterPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient));
    this.googleParameterPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParameterPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void disabledPropertySourceAvoidChecks() throws Exception {
    when(this.gcpParameterProperties.isEnabled()).thenReturn(false);
    this.googleParameterPropertySourceLocator =
        spy(new GoogleParameterPropertySourceLocator(null, null, this.gcpParameterProperties, this.parameterManagerClient));
    this.googleParameterPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParameterPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void testProjectIdInConfigProperties() throws IOException {
    when(this.gcpParameterProperties.getProjectId()).thenReturn("pariah");
    this.googleParameterPropertySourceLocator =
        new GoogleParameterPropertySourceLocator(
            this.projectIdProvider, this.credentialsProvider, this.gcpParameterProperties, this.parameterManagerClient);

    assertThat(this.googleParameterPropertySourceLocator.getProjectId()).isEqualTo("pariah");
  }
}
