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

package com.google.cloud.spring.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ParameterManagerPropertySourceTests {

  private static final String PROPERTY_SOURCE_NAME = "test-property-source";
  private static final String TEST_PROJECT_ID = "test-project";

  @Mock
  private ParameterManagerTemplate parameterManagerTemplate;

  @Mock
  private GcpProjectIdProvider projectIdProvider;

  private ParameterManagerPropertySource propertySource;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(projectIdProvider.getProjectId()).thenReturn(TEST_PROJECT_ID);
    propertySource = new ParameterManagerPropertySource(
        PROPERTY_SOURCE_NAME, parameterManagerTemplate, projectIdProvider);
  }

  @Test
  void testGetProperty_withValidParameter() {
    // Arrange
    String propertyName = "pm@global/test-parameter/v1";
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        TEST_PROJECT_ID, "global", "test-parameter", "v1");
    ByteString expectedValue = ByteString.copyFromUtf8("test-value");

    // Use the specific ParameterVersionName overload to avoid ambiguity
    when(parameterManagerTemplate.getRenderedParameterByteString(any(ParameterVersionName.class)))
        .thenReturn(expectedValue);

    // Act
    Object result = propertySource.getProperty(propertyName);

    // Assert
    assertThat(result).isEqualTo(expectedValue);
  }

  @Test
  void testGetProperty_withNonParameterProperty() {
    // Arrange
    String propertyName = "spring.application.name";

    // Act
    Object result = propertySource.getProperty(propertyName);

    // Assert
    assertThat(result).isNull();
    verify(parameterManagerTemplate, never()).getRenderedParameterByteString(any(ParameterVersionName.class));
  }

  @Test
  void testGetPropertyNames_returnsEmptyArray() {
    // Act
    String[] propertyNames = propertySource.getPropertyNames();

    // Assert
    assertThat(propertyNames).isNotNull().isEmpty();
  }
}
