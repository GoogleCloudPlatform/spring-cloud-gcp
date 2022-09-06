/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.secretmanager;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AddSecretVersionRequest;
import com.google.cloud.secretmanager.v1.CreateSecretRequest;
import com.google.cloud.secretmanager.v1.DeleteSecretRequest;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;
import javax.annotation.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Offers convenience methods for performing common operations on Secret Manager including creating
 * and reading secrets.
 *
 * @since 1.2.2
 */
public class SecretManagerTemplate implements SecretManagerOperations {

  /**
   * Default value for the latest version of the secret.
   */
  public static final String LATEST_VERSION = "latest";

  private static final Log LOGGER = LogFactory.getLog(SecretManagerTemplate.class);
  private final SecretManagerServiceClient secretManagerServiceClient;
  private final GcpProjectIdProvider projectIdProvider;
  /**
   * Define the behavior when accessing a non-existed secret string/bytes.
   */
  private boolean allowDefaultSecretValue;

  public SecretManagerTemplate(
      SecretManagerServiceClient secretManagerServiceClient,
      GcpProjectIdProvider projectIdProvider) {
    this.secretManagerServiceClient = secretManagerServiceClient;
    this.projectIdProvider = projectIdProvider;
    this.allowDefaultSecretValue = false;
  }

  public SecretManagerTemplate setAllowDefaultSecretValue(boolean allowDefaultSecretValue) {
    this.allowDefaultSecretValue = allowDefaultSecretValue;

    return this;
  }

  @Override
  public void createSecret(String secretId, String payload) {
    createNewSecretVersion(
        secretId, ByteString.copyFromUtf8(payload), projectIdProvider.getProjectId());
  }

  @Override
  public void createSecret(String secretId, byte[] payload) {
    createNewSecretVersion(
        secretId, ByteString.copyFrom(payload), projectIdProvider.getProjectId());
  }

  @Override
  public void createSecret(String secretId, byte[] payload, String projectId) {
    createNewSecretVersion(secretId, ByteString.copyFrom(payload), projectId);
  }

  @Override
  @Nullable
  public String getSecretString(String secretIdentifier) {
    ByteString secretByteString = getSecretByteString(secretIdentifier);
    return secretByteString == null ? null : secretByteString.toStringUtf8();
  }

  @Override
  @Nullable
  public byte[] getSecretBytes(String secretIdentifier) {
    ByteString secretByteString = getSecretByteString(secretIdentifier);
    return secretByteString == null ? null : secretByteString.toByteArray();
  }

  @Override
  public boolean secretExists(String secretId) {
    return secretExists(secretId, this.projectIdProvider.getProjectId());
  }

  @Override
  public boolean secretExists(String secretId, String projectId) {
    SecretName secretName = SecretName.of(projectId, secretId);
    try {
      this.secretManagerServiceClient.getSecret(secretName);
    } catch (NotFoundException ex) {
      return false;
    }

    return true;
  }

  @Override
  public void disableSecretVersion(String secretId, String version) {
    disableSecretVersion(secretId, version, this.projectIdProvider.getProjectId());
  }

  @Override
  public void disableSecretVersion(String secretId, String version, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(projectId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    this.secretManagerServiceClient.disableSecretVersion(secretVersionName);
  }

  @Override
  public void enableSecretVersion(String secretId, String version) {
    enableSecretVersion(secretId, version, this.projectIdProvider.getProjectId());
  }

  @Override
  public void enableSecretVersion(String secretId, String version, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(projectId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    this.secretManagerServiceClient.enableSecretVersion(secretVersionName);
  }

  @Override
  public void deleteSecret(String secretId) {
    deleteSecret(secretId, this.projectIdProvider.getProjectId());
  }

  @Override
  public void deleteSecret(String secretId, String projectId) {
    SecretName name = SecretName.of(projectId, secretId);
    DeleteSecretRequest request = DeleteSecretRequest.newBuilder().setName(name.toString()).build();
    this.secretManagerServiceClient.deleteSecret(request);
  }

  @Override
  public void deleteSecretVersion(String secretId, String version, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(projectId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    this.secretManagerServiceClient.destroySecretVersion(secretVersionName);
  }

  ByteString getSecretByteString(String secretIdentifier) {
    SecretVersionName secretVersionName =
        SecretManagerPropertyUtils.getSecretVersionName(secretIdentifier, projectIdProvider);

    if (secretVersionName == null) {
      secretVersionName = getDefaultSecretVersionName(secretIdentifier);
    }

    return getSecretByteString(secretVersionName);
  }

  ByteString getSecretByteString(SecretVersionName secretVersionName) {
    ByteString secretData;
    try {
      secretData = secretManagerServiceClient
          .accessSecretVersion(secretVersionName)
          .getPayload()
          .getData();
    } catch (NotFoundException ex) {
      LOGGER.warn(secretVersionName.toString() + " doesn't exist in Secret Manager.");
      if (!this.allowDefaultSecretValue) {
        throw ex;
      }
      // If no secret is found in Secret Manager and default secret is allowed,
      // returns null rather than throwing the exception to facilitate default
      // secret value parsing.
      // See https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/213
      return null;
    }

    return secretData;
  }

  /**
   * Create a new version of the secret with the specified payload under a {@link Secret}. Will also
   * create the parent secret if it does not already exist.
   */
  private void createNewSecretVersion(String secretId, ByteString payload, String projectId) {
    if (!secretExists(secretId, projectId)) {
      createSecretInternal(secretId, projectId);
    }

    SecretName name = SecretName.of(projectId, secretId);
    AddSecretVersionRequest payloadRequest =
        AddSecretVersionRequest.newBuilder()
            .setParent(name.toString())
            .setPayload(SecretPayload.newBuilder().setData(payload))
            .build();
    secretManagerServiceClient.addSecretVersion(payloadRequest);
  }

  /**
   * Creates a new secret for the GCP Project.
   *
   * <p>Note that the {@link Secret} object does not contain the secret payload. You must create
   * versions of the secret which stores the payload of the secret.
   */
  private void createSecretInternal(String secretId, String projectId) {
    ProjectName projectName = ProjectName.of(projectId);

    Secret secret =
        Secret.newBuilder()
            .setReplication(
                Replication.newBuilder().setAutomatic(Replication.Automatic.getDefaultInstance()))
            .build();
    CreateSecretRequest request =
        CreateSecretRequest.newBuilder()
            .setParent(projectName.toString())
            .setSecretId(secretId)
            .setSecret(secret)
            .build();
    this.secretManagerServiceClient.createSecret(request);
  }

  private SecretVersionName getDefaultSecretVersionName(String secretId) {
    return SecretVersionName.newBuilder()
        .setProject(this.projectIdProvider.getProjectId())
        .setSecret(secretId)
        .setSecretVersion(LATEST_VERSION)
        .build();
  }
}
