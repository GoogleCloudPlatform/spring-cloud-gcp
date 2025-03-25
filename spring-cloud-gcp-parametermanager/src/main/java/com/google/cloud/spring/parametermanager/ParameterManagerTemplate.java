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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.parametermanager.v1.CreateParameterRequest;
import com.google.cloud.parametermanager.v1.CreateParameterVersionRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterRequest;
import com.google.cloud.parametermanager.v1.DeleteParameterVersionRequest;
import com.google.cloud.parametermanager.v1.GetParameterVersionRequest;
import com.google.cloud.parametermanager.v1.LocationName;
import com.google.cloud.parametermanager.v1.Parameter;
import com.google.cloud.parametermanager.v1.ParameterFormat;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterName;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
import com.google.cloud.parametermanager.v1.UpdateParameterVersionRequest;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.FieldMaskUtil;
import javax.annotation.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Offers convenience methods for performing common operations on Parameter Manager including
 * creating and reading parameters.
 */
public class ParameterManagerTemplate implements ParameterManagerOperations {

  private static final Log LOGGER = LogFactory.getLog(ParameterManagerTemplate.class);
  private final ParameterManagerClient parameterManagerClient;
  private final GcpProjectIdProvider projectIdProvider;

  /** Define the behavior when accessing a non-existed parameter string/bytes. */
  private boolean allowDefaultParameterValue;

  public ParameterManagerTemplate(
      ParameterManagerClient parameterManagerClient, GcpProjectIdProvider projectIdProvider) {
    this.parameterManagerClient = parameterManagerClient;
    this.projectIdProvider = projectIdProvider;
    this.allowDefaultParameterValue = false;
  }

  public ParameterManagerTemplate setAllowDefaultParameterValue(
      boolean allowDefaultParameterValue) {
    this.allowDefaultParameterValue = allowDefaultParameterValue;
    return this;
  }

  public String getProjectId() {
    return projectIdProvider.getProjectId();
  }

  @Override
  public void createParameter(
      String locationId, String parameterId, String versionId, String payload) {
    createNewParameterVersion(
        projectIdProvider.getProjectId(),
        locationId,
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        ParameterFormat.UNFORMATTED);
  }

  @Override
  public void createParameter(
      String projectId, String locationId, String parameterId, String versionId, String payload) {
    createNewParameterVersion(
        projectId,
        locationId,
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        ParameterFormat.UNFORMATTED);
  }

  @Override
  public void createParameter(
      String locationId, String parameterId, String versionId, String payload, ParameterFormat format) {
    createNewParameterVersion(
        projectIdProvider.getProjectId(),
        locationId,
        parameterId,
        versionId,
        ByteString.copyFromUtf8(payload),
        format);
  }

  @Override
  public void createParameter(
      String projectId,
      String locationId,
      String parameterId,
      String versionId,
      String payload,
      ParameterFormat format
      ) {
    createNewParameterVersion(
        projectId, locationId, parameterId, versionId, ByteString.copyFromUtf8(payload), format);
  }

  @Override
  public void createParameter(
      String locationId, String parameterId, String versionId, byte[] payload) {
    createNewParameterVersion(
        projectIdProvider.getProjectId(),
        locationId,
        parameterId,
        versionId,
        ByteString.copyFrom(payload),
        ParameterFormat.UNFORMATTED);
  }

  @Override
  public void createParameter(
      String locationId,
      String parameterId,
      String versionId,
      byte[] payload,
      ParameterFormat format
      ) {
    createNewParameterVersion(
        projectIdProvider.getProjectId(),
        locationId,
        parameterId,
        versionId,
        ByteString.copyFrom(payload),
        format);
  }

  @Override
  public void createParameter(
      String projectId,
      String locationId,
      String parameterId,
      String versionId,
      byte[] payload,
      ParameterFormat format
      ) {
    createNewParameterVersion(
        projectId, locationId, parameterId, versionId, ByteString.copyFrom(payload), format);
  }

  @Override
  public void enableParameterVersion(String locationId, String parameterId, String versionId) {
    enableParameterVersion(projectIdProvider.getProjectId(), locationId, parameterId, versionId);
  }

  @Override
  public void enableParameterVersion(
      String projectId, String locationId, String parameterId, String versionId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    ParameterVersion parameterVersion =
        ParameterVersion.newBuilder()
            .setName(parameterVersionName.toString())
            .setDisabled(false)
            .build();
    UpdateParameterVersionRequest request =
        UpdateParameterVersionRequest.newBuilder()
            .setParameterVersion(parameterVersion)
            .setUpdateMask(FieldMaskUtil.fromString("disabled"))
            .build();
    this.parameterManagerClient.updateParameterVersion(request);
  }

  @Override
  public void disableParameterVersion(String locationId, String parameterId, String versionId) {
    disableParameterVersion(projectIdProvider.getProjectId(), locationId, parameterId, versionId);
  }

  @Override
  public void disableParameterVersion(
      String projectId, String locationId, String parameterId, String versionId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    ParameterVersion parameterVersion =
        ParameterVersion.newBuilder()
            .setName(parameterVersionName.toString())
            .setDisabled(true)
            .build();
    UpdateParameterVersionRequest request =
        UpdateParameterVersionRequest.newBuilder()
            .setParameterVersion(parameterVersion)
            .setUpdateMask(FieldMaskUtil.fromString("disabled"))
            .build();
    this.parameterManagerClient.updateParameterVersion(request);
  }

  @Override
  public void deleteParameter(String locationId, String parameterId) {
    deleteParameter(projectIdProvider.getProjectId(), locationId, parameterId);
  }

  @Override
  public void deleteParameter(String projectId, String locationId, String parameterId) {
    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    DeleteParameterRequest request =
        DeleteParameterRequest.newBuilder().setName(parameterName.toString()).build();
    this.parameterManagerClient.deleteParameter(request);
  }

  @Override
  public void deleteParameterVersion(String locationId, String parameterId, String versionId) {
    deleteParameterVersion(projectIdProvider.getProjectId(), locationId, parameterId, versionId);
  }

  @Override
  public void deleteParameterVersion(
      String projectId, String locationId, String parameterId, String versionId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    DeleteParameterVersionRequest request =
        DeleteParameterVersionRequest.newBuilder().setName(parameterVersionName.toString()).build();
    this.parameterManagerClient.deleteParameterVersion(request);
  }

  @Override
  public boolean parameterExists(String locationId, String parameterId) {
    return parameterExists(projectIdProvider.getProjectId(), locationId, parameterId);
  }

  @Override
  public boolean parameterExists(String projectId, String locationId, String parameterId) {
    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    try {
      this.parameterManagerClient.getParameter(parameterName);
    } catch (NotFoundException e) {
      return false;
    }
    return true;
  }

  @Override
  public boolean parameterVersionExists(String locationId, String parameterId, String versionId) {
    return parameterVersionExists(
        projectIdProvider.getProjectId(), locationId, parameterId, versionId);
  }

  @Override
  public boolean parameterVersionExists(
      String projectId, String locationId, String parameterId, String versionId) {
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(projectId, locationId, parameterId, versionId);
    GetParameterVersionRequest request =
        GetParameterVersionRequest.newBuilder().setName(parameterVersionName.toString()).build();
    try {
      this.parameterManagerClient.getParameterVersion(request);
    } catch (NotFoundException e) {
      return false;
    }
    return true;
  }

  @Override
  @Nullable
  public String getParameterString(String parameterIdentifier) {
    ByteString parameterByteString = getParameterByteString(parameterIdentifier);
    return parameterByteString == null ? null : parameterByteString.toStringUtf8();
  }

  @Override
  @Nullable
  public byte[] getParameterBytes(String parameterIdentifier) {
    ByteString parameterByteString = getParameterByteString(parameterIdentifier);
    return parameterByteString == null ? null : parameterByteString.toByteArray();
  }

  @Override
  @Nullable
  public String getRenderedParameterString(String parameterIdentifier) {
    ByteString renderedParameterByteString = getRenderedParameterByteString(parameterIdentifier);
    return renderedParameterByteString == null ? null : renderedParameterByteString.toStringUtf8();
  }

  @Override
  @Nullable
  public byte[] getRenderedParameterBytes(String parameterIdentifier) {
    ByteString renderedParameterByteString = getRenderedParameterByteString(parameterIdentifier);
    return renderedParameterByteString == null ? null : renderedParameterByteString.toByteArray();
  }

  ByteString getParameterByteString(String parameterIdentifier) {
    ParameterVersionName parameterVersionName =
        ParameterManagerPropertyUtils.getParameterVersionName(
            parameterIdentifier, projectIdProvider);
    return getParameterByteString(parameterVersionName);
  }

  ByteString getParameterByteString(ParameterVersionName parameterVersionName) {
    ByteString parameterData;
    try {
      parameterData =
          parameterManagerClient.getParameterVersion(parameterVersionName).getPayload().getData();
    } catch (NotFoundException ex) {
      LOGGER.warn(parameterVersionName.toString() + " doesn't exist in Parameter Manager.");
      if (!this.allowDefaultParameterValue) {
        throw ex;
      }
      // If no parameter is found in Parameter Manager and default parameter is allowed,
      // returns null rather than throwing the exception to facilitate default
      // parameter value parsing.
      return null;
    }
    return parameterData;
  }

  ByteString getRenderedParameterByteString(String parameterIdentifier) {
    ParameterVersionName parameterVersionName =
        ParameterManagerPropertyUtils.getParameterVersionName(
            parameterIdentifier, projectIdProvider);
    return getRenderedParameterByteString(parameterVersionName);
  }

  ByteString getRenderedParameterByteString(ParameterVersionName parameterVersionName) {
    ByteString parameterData;
    try {
      parameterData =
          parameterManagerClient.renderParameterVersion(parameterVersionName).getRenderedPayload();
    } catch (NotFoundException ex) {
      LOGGER.warn(parameterVersionName.toString() + " doesn't exist in Parameter Manager.");
      if (!this.allowDefaultParameterValue) {
        throw ex;
      }
      // If no parameter is found in Parameter Manager and default parameter is allowed,
      // returns null rather than throwing the exception to facilitate default
      // parameter value parsing.
      return null;
    }
    return parameterData;
  }

  /**
   * Creates a new version of parameter version with the provided {@code payload} under a {@link
   * Parameter}. Will also create a new {@link Parameter} if one does not exist with the provided
   * {@code parameterId}.
   */
  private void createNewParameterVersion(
      String projectId,
      String locationId,
      String parameterId,
      String versionId,
      ByteString payload,
      ParameterFormat format) {
    if (!parameterExists(projectId, locationId, parameterId)) {
      createParameterInternal(projectId, locationId, parameterId, format);
    }

    ParameterName parameterName = ParameterName.of(projectId, locationId, parameterId);
    ParameterVersionPayload parameterVersionPayload =
        ParameterVersionPayload.newBuilder().setData(payload).build();
    CreateParameterVersionRequest payloadRequest =
        CreateParameterVersionRequest.newBuilder()
            .setParent(parameterName.toString())
            .setParameterVersionId(versionId)
            .setParameterVersion(
                ParameterVersion.newBuilder().setPayload(parameterVersionPayload).build())
            .build();
    this.parameterManagerClient.createParameterVersion(payloadRequest);
  }

  /**
   * Creates a new {@link Parameter} with the provided {@code format} for the GCP Project under
   * specified {@code locationId}.
   *
   * <p>Note that the {@link Parameter} object does not contain the parameter version payload. You
   * must create versions of the parameter which stores the payload of the parameter.
   */
  private void createParameterInternal(
      String projectId, String locationId, String parameterId, ParameterFormat format) {
    LocationName locationName = LocationName.of(projectId, locationId);

    Parameter parameter = Parameter.newBuilder().setFormat(format).build();

    CreateParameterRequest request =
        CreateParameterRequest.newBuilder()
            .setParent(locationName.toString())
            .setParameterId(parameterId)
            .setParameter(parameter)
            .build();
    this.parameterManagerClient.createParameter(request);
  }
}
