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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.junit.jupiter.api.Test;

class ParameterManagerPropertyUtilsTests {
  private static final GcpProjectIdProvider DEFAULT_PROJECT_ID_PROVIDER = () -> "defaultProject";

  @Test
  void testNonParameter() {
    String property = "sprint.cloud.datasource";
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(
            property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(parameterIdentifier).isNull();
  }

  @Test
  void testInvalidParameterFormat_missingLocationId() {
    String property = "pm@";

    assertThatThrownBy(
        () ->
            ParameterManagerPropertyUtils.getParameterVersionName(
                property, DEFAULT_PROJECT_ID_PROVIDER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Unrecognized format for specifying a GCP Parameter Manager parameter:");
  }

  @Test
  void testInvalidParameterFormat_missingParameterId() {
    String property = "pm@global/ / ";

    assertThatThrownBy(
            () ->
                ParameterManagerPropertyUtils.getParameterVersionName(
                    property, DEFAULT_PROJECT_ID_PROVIDER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "The GCP Parameter Manager parameter id must not be empty:");
  }

  @Test
  void testInvalidParameterFormat_missingVersionId() {
    String property = "pm@global/the-parameter/ ";

    assertThatThrownBy(
            () ->
                ParameterManagerPropertyUtils.getParameterVersionName(
                    property, DEFAULT_PROJECT_ID_PROVIDER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The GCP Parameter Manager parameter version must not be empty:");
  }

  @Test
  void testShortProperty_locationParameterVersionId() {
    String property = "pm@my-location/the-parameter/v3";
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(
            property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(parameterIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(parameterIdentifier.getLocation()).isEqualTo("my-location");
    assertThat(parameterIdentifier.getParameter()).isEqualTo("the-parameter");
    assertThat(parameterIdentifier.getParameterVersion()).isEqualTo("v3");
  }

  @Test
  void testShortProperty_projectLocationParameterVersionId() {
    String property = "pm@my-project/my-location/the-parameter/v2";
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(
            property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(parameterIdentifier.getProject()).isEqualTo("my-project");
    assertThat(parameterIdentifier.getLocation()).isEqualTo("my-location");
    assertThat(parameterIdentifier.getParameter()).isEqualTo("the-parameter");
    assertThat(parameterIdentifier.getParameterVersion()).isEqualTo("v2");
  }

  @Test
  void testLongProperty_locationParameterVersionId() {
    String property = "pm@locations/my-location/parameters/the-parameter/versions/v1";
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(
            property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(parameterIdentifier.getProject()).isEqualTo("defaultProject");
    assertThat(parameterIdentifier.getLocation()).isEqualTo("my-location");
    assertThat(parameterIdentifier.getParameter()).isEqualTo("the-parameter");
    assertThat(parameterIdentifier.getParameterVersion()).isEqualTo("v1");
  }

  @Test
  void testLongProperty_projectLocationParameterVersionId() {
    String property =
        "pm@projects/my-project/locations/my-location/parameters/the-parameter/versions/v1";
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(
            property, DEFAULT_PROJECT_ID_PROVIDER);

    assertThat(parameterIdentifier.getProject()).isEqualTo("my-project");
    assertThat(parameterIdentifier.getLocation()).isEqualTo("my-location");
    assertThat(parameterIdentifier.getParameter()).isEqualTo("the-parameter");
    assertThat(parameterIdentifier.getParameterVersion()).isEqualTo("v1");
  }
}
