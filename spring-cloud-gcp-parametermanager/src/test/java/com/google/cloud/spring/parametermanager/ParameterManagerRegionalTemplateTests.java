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

class ParameterManagerRegionalTemplateTests {

  private ParameterManagerClientFactory parameterManagerClientFactory;
  private ParameterManagerTemplate parameterManagerTemplate;
  private ParameterManagerClient parameterManagerClient;

  @BeforeEach
  void setupMocks() {
    this.parameterManagerClient = mock(ParameterManagerClient.class);
    this.parameterManagerClientFactory = mock(ParameterManagerClientFactory.class);
    when(parameterManagerClientFactory.getClient("us-central1"))
        .thenReturn(parameterManagerClient);
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("get after it."))
                        .build())
                .build());
    this.parameterManagerTemplate = new ParameterManagerTemplate(this.parameterManagerClientFactory, () -> "my-project");
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

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);

    verifyCreateParameterRequest(parameterId, ParameterFormat.JSON, "my-project", "us-central1");

    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateParameterIfMissing_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "my-project", "us-central1");
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateParameterIfMissing_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "custom-project", "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "custom-project", "us-central1");

    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "us-central1");
  }

  @Test
  void testCreateParameterIfAlreadyPresent() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(ParameterName.of("my-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameter(ParameterName.of("my-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateParameterIfAlreadyPresent_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(ParameterName.of("my-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameter(ParameterName.of("my-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateParameterIfAlreadyPresent_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(
            ParameterName.of("custom-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "custom-project", "us-central1", parameterId, versionId, payload, ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"))
        .getParameter(ParameterName.of("custom-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "us-central1");
  }

  @Test
  void testCreateByteParameterIfMissing() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);

    verifyCreateParameterRequest(parameterId, ParameterFormat.JSON, "my-project", "us-central1");

    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateByteParameterIfMissing_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "my-project", "us-central1");
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateByteParameterIfMissing_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(any(ParameterName.class))).thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.createParameter(
        "custom-project",
        "us-central1",
        parameterId,
        versionId,
        payload.getBytes(),
        ParameterFormat.JSON);
    verifyCreateParameterRequest(
        parameterId, ParameterFormat.JSON, "custom-project", "us-central1");

    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "us-central1");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(ParameterName.of("my-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameter(ParameterName.of("my-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent_withLocation() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(ParameterName.of("my-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "us-central1", parameterId, versionId, payload.getBytes(), ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameter(ParameterName.of("my-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId, versionId, ByteString.copyFromUtf8(payload), "my-project", "us-central1");
  }

  @Test
  void testCreateByteParameterIfAlreadyPresent_withProject() {
    String parameterId = "my-parameter";
    String versionId = "v1";
    String payload = "{'message': 'hello world!'}";

    // The Parameter "my-parameter" already exists.
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameter(
            ParameterName.of("custom-project", "us-central1", parameterId)))
        .thenReturn(Parameter.getDefaultInstance());

    // Verify that the parameter is not created.
    this.parameterManagerTemplate.createParameter(
        "custom-project",
        "us-central1",
        parameterId,
        versionId,
        payload.getBytes(),
        ParameterFormat.JSON);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"))
        .getParameter(ParameterName.of("custom-project", "us-central1", parameterId));
    verify(this.parameterManagerClientFactory
        .getClient("us-central1"), never()).createParameter(any());
    verifyCreateParameterVersionRequest(
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        "custom-project",
        "us-central1");
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
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(parameterVersion);

    String result =
        this.parameterManagerTemplate.getParameterString(
            "pm@us-central1/" + parameterId + "/" + versionId);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameterVersion(any(ParameterVersionName.class));
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
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameterVersion(any(ParameterVersionName.class)))
        .thenReturn(parameterVersion);

    byte[] result =
        this.parameterManagerTemplate.getParameterBytes(
            "pm@us-central1/" + parameterId + "/" + versionId);
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).getParameterVersion(any(ParameterVersionName.class));
    assertThat(result).isEqualTo(expectedValue.getBytes());
  }

  @Test
  void testAccessNonExistentParameterStringWhenDefaultIsNotAllowed() {
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameterVersion(any(ParameterVersionName.class)))
        .thenThrow(NotFoundException.class);
    assertThatThrownBy(
            () -> this.parameterManagerTemplate.getParameterString("pm@us-central1/fake-parameter/v1"))
        .isExactlyInstanceOf(NotFoundException.class);
  }

  @Test
  void testAccessNonExistentParameterStringWhenDefaultIsAllowed() {
    when(this.parameterManagerClientFactory
        .getClient("us-central1").getParameterVersion(any(ParameterVersionName.class)))
        .thenThrow(NotFoundException.class);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(true);
    String result = this.parameterManagerTemplate.getParameterString("pm@us-central1/fake-parameter/v1");
    assertThat(result).isNull();
  }

  @Test
  void testDeleteParameter() {
    String parameterId = "my-parameter";

    this.parameterManagerTemplate.deleteParameter("us-central1", parameterId);
    verifyDeleteParameterRequest(parameterId, "my-project", "us-central1");

    this.parameterManagerTemplate.deleteParameter("custom-project", "us-central1", parameterId);
    verifyDeleteParameterRequest(parameterId, "custom-project", "us-central1");
  }

  @Test
  void testDeleteParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.deleteParameterVersion("us-central1", parameterId, versionId);
    verifyDeleteParameterVersionRequest(parameterId, versionId, "my-project", "us-central1");

    this.parameterManagerTemplate.deleteParameterVersion(
        "custom-project", "us-central1", parameterId, versionId);
    verifyDeleteParameterVersionRequest(
        parameterId, versionId, "custom-project", "us-central1");
  }

  @Test
  void testEnableParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.enableParameterVersion("us-central1", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "my-project", "us-central1", false);

    this.parameterManagerTemplate.enableParameterVersion(
        "custom-project", "us-central1", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "custom-project", "us-central1", false);
  }

  @Test
  void testDisableParameterVersion() {
    String parameterId = "my-parameter";
    String versionId = "v1";

    this.parameterManagerTemplate.disableParameterVersion(
        "us-central1", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "my-project", "us-central1", true);

    this.parameterManagerTemplate.disableParameterVersion(
        "custom-project", "us-central1", parameterId, versionId);
    verifyEnableDisableParameterVersionRequest(
        parameterId, versionId, "custom-project", "us-central1", true);
  }

  @Test
  void testGetRenderedParameterByteString_withParameterIdentifier() {
    String parameterIdentifier = "pm@us-central1/my-parameter/v1";
    ByteString expectedData = ByteString.copyFromUtf8("rendered parameter data");

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(expectedData);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    byte[] result = this.parameterManagerTemplate.renderedParameterVersionBytes(parameterIdentifier);

    assertThat(result).isEqualTo(expectedData.toByteArray());
  }

  @Test
  void testGetRenderedParameterByteString_withParameterVersionName() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "my-parameter", "v1");
    ByteString expectedData = ByteString.copyFromUtf8("rendered parameter data");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(expectedData);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    ByteString result = invokeGetRenderedParameterByteString(parameterVersionName);

    assertThat(result).isEqualTo(expectedData);
  }

  @Test
  void testGetRenderedParameterByteString_notFoundAndDefaultNotAllowed() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(false);

    assertThatThrownBy(() -> invokeGetRenderedParameterByteString(parameterVersionName))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testGetRenderedParameterByteString_notFoundAndDefaultAllowed() {
    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    this.parameterManagerTemplate.setAllowDefaultParameterValue(true);

    ByteString result = invokeGetRenderedParameterByteString(parameterVersionName);

    assertThat(result).isNull();
  }

  @Test
  void testParameterExists_withLocationAndParameterId() {
    String locationId = "us-central1";
    String parameterId = "my-parameter";
    String projectId = "my-project";

    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);

    when(parameterManagerClient.getParameter(parameterName)).thenReturn(Parameter.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterExists(locationId, parameterId);

    assertThat(result).isTrue();
    verify(parameterManagerClient).getParameter(parameterName);
  }

  @Test
  void testParameterExists_withProjectLocationAndParameterId() {
    String locationId = "us-central1";
    String parameterId = "my-parameter";
    String projectId = "my-project";

    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);

    when(parameterManagerClient.getParameter(parameterName)).thenReturn(Parameter.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterExists(projectId, locationId, parameterId);

    assertThat(result).isTrue();
    verify(parameterManagerClient).getParameter(parameterName);
  }

  @Test
  void testParameterVersionExists_withLocationParameterAndVersionId() {
    String locationId = "us-central1";
    String parameterId = "my-parameter";
    String versionId = "v1";
    String projectId = "my-project";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        projectId, locationId, parameterId, versionId);
    GetParameterVersionRequest request = GetParameterVersionRequest.newBuilder()
        .setName(parameterVersionName.toString())
        .build();

    when(parameterManagerClient.getParameterVersion(request)).thenReturn(ParameterVersion.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterVersionExists(locationId, parameterId, versionId);

    assertThat(result).isTrue();
    verify(parameterManagerClient).getParameterVersion(request);
  }

  @Test
  void testParameterVersionExists_withProjectLocationParameterAndVersionId() {
    String projectId = "custom-project";
    String locationId = "us-central1";
    String parameterId = "my-parameter";
    String versionId = "v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        projectId, locationId, parameterId, versionId);
    GetParameterVersionRequest request = GetParameterVersionRequest.newBuilder()
        .setName(parameterVersionName.toString())
        .build();

    when(parameterManagerClient.getParameterVersion(request)).thenReturn(ParameterVersion.newBuilder().build());

    boolean result = parameterManagerTemplate.parameterVersionExists(
        projectId, locationId, parameterId, versionId);

    assertThat(result).isTrue();
    verify(parameterManagerClient).getParameterVersion(request);
  }

  @Test
  void testRenderedParameterVersionString() {
    String parameterIdentifier = "pm@us-central1/my-parameter/v1";
    String expectedValue = "test-value";
    ByteString byteString = ByteString.copyFromUtf8(expectedValue);

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(byteString);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    String result = parameterManagerTemplate.renderedParameterVersionString(parameterIdentifier);

    assertThat(result).isEqualTo(expectedValue);
  }

  @Test
  void testRenderedParameterVersionString_returnsNullWhenByteStringIsNull() {
    String parameterIdentifier = "pm@us-central1/non-existent-parameter/v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenThrow(notFoundException);

    parameterManagerTemplate.setAllowDefaultParameterValue(true);

    String result = parameterManagerTemplate.renderedParameterVersionString(parameterIdentifier);

    assertThat(result).isNull();
  }

  @Test
  void testRenderedParameterVersionBytes() {
    String parameterIdentifier = "pm@us-central1/my-parameter/v1";
    String valueString = "test-value";
    ByteString byteString = ByteString.copyFromUtf8(valueString);
    byte[] expectedBytes = byteString.toByteArray();

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "my-parameter", "v1");

    RenderParameterVersionResponse mockResponse = mock(RenderParameterVersionResponse.class);
    when(mockResponse.getRenderedPayload()).thenReturn(byteString);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
        .thenReturn(mockResponse);

    byte[] result = parameterManagerTemplate.renderedParameterVersionBytes(parameterIdentifier);

    assertThat(result).isEqualTo(expectedBytes);
  }

  @Test
  void testRenderedParameterVersionBytes_returnsNullWhenByteStringIsNull() {
    String parameterIdentifier = "pm@us-central1/non-existent-parameter/v1";

    ParameterVersionName parameterVersionName = ParameterVersionName.of(
        "my-project", "us-central1", "non-existent-parameter", "v1");

    NotFoundException notFoundException = new NotFoundException(
        "Parameter not found",
        new RuntimeException("Not found"),
        GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND),
        false);

    when(parameterManagerClient.renderParameterVersion(parameterVersionName))
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
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).createParameter(request);
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
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).createParameterVersion(parameterVersionRequest);
  }

  private void verifyDeleteParameterRequest(
      String parameterId, String projectId, String locationId) {
    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    DeleteParameterRequest request =
        DeleteParameterRequest.newBuilder().setName(parameterName.toString()).build();
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).deleteParameter(request);
  }

  private void verifyDeleteParameterVersionRequest(
      String parameterId, String versionId, String projectId, String locationId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    DeleteParameterVersionRequest request =
        DeleteParameterVersionRequest.newBuilder().setName(parameterVersionName.toString()).build();
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).deleteParameterVersion(request);
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
    verify(this.parameterManagerClientFactory
        .getClient("us-central1")).updateParameterVersion(request);
  }
}
