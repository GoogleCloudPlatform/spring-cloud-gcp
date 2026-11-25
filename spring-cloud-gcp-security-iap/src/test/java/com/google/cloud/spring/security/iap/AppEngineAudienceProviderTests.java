/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.security.iap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.resourcemanager.ResourceManager;
import com.google.cloud.resourcemanager.v3.GetProjectRequest;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.cloud.resourcemanager.v3.ProjectName;
import com.google.cloud.resourcemanager.v3.ProjectsClient;
import com.google.cloud.resourcemanager.v3.stub.ProjectsStub;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for the AppEngine Audience Provider. */
@ExtendWith(MockitoExtension.class)
class AppEngineAudienceProviderTests {

  @Mock GcpProjectIdProvider mockProjectIdProvider;

  @Mock ProjectsStub mockProjectsStub;

  @Mock UnaryCallable<GetProjectRequest, Project> mockGetProjectCallable;

  @Mock ResourceManager mockResourceManager;

  @Mock com.google.cloud.resourcemanager.Project mockLegacyProject;

  private ProjectsClient projectsClient;

  @BeforeEach
  void setUp() {
    this.projectsClient = ProjectsClient.create(this.mockProjectsStub);
  }

  @Test
  void testNullProjectIdProviderDisallowed() {

    assertThatThrownBy(() -> new AppEngineAudienceProvider(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("GcpProjectIdProvider cannot be null.");
  }

  @Test
  void testNullProjectsClientDisallowed() {
    AppEngineAudienceProvider audienceProvider =
        new AppEngineAudienceProvider(this.mockProjectIdProvider);
    assertThatThrownBy(() -> audienceProvider.setProjectsClient(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProjectsClient cannot be null.");
  }

  @Test
  void testNullProjectDisallowedWithProjectsClient() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    when(this.mockProjectsStub.getProjectCallable()).thenReturn(this.mockGetProjectCallable);
    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setProjectsClient(this.projectsClient);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith(
            "Project expected not to be null. Is Cloud Resource Manager API enabled");
  }

  @Test
  void testEmptyProjectNameDisallowedWithProjectsClient() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    stubGetProject("steal-spaceship", Project.newBuilder().build());

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setProjectsClient(this.projectsClient);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Project Number expected not to be null.");
  }

  @Test
  void testMalformedProjectNameDisallowedWithProjectsClient() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    stubGetProject("steal-spaceship", Project.newBuilder().setName("invalid-name").build());

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setProjectsClient(this.projectsClient);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Project Number expected not to be null.");
  }

  @Test
  void testNullProjectIdDisallowedWithProjectsClient() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn(null);

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setProjectsClient(this.projectsClient);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Project Id expected not to be null.");
  }

  @Test
  void testAudienceFormatCorrectWithProjectsClient() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    stubGetProject("steal-spaceship", Project.newBuilder().setName("projects/42").build());

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setProjectsClient(this.projectsClient);

    assertThat(provider.getAudience()).isEqualTo("/projects/42/apps/steal-spaceship");
    verify(this.mockProjectsStub, never()).close();
  }

  @Test
  void testDefaultProjectsClientCreatedLazilyAndClosed() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    stubGetProject("steal-spaceship", Project.newBuilder().setName("projects/42").build());

    AtomicInteger createCount = new AtomicInteger();
    AppEngineAudienceProvider provider =
        new AppEngineAudienceProvider(this.mockProjectIdProvider) {
          @Override
          ProjectsClient createProjectsClient() {
            createCount.incrementAndGet();
            return projectsClient;
          }
        };

    assertThat(createCount.get()).isZero();
    assertThat(provider.getAudience()).isEqualTo("/projects/42/apps/steal-spaceship");
    assertThat(createCount.get()).isEqualTo(1);
    verify(this.mockProjectsStub).close();
  }

  /** Stubs the v3 GetProject RPC response for the given project ID. */
  private void stubGetProject(String projectId, Project response) {
    when(this.mockProjectsStub.getProjectCallable()).thenReturn(this.mockGetProjectCallable);
    GetProjectRequest expectedRequest =
        GetProjectRequest.newBuilder().setName(ProjectName.of(projectId).toString()).build();
    when(this.mockGetProjectCallable.call(expectedRequest)).thenReturn(response);
  }

  @Test
  void testDefaultProjectsClientCreationFailureWrapped() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");

    AppEngineAudienceProvider provider =
        new AppEngineAudienceProvider(this.mockProjectIdProvider) {
          @Override
          ProjectsClient createProjectsClient() throws IOException {
            throw new IOException("client creation failed");
          }
        };

    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(UncheckedIOException.class)
        .hasMessageContaining("Failed to create Resource Manager ProjectsClient.");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testNullResourceManagerDisallowed() {
    AppEngineAudienceProvider audienceProvider =
        new AppEngineAudienceProvider(this.mockProjectIdProvider);
    assertThatThrownBy(() -> audienceProvider.setResourceManager(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ResourceManager cannot be null.");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testNullProjectDisallowed() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setResourceManager(this.mockResourceManager);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith(
            "Project expected not to be null. Is Cloud Resource Manager API enabled");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testNullProjectNumberDisallowed() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    when(this.mockResourceManager.get("steal-spaceship")).thenReturn(this.mockLegacyProject);
    when(this.mockLegacyProject.getProjectNumber()).thenReturn(null);

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setResourceManager(this.mockResourceManager);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Project Number expected not to be null.");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testNullProjectIdDisallowed() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn(null);
    when(this.mockResourceManager.get(null)).thenReturn(this.mockLegacyProject);
    when(this.mockLegacyProject.getProjectNumber()).thenReturn(42L);

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setResourceManager(this.mockResourceManager);
    assertThatThrownBy(provider::getAudience)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Project Id expected not to be null.");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testAudienceFormatCorrect() {
    when(this.mockProjectIdProvider.getProjectId()).thenReturn("steal-spaceship");
    when(this.mockResourceManager.get("steal-spaceship")).thenReturn(this.mockLegacyProject);
    when(this.mockLegacyProject.getProjectNumber()).thenReturn(42L);

    AppEngineAudienceProvider provider = new AppEngineAudienceProvider(this.mockProjectIdProvider);
    provider.setResourceManager(this.mockResourceManager);

    assertThat(provider.getAudience()).isEqualTo("/projects/42/apps/steal-spaceship");
  }
}
