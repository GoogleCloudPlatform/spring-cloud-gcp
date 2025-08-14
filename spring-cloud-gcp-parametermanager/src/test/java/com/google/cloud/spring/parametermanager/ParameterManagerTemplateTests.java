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

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.parametermanager.v1.CreateParameterRequest;
import com.google.cloud.parametermanager.v1.CreateParameterVersionRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterVersionRequest;
import com.google.cloud.parametermanager.v1.GetParameterRequest;
import com.google.cloud.parametermanager.v1.GetParameterVersionRequest;
import com.google.cloud.parametermanager.v1.LocationName;
import com.google.cloud.parametermanager.v1.Parameter;
import com.google.cloud.parametermanager.v1.ParameterFormat;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterName;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
import com.google.cloud.parametermanager.v1.RenderParameterVersionResponse;
import com.google.cloud.parametermanager.v1.UpdateParameterVersionRequest;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.FieldMaskUtil;
import io.grpc.Status;
import java.lang.reflect.Method;
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
        ParameterFormat.JSON);
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
        this.parameterManagerTemplate.getParameterString(
            "pm@global/" + parameterId + "/" + versionId);
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
        this.parameterManagerTemplate.getParameterBytes(
            "pm@global/" + parameterId + "/" + versionId);
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

  @Test
  void testGetRenderedParameterByteString_withParameterIdentifier() {
    String parameterIdentifier = "pm@global/my-parameter/v1";
    ByteString expectedData = ByteString.copyFromUtf8("rendered parameter data");

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(expectedData);

    when(client.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    byte[] result = this.parameterManagerTemplate.renderedParameterVersionBytes(parameterIdentifier);

    assertThat(result).isEqualTo(expectedData.toByteArray());
  }

  @Test
  void testGetRenderedParameterByteString_withParameterVersionName() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "my-parameter", "v1");
    ByteString expectedData = ByteString.copyFromUtf8("rendered parameter data");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(expectedData);

    when(client.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    ByteString result = invokeGetRenderedParameterByteString(parameterVersionName);

    assertThat(result).isEqualTo(expectedData);
  }

  @Test
  void testGetRenderedParameterByteString_notFoundAndDefaultNotAllowed() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(client.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(false);

    assertThatThrownBy(() -> invokeGetRenderedParameterByteString(parameterVersionName))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testGetRenderedParameterByteString_notFoundAndDefaultAllowed() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(client.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(true);

    ByteString result = invokeGetRenderedParameterByteString(parameterVersionName);

    assertThat(result).isNull();
  }

  @Test
  void testParameterExists_withLocationAndParameterId() {
    String locationId = "global";
    String parameterId = "my-parameter";
    String projectId = "my-project";

    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);

    when(client.getParameter(parameterName)).thenReturn(Parameter.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterExists(locationId, parameterId);

    assertThat(result).isTrue();
    verify(client).getParameter(parameterName);
  }

  @Test
  void testParameterExists_withProjectLocationAndParameterId() {
    String locationId = "global";
    String parameterId = "my-parameter";
    String projectId = "my-project";

    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);

    when(client.getParameter(parameterName)).thenReturn(Parameter.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterExists(projectId, locationId, parameterId);

    assertThat(result).isTrue();
    verify(client).getParameter(parameterName);
  }

  @Test
  void testParameterVersionExists_withLocationParameterAndVersionId() {
    String locationId = "global";
    String parameterId = "my-parameter";
    String versionId = "v1";
    String projectId = "my-project";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        projectId, locationId, parameterId, versionId);
    GetParameterVersionRequest request = GetParameterVersionRequest.newBuilder()
        .setName(parameterVersionName.toString())
        .build();

    when(client.getParameterVersion(request)).thenReturn(ParameterVersion.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterVersionExists(locationId, parameterId, versionId);

    assertThat(result).isTrue();
    verify(client).getParameterVersion(request);
  }

  @Test
  void testParameterVersionExists_withProjectLocationParameterAndVersionId() {
    String projectId = "custom-project";
    String locationId = "global";
    String parameterId = "my-parameter";
    String versionId = "v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        projectId, locationId, parameterId, versionId);
    GetParameterVersionRequest request = GetParameterVersionRequest.newBuilder()
        .setName(parameterVersionName.toString())
        .build();

    when(client.getParameterVersion(request)).thenReturn(ParameterVersion.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterVersionExists(
        projectId, locationId, parameterId, versionId);

    assertThat(result).isTrue();
    verify(client).getParameterVersion(request);
  }

  @Test
  void testRenderedParameterVersionString() {
    String parameterIdentifier = "pm@global/my-parameter/v1";
    String expectedValue = "test-value";
    ByteString byteString = ByteString.copyFromUtf8(expectedValue);

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(byteString);

    when(client.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    String result = parameterManagerTemplate.renderedParameterVersionString(parameterIdentifier);

    assertThat(result).isEqualTo(expectedValue);
  }

  @Test
  void testRenderedParameterVersionString_returnsNullWhenByteStringIsNull() {
    String parameterIdentifier = "pm@global/non-existent-parameter/v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(client.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    parameterManagerTemplate.setAllowDefaultParameterValue(true);

    String result = parameterManagerTemplate.renderedParameterVersionString(parameterIdentifier);

    assertThat(result).isNull();
  }

  @Test
  void testRenderedParameterVersionBytes() {
    String parameterIdentifier = "pm@global/my-parameter/v1";
    String valueString = "test-value";
    ByteString byteString = ByteString.copyFromUtf8(valueString);
    byte[] expectedBytes = byteString.toByteArray();

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(byteString);

    when(client.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    byte[] result = parameterManagerTemplate.renderedParameterVersionBytes(parameterIdentifier);

    assertThat(result).isEqualTo(expectedBytes);
  }

  @Test
  void testRenderedParameterVersionBytes_returnsNullWhenByteStringIsNull() {
    String parameterIdentifier = "pm@global/non-existent-parameter/v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "global", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(client.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    parameterManagerTemplate.setAllowDefaultParameterValue(true);

    byte[] result = parameterManagerTemplate.renderedParameterVersionBytes(parameterIdentifier);

    assertThat(result).isNull();
  }

  private ByteString invokeGetRenderedParameterByteString(ParameterVersionName parameterVersionName) {
    try {
      Method method = ParameterManagerTemplate.class.getDeclaredMethod(
          "getRenderedParameterByteString", ParameterVersionName.class);
      method.setAccessible(true);
      return (ByteString) method.invoke(parameterManagerTemplate, parameterVersionName);
    } catch (Exception e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      throw new RuntimeException("Failed to invoke getRenderedParameterByteString", e);
    }
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
