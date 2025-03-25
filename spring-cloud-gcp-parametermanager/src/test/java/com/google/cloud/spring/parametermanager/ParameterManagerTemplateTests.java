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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.parametermanager.v1.CreateParameterRequest;
import com.google.cloud.parametermanager.v1.CreateParameterVersionRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterVersionRequest;
import com.google.cloud.parametermanager.v1.LocationName;
import com.google.cloud.parametermanager.v1.Parameter;
import com.google.cloud.parametermanager.v1.ParameterFormat;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterName;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
import com.google.cloud.parametermanager.v1.UpdateParameterVersionRequest;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.FieldMaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterManagerTemplateTests {

  private ParameterManagerClient client;
  private ParameterManagerTemplate parameterManagerTemplate;

  @BeforeEach
  void setupMocks() {
    this.client = mock(ParameterManagerClient.class);
    when(this.client.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("get after it."))
                        .build())
                .build());
    this.parameterManagerTemplate = new ParameterManagerTemplate(this.client, () -> "my-project");
  }

  @Test
  void testProjectId() {
    assertThat(this.parameterManagerTemplate.getProjectId()).isEqualTo("my-project");
  }

  @Test
  void testCreateParameterIfMissing() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "global", parameterId, versionId, payload, ParameterFormat.JSON);

    verifyCreateParameterRequest(parameterId, ParameterFormat.JSON, "my-project", "global");

    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "global");
  }

  @Test
  void testCreateParameterIfMissing_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);
    this.parameterManagerTemplate.createParameter(
        "custom-location", parameterId, versionId, payload, ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "my-project", "custom-location");
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "custom-location");
  }

  @Test
  void testCreateParameterIfMissing_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "custom-project", "custom-location", parameterId, versionId, payload, ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "custom-project", "custom-location");

    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "custom-location");
  }

  @Test
  void testCreateParameterIfAlreadyPresent() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(ParameterName.of("my-project", "global", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "global", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.client).getParameter(ParameterName.of("my-project", "global", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "global");
  }

  @Test
  void testCreateParameterIfAlreadyPresent_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(ParameterName.of("my-project", "my-location", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "my-location", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.client).getParameter(ParameterName.of("my-project", "my-location", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "my-location");
  }

  @Test
  void testCreateParameterIfAlreadyPresent_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(
            ParameterName.of("custom-project", "custom-location", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "custom-project", "custom-location", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.client)
        .getParameter(ParameterName.of("custom-project", "custom-location", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "custom-location");
  }

  @Test
  void testCreateByteParameterIfMissing() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "global", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);

    verifyCreateParameterRequest(parameterId, ParameterFormat.JSON, "my-project", "global");

    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "global");
  }

  @Test
  void testCreateByteParameterIfMissing_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);
    this.parameterManagerTemplate.createParameter(
        "custom-location", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "my-project", "custom-location");
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "custom-location");
  }

  @Test
  void testCreateByteParameterIfMissing_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.client.getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "custom-project",
        "custom-location",
        parameterId,
        versionId,
        payload.getBytes(),
        ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "custom-project", "custom-location");

    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "custom-location");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(ParameterName.of("my-project", "global", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "global", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verify(this.client).getParameter(ParameterName.of("my-project", "global", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "global");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(ParameterName.of("my-project", "my-location", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "my-location", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verify(this.client).getParameter(ParameterName.of("my-project", "my-location", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "my-location");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.client.getParameter(
            ParameterName.of("custom-project", "custom-location", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "custom-project",
        "custom-location",
        parameterId,
        versionId,
        payload.getBytes(),
        ParameterFormat.JSON
        );
    verify(this.client)
        .getParameter(ParameterName.of("custom-project", "custom-location", parameterId));
    verify(this.client, never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "custom-location");
  }

  @Test
  void testAccessParameterString() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String expectedValue = "get after it.";

    ParameterVersion parameterVersion =
        ParameterVersion.newBuilder()
            .setPayload(
                ParameterVersionPayload.newBuilder()
                    .setData(ByteString.copyFromUtf8(expectedValue))
                    .build())
            .build();
    when(this.client.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(parameterVersion);

    String result =
        this.parameterManagerTemplate.getParameterString("pm@global/" + parameterId + "/" + versionId);
    verify(this.client).getParameterVersion(any(ParameterVersionName.class));
    assertThat(result).isEqualTo(expectedValue);
  }

  @Test
  void testAccessParameterBytes() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String expectedValue = "get after it.";

    ParameterVersion parameterVersion =
        ParameterVersion.newBuilder()
            .setPayload(
                ParameterVersionPayload.newBuilder()
                    .setData(ByteString.copyFromUtf8(expectedValue))
                    .build())
            .build();
    when(this.client.getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(parameterVersion);

    byte[] result =
        this.parameterManagerTemplate.getParameterBytes("pm@global/" + parameterId + "/" + versionId);
    verify(this.client).getParameterVersion(any(ParameterVersionName.class));
    assertThat(result).isEqualTo(expectedValue.getBytes());
  }

  @Test
  void testAccessNonExistentParameterStringWhenDefaultIsNotAllowed() {
    when(this.client.getParameterVersion(any(ParameterVersionName.class)))
        .thenThrow(NotFoundException.class);
    assertThatThrownBy(
            () -> this.parameterManagerTemplate.getParameterString("pm@global/fake-parameter/v1"))
        .isExactlyInstanceOf(NotFoundException.class);
  }

  @Test
  void testAccessNonExistentParameterStringWhenDefaultIsAllowed() {
    when(this.client.getParameterVersion(any(ParameterVersionName.class)))
        .thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(true);
    String result = this.parameterManagerTemplate.getParameterString("pm@global/fake-parameter/v1");
    assertThat(result).isNull();
  }

  @Test
  void testDeleteParameter() {
    String parameterId = "my-parameter";

    this.parameterManagerTemplate.deleteParameter("my-location", parameterId);
    verifyDeleteParameterRequest(parameterId, "my-project", "my-location");

    this.parameterManagerTemplate.deleteParameter("custom-project", "custom-location", parameterId);
    verifyDeleteParameterRequest(parameterId, "custom-project", "custom-location");
  }

  @Test
  void testDeleteParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.deleteParameterVersion("my-location", parameterId, versionId);
    verifyDeleteParameterVersionRequest(parameterId, versionId, "my-project", "my-location");

    this.parameterManagerTemplate.deleteParameterVersion(
        "custom-project", "custom-location", parameterId, versionId);
    verifyDeleteParameterVersionRequest(
        parameterId, versionId, "custom-project", "custom-location");
  }

  @Test
  void testEnableParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.enableParameterVersion("custom-location", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "my-project", "custom-location", false);

    this.parameterManagerTemplate.enableParameterVersion(
        "custom-project", "custom-location", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "custom-project", "custom-location", false);
  }

  @Test
  void testDisableParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.disableParameterVersion("global", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "my-project", "global", true);

    this.parameterManagerTemplate.disableParameterVersion(
        "custom-location", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "my-project", "custom-location", true);

    this.parameterManagerTemplate.disableParameterVersion(
        "custom-project", "custom-location", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "custom-project", "custom-location", true);
  }

  private void verifyCreateParameterRequest(
      String parameterId, ParameterFormat format, String projectId, String locationId) {
    LocationName locationName = LocationName.of(projectId, locationId);
    Parameter parameter = Parameter.newBuilder().setFormat(format).build();

    CreateParameterRequest request =
        CreateParameterRequest.newBuilder()
            .setParent(locationName.toString())
            .setParameterId(parameterId)
            .setParameter(parameter)
            .build();
    verify(this.client).createParameter(request);
  }

  private void verifyCreateParameterVersionRequest(
      String parameterId,
      String versionId,
      ByteString payload,
      String projectId,
      String locationId) {
    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    CreateParameterVersionRequest parameterVersionRequest =
        CreateParameterVersionRequest.newBuilder()
            .setParent(parameterName.toString())
            .setParameterVersionId(versionId)
            .setParameterVersion(
                ParameterVersion.newBuilder()
                    .setPayload(ParameterVersionPayload.newBuilder().setData(payload).build())
                    .build())
            .build();
    verify(this.client).createParameterVersion(parameterVersionRequest);
  }

  private void verifyDeleteParameterRequest(
      String parameterId, String projectId, String locationId) {
    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    DeleteParameterRequest request =
        DeleteParameterRequest.newBuilder().setName(parameterName.toString()).build();
    verify(this.client).deleteParameter(request);
  }

  private void verifyDeleteParameterVersionRequest(
      String parameterId, String versionId, String projectId, String locationId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    DeleteParameterVersionRequest request =
        DeleteParameterVersionRequest.newBuilder().setName(parameterVersionName.toString()).build();
    verify(this.client).deleteParameterVersion(request);
  }

  private void verifyEnableDisableParameterVersionRequest(
      String parameterId, String versionId, String projectId, String locationId, boolean disabled) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    ParameterVersion parameterVersion =
        ParameterVersion.newBuilder()
            .setName(parameterVersionName.toString())
            .setDisabled(disabled)
            .build();
    UpdateParameterVersionRequest request =
        UpdateParameterVersionRequest.newBuilder()
            .setParameterVersion(parameterVersion)
            .setUpdateMask(FieldMaskUtil.fromString("disabled"))
            .build();
    verify(this.client).updateParameterVersion(request);
  }
}
