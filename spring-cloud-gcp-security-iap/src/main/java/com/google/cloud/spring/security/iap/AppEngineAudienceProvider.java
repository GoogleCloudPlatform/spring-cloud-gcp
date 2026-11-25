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

import com.google.cloud.resourcemanager.ResourceManager;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.cloud.resourcemanager.v3.ProjectName;
import com.google.cloud.resourcemanager.v3.ProjectsClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.security.oauth2.jwt.Jwt} token validator for GCP App Engine (both
 * Flexible and Standard) audience strings.
 *
 * @since 1.1
 */
public class AppEngineAudienceProvider implements AudienceProvider, AutoCloseable {

  private static final String AUDIENCE_FORMAT = "/projects/%s/apps/%s";

  private static final String PROJECT_NOT_FOUND_MESSAGE =
      "Project expected not to be null. Is Cloud Resource Manager API enabled?"
          + " (https://console.developers.google.com/apis/api/cloudresourcemanager.googleapis.com)";

  private final GcpProjectIdProvider projectIdProvider;

  private ProjectsClient projectsClient;

  private boolean ownsProjectsClient;

  private ResourceManager resourceManager;

  public AppEngineAudienceProvider(GcpProjectIdProvider projectIdProvider) {
    Assert.notNull(projectIdProvider, "GcpProjectIdProvider cannot be null.");

    this.projectIdProvider = projectIdProvider;
  }

  @Override
  public String getAudience() {
    if (this.resourceManager != null) {
      return getAudienceWithLegacyResourceManager();
    }

    String projectId = this.projectIdProvider.getProjectId();
    Assert.notNull(projectId, "Project Id expected not to be null.");

    Project project = fetchProject(projectId);
    Assert.notNull(project, PROJECT_NOT_FOUND_MESSAGE);

    String projectNumber = extractProjectNumber(project);
    Assert.hasText(projectNumber, "Project Number expected not to be null.");

    return String.format(AUDIENCE_FORMAT, projectNumber, projectId);
  }

  /**
   * Sets the Resource Manager v3 {@link ProjectsClient} used to resolve project numbers.
   *
   * @param projectsClient the {@link ProjectsClient} to use; must not be {@code null}
   */
  public synchronized void setProjectsClient(ProjectsClient projectsClient) {
    Assert.notNull(projectsClient, "ProjectsClient cannot be null.");
    closeOwnedProjectsClient();
    this.projectsClient = projectsClient;
    this.ownsProjectsClient = false;
    this.resourceManager = null;
  }

  /**
   * Sets the legacy {@link ResourceManager} client used to resolve project numbers.
   *
   * @param resourceManager the legacy {@link ResourceManager} to use; must not be {@code null}
   * @deprecated Use {@link #setProjectsClient(ProjectsClient)} instead.
   */
  @Deprecated
  public synchronized void setResourceManager(ResourceManager resourceManager) {
    Assert.notNull(resourceManager, "ResourceManager cannot be null.");
    closeOwnedProjectsClient();
    this.resourceManager = resourceManager;
    this.projectsClient = null;
    this.ownsProjectsClient = false;
  }

  /** Closes the internally created {@link ProjectsClient}, if any. */
  @Override
  public synchronized void close() {
    closeOwnedProjectsClient();
  }

  /** Creates a default Resource Manager v3 {@link ProjectsClient}. */
  ProjectsClient createProjectsClient() throws IOException {
    return ProjectsClient.create();
  }

  /** Fetches the v3 {@link Project} for the given project ID. */
  private Project fetchProject(String projectId) {
    return getOrCreateProjectsClient().getProject(ProjectName.of(projectId));
  }

  /** Returns the configured {@link ProjectsClient}, lazily creating and caching one if needed. */
  private synchronized ProjectsClient getOrCreateProjectsClient() {
    if (this.projectsClient == null) {
      try {
        this.projectsClient = createProjectsClient();
        this.ownsProjectsClient = true;
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to create Resource Manager ProjectsClient.", e);
      }
    }
    return this.projectsClient;
  }

  /** Closes and clears the cached {@link ProjectsClient} only when owned by this provider. */
  private void closeOwnedProjectsClient() {
    if (this.ownsProjectsClient && this.projectsClient != null) {
      this.projectsClient.close();
      this.projectsClient = null;
      this.ownsProjectsClient = false;
    }
  }

  /** Extracts the project number from a v3 {@link Project}'s resource name. */
  private static String extractProjectNumber(Project project) {
    String name = project.getName();
    if (!StringUtils.hasText(name) || !ProjectName.isParsableFrom(name)) {
      return null;
    }
    ProjectName parsed = ProjectName.parse(name);
    return parsed != null ? parsed.getProject() : null;
  }

  /** Resolves the audience using a caller-supplied legacy {@link ResourceManager} client. */
  private String getAudienceWithLegacyResourceManager() {
    var project = this.resourceManager.get(this.projectIdProvider.getProjectId());
    Assert.notNull(project, PROJECT_NOT_FOUND_MESSAGE);
    Assert.notNull(project.getProjectNumber(), "Project Number expected not to be null.");

    String projectId = this.projectIdProvider.getProjectId();
    Assert.notNull(projectId, "Project Id expected not to be null.");

    return String.format(AUDIENCE_FORMAT, project.getProjectNumber(), projectId);
  }
}
