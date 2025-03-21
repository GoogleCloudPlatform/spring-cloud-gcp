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
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
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
 * Tests for the paramconfig property source locator.
 */
class GoogleParamConfigPropertySourceLocatorTest {

  private GcpParamConfigProperties gcpParamConfigProperties;

  private Map<String, Object> expectedProperties;

  private GoogleParamConfigPropertySourceLocator googleParamConfigPropertySourceLocator;

  private GcpProjectIdProvider projectIdProvider;

  private CredentialsProvider credentialsProvider;

  private ParameterManagerClient parameterManagerClient;

  @BeforeEach
  void setUp() {
    this.gcpParamConfigProperties = mock(GcpParamConfigProperties.class);
    when(this.gcpParamConfigProperties.getName()).thenReturn("test");
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(true);
    com.google.cloud.spring.core.Credentials configCredentials =
        mock(com.google.cloud.spring.core.Credentials.class);
    when(this.gcpParamConfigProperties.getCredentials()).thenReturn(configCredentials);
    when(this.gcpParamConfigProperties.getProfile()).thenReturn("default");
    when(this.gcpParamConfigProperties.getLocation()).thenReturn("global");
    this.expectedProperties = new HashMap<>();
    this.expectedProperties.put("property-int", 10);
    this.expectedProperties.put("property-bool", true);
    this.projectIdProvider = () -> "projectid";
    this.credentialsProvider = () -> mock(Credentials.class);
    this.parameterManagerClient = mock(ParameterManagerClient.class);
  }

  @Test
  void locateReturnsMapPropertySourceJson() throws Exception {
    ParameterVersion version = ParameterVersion.newBuilder()
        .setPayload(
            ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("{\"property-int\": 10, \"property-bool\": true}")))
        .build();
    when(this.parameterManagerClient.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("{\"property-int\": 10, \"property-bool\": true}")))
                .build());

    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParamConfigPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(propertySource.getProperty("property-int")).isEqualTo(10);
    assertThat((Boolean) propertySource.getProperty("property-bool")).isTrue();
    assertThat(this.googleParamConfigPropertySourceLocator.getProjectId()).isEqualTo("projectid");
  }

  @Test
  void locateReturnsMapPropertySourceUnformatted() throws Exception {
    ParameterVersion version = ParameterVersion.newBuilder()
        .setPayload(
            ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("This is unformatted payload")))
        .build();
    when(this.parameterManagerClient.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("This is unformatted payload")))
                .build());

    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParamConfigPropertySourceLocator)
        .getRemoteEnvironment();
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment()))
        .withMessageContaining("Error loading configuration");
  }

  @Test
  void locateReturnsMapPropertySource_disabled() throws Exception {
    ParameterVersion version = ParameterVersion.newBuilder()
        .setPayload(
            ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("get after it.")))
        .build();
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    when(this.parameterManagerClient.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("get after it.")))
                .build());

    // Configure the behavior of the mock objects
    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParamConfigPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(((MapPropertySource) propertySource).getPropertyNames()).isEmpty();
  }

  @Test
  void locateReturnsMapPropertySourceYaml() throws Exception {
    ParameterVersion version = ParameterVersion.newBuilder()
        .setPayload(
            ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("property-int: 10\nproperty-bool: true\nnested_property:\n   nested_int: 5")))
        .build();
    when(this.parameterManagerClient.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder().setData(ByteString.copyFromUtf8("property-int: 10\nproperty-bool: true\nnested_property:\n   nested_int: 5")))
                .build());
    Map<String, Object> expectedMap = Map.of("nested_int", 5);

    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient));
    doReturn(version)
        .when(this.googleParamConfigPropertySourceLocator)
        .getRemoteEnvironment();
    PropertySource<?> propertySource =
        this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    assertThat(propertySource.getName()).isEqualTo("spring-cloud-gcp");
    assertThat(propertySource.getProperty("property-int")).isEqualTo(10);
    assertThat(propertySource.getProperty("nested_property")).isEqualTo(expectedMap);
    assertThat((Boolean) propertySource.getProperty("property-bool")).isTrue();
    assertThat(this.googleParamConfigPropertySourceLocator.getProjectId()).isEqualTo("projectid");
  }

  @Test
  void disabledPropertySourceReturnsNull() throws Exception {
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    this.googleParamConfigPropertySourceLocator =
        spy(
            new GoogleParamConfigPropertySourceLocator(
                this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient));
    this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParamConfigPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void disabledPropertySourceAvoidChecks() throws Exception {
    when(this.gcpParamConfigProperties.isEnabled()).thenReturn(false);
    this.googleParamConfigPropertySourceLocator =
        spy(new GoogleParamConfigPropertySourceLocator(null, null, this.gcpParamConfigProperties, this.parameterManagerClient));
    this.googleParamConfigPropertySourceLocator.locate(new StandardEnvironment());
    verify(this.googleParamConfigPropertySourceLocator, never()).getRemoteEnvironment();
  }

  @Test
  void testProjectIdInConfigProperties() throws IOException {
    when(this.gcpParamConfigProperties.getProjectId()).thenReturn("pariah");
    this.googleParamConfigPropertySourceLocator =
        new GoogleParamConfigPropertySourceLocator(
            this.projectIdProvider, this.credentialsProvider, this.gcpParamConfigProperties, this.parameterManagerClient);

    assertThat(this.googleParamConfigPropertySourceLocator.getProjectId()).isEqualTo("pariah");
  }
}
