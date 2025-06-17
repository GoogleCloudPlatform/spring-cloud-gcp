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

package com.google.cloud.spring.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.AddSecretVersionRequest;
import com.google.cloud.secretmanager.v1.CreateSecretRequest;
import com.google.cloud.secretmanager.v1.DeleteSecretRequest;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecretManagerRegionalTemplateTests {

  private SecretManagerServiceClientFactory secretManagerServiceClientFactory;

  private SecretManagerTemplate secretManagerTemplate;

  @BeforeEach
  void setupMocks() {
    SecretManagerServiceClient secretManagerServiceClient = mock(SecretManagerServiceClient.class);
    this.secretManagerServiceClientFactory = mock(SecretManagerServiceClientFactory.class);
    when(secretManagerServiceClientFactory.getClient("us-central1"))
        .thenReturn(secretManagerServiceClient);
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .accessSecretVersion(any(SecretVersionName.class)))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(
                    SecretPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("regional payload.")))
                .build());

    this.secretManagerTemplate =
        new SecretManagerTemplate(this.secretManagerServiceClientFactory, () -> "my-reg-project");
  }

  @Test
  void testProjectIdRegional() {
    assertThat(this.secretManagerTemplate.getProjectId()).isEqualTo("my-reg-project");
  }

  @Test
  void testCreateRegionalSecretIfMissing() {
    // This means that no previous regional secrets exist.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(any(SecretName.class)))
        .thenThrow(NotFoundException.class);

    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!", "us-central1");

    // Verify the regional secret is created correctly.
    verifyCreateRegionalSecretRequest("my-reg-secret", "us-central1", "my-reg-project");

    // Verifies the regional secret payload is added correctly.
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "my-reg-project");
  }

  @Test
  void testCreateRegionalSecretIfMissing_withProject() {
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(any(SecretName.class)))
        .thenThrow(NotFoundException.class);

    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!".getBytes(), "custom-reg-project", "us-central1");

    verifyCreateRegionalSecretRequest("my-reg-secret", "us-central1", "custom-reg-project");
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "custom-reg-project");
  }

  @Test
  void testCreateRegionalSecretIfAlreadyPresent() {
    // The secret 'my-reg-secret' already exists.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(
                SecretName.ofProjectLocationSecretName(
                    "my-reg-project", "us-central1", "my-reg-secret")))
        .thenReturn(Secret.getDefaultInstance());

    // Verify that the secret is not created.
    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!", "us-central1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .getSecret(
            SecretName.ofProjectLocationSecretName(
                "my-reg-project", "us-central1", "my-reg-secret"));
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"), never())
        .createSecret(any());
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "my-reg-project");
  }

  @Test
  void testCreateRegionalSecretIfAlreadyPresent_withProject() {
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(
                SecretName.ofProjectLocationSecretName(
                    "custom-reg-project", "us-central1", "my-reg-secret")))
        .thenReturn(Secret.getDefaultInstance());

    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!".getBytes(), "custom-reg-project", "us-central1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .getSecret(
            SecretName.ofProjectLocationSecretName(
                "custom-reg-project", "us-central1", "my-reg-secret"));
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"), never())
        .createSecret(any());
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "custom-reg-project");
  }

  @Test
  void testCreateRegionalByteSecretIfMissing() {
    // This means that no previous secrets exist.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(any(SecretName.class)))
        .thenThrow(NotFoundException.class);

    this.secretManagerTemplate.createSecret(
        "my-reg-secret",
        "hello regional world!".getBytes(),
        this.secretManagerTemplate.getProjectId(),
        "us-central1");

    verifyCreateRegionalSecretRequest("my-reg-secret", "us-central1", "my-reg-project");
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "my-reg-project");
  }

  @Test
  void testCreateRegionalByteSecretIfMissing_withProject() {
    // This means that no previous secrets exist.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(any(SecretName.class)))
        .thenThrow(NotFoundException.class);

    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!".getBytes(), "custom-reg-project", "us-central1");

    verifyCreateRegionalSecretRequest("my-reg-secret", "us-central1", "custom-reg-project");
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "custom-reg-project");
  }

  @Test
  void testCreateRegionalByteSecretIfAlreadyPresent() {
    // The secret 'my-reg-secret' already exists.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(
                SecretName.ofProjectLocationSecretName(
                    "my-reg-project", "us-central1", "my-reg-secret")))
        .thenReturn(Secret.getDefaultInstance());

    // Verify that the secret is not created.
    this.secretManagerTemplate.createSecret(
        "my-reg-secret",
        "hello regional world!".getBytes(),
        this.secretManagerTemplate.getProjectId(),
        "us-central1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .getSecret(
            SecretName.ofProjectLocationSecretName(
                "my-reg-project", "us-central1", "my-reg-secret"));
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"), never())
        .createSecret(any());
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "my-reg-project");
  }

  @Test
  void testCreateRegionalByteSecretIfAlreadyPresent_withProject() {
    // The secret 'my-reg-secret' already exists.
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .getSecret(
                SecretName.ofProjectLocationSecretName(
                    "custom-reg-project", "us-central1", "my-reg-secret")))
        .thenReturn(Secret.getDefaultInstance());

    // Verify that the secret is not created.
    this.secretManagerTemplate.createSecret(
        "my-reg-secret", "hello regional world!".getBytes(), "custom-reg-project", "us-central1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .getSecret(
            SecretName.ofProjectLocationSecretName(
                "custom-reg-project", "us-central1", "my-reg-secret"));
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"), never())
        .createSecret(any());
    verifyAddRegionalSecretRequest(
        "my-reg-secret", "hello regional world!", "us-central1", "custom-reg-project");
  }

  @Test
  void testAccessRegionalSecretBytes() {
    byte[] result =
        this.secretManagerTemplate.getSecretBytes("sm@locations/us-central1/my-reg-secret");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .accessSecretVersion(
            SecretVersionName.ofProjectLocationSecretSecretVersionName(
                "my-reg-project", "us-central1", "my-reg-secret", "latest"));
    assertThat(result).isEqualTo("regional payload.".getBytes());

    result =
        this.secretManagerTemplate.getSecretBytes("sm://locations/us-central1/my-reg-secret/1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .accessSecretVersion(
            SecretVersionName.ofProjectLocationSecretSecretVersionName(
                "my-reg-project", "us-central1", "my-reg-secret", "1"));
    assertThat(result).isEqualTo("regional payload.".getBytes());
  }

  @Test
  void testAccessRegionalSecretString() {
    String result =
        this.secretManagerTemplate.getSecretString("sm@locations/us-central1/my-reg-secret");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .accessSecretVersion(
            SecretVersionName.ofProjectLocationSecretSecretVersionName(
                "my-reg-project", "us-central1", "my-reg-secret", "latest"));
    assertThat(result).isEqualTo("regional payload.");

    result =
        this.secretManagerTemplate.getSecretString("sm://locations/us-central1/my-reg-secret/1");
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .accessSecretVersion(
            SecretVersionName.ofProjectLocationSecretSecretVersionName(
                "my-reg-project", "us-central1", "my-reg-secret", "1"));
    assertThat(result).isEqualTo("regional payload.");
  }

  @Test
  void testAccessNonExistentRegionalSecretStringWhenDefaultIsNotAllowed() {
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .accessSecretVersion(any(SecretVersionName.class)))
        .thenThrow(NotFoundException.class);
    assertThatThrownBy(
            () ->
                this.secretManagerTemplate.getSecretString(
                    "sm://locations/us-central1/fake-secret"))
        .isExactlyInstanceOf(NotFoundException.class);
  }

  @Test
  void testAccessNonExistentRegionalSecretStringWhenDefaultIsAllowed() {
    when(this.secretManagerServiceClientFactory
            .getClient("us-central1")
            .accessSecretVersion(any(SecretVersionName.class)))
        .thenThrow(NotFoundException.class);
    this.secretManagerTemplate =
        new SecretManagerTemplate(this.secretManagerServiceClientFactory, () -> "my-reg-project")
            .setAllowDefaultSecretValue(true);
    String result =
        this.secretManagerTemplate.getSecretString("sm://locations/us-central1/fake-secret");
    assertThat(result).isNull();
  }

  @Test
  void testEnableRegionalSecretVersion() {
    this.secretManagerTemplate.enableSecretVersion(
        "my-reg-secret", "1", "custom-reg-project", "us-central1");
    verifyEnableRegionalSecretVersionRequest(
        "my-reg-secret", "1", "us-central1", "custom-reg-project");
  }

  @Test
  void testDeleteRegionalSecret() {
    this.secretManagerTemplate.deleteSecret("my-reg-secret", "custom-reg-project", "us-central1");
    verifyDeleteRegionalSecretRequest("my-reg-secret", "us-central1", "custom-reg-project");
  }

  @Test
  void testDeleteRegionalSecretVersion() {
    this.secretManagerTemplate.deleteSecretVersion(
        "my-reg-secret", "10", "custom-reg-project", "us-central1");
    verifyDeleteRegionalSecretVersionRequest(
        "my-reg-secret", "10", "us-central1", "custom-reg-project");
  }

  @Test
  void testDisableRegionalSecretVersion() {
    this.secretManagerTemplate.disableSecretVersion(
        "my-reg-secret", "1", this.secretManagerTemplate.getProjectId(), "us-central1");
    verifyDisableRegionalSecretVersionRequest(
        "my-reg-secret", "1", "us-central1", "my-reg-project");
  }

  private void verifyCreateRegionalSecretRequest(
      String secretId, String locationId, String projectId) {
    Secret secretToAdd = Secret.newBuilder().build();

    CreateSecretRequest createSecretRequest =
        CreateSecretRequest.newBuilder()
            .setParent("projects/" + projectId + "/locations/" + locationId)
            .setSecretId(secretId)
            .setSecret(secretToAdd)
            .build();

    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .createSecret(createSecretRequest);
  }

  private void verifyAddRegionalSecretRequest(
      String secretId, String payload, String locationId, String projectId) {
    AddSecretVersionRequest addSecretVersionRequest =
        AddSecretVersionRequest.newBuilder()
            .setParent(
                "projects/" + projectId + "/locations/" + locationId + "/secrets/" + secretId)
            .setPayload(SecretPayload.newBuilder().setData(ByteString.copyFromUtf8(payload)))
            .build();
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .addSecretVersion(addSecretVersionRequest);
  }

  private void verifyEnableRegionalSecretVersionRequest(
      String secretId, String version, String locationId, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(projectId)
            .setLocation(locationId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .enableSecretVersion(secretVersionName);
  }

  private void verifyDeleteRegionalSecretRequest(
      String secretId, String locationId, String projectId) {
    SecretName name = SecretName.ofProjectLocationSecretName(projectId, locationId, secretId);
    DeleteSecretRequest request = DeleteSecretRequest.newBuilder().setName(name.toString()).build();
    verify(this.secretManagerServiceClientFactory.getClient("us-central1")).deleteSecret(request);
  }

  private void verifyDeleteRegionalSecretVersionRequest(
      String secretId, String version, String locationId, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(projectId)
            .setLocation(locationId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .destroySecretVersion(secretVersionName);
  }

  private void verifyDisableRegionalSecretVersionRequest(
      String secretId, String version, String locationId, String projectId) {
    SecretVersionName secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(projectId)
            .setLocation(locationId)
            .setSecret(secretId)
            .setSecretVersion(version)
            .build();
    verify(this.secretManagerServiceClientFactory.getClient("us-central1"))
        .disableSecretVersion(secretVersionName);
  }
}
