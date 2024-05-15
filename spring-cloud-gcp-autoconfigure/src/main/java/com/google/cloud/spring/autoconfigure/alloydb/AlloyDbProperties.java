/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.spring.autoconfigure.alloydb;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("spring.cloud.gcp.alloydb")
public class AlloyDbProperties implements CredentialsSupplier {

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.CLOUD_PLATFORM.getUrl());

  /** Overrides the GCP Project ID specified in the Core module. */
  private String projectId;

  /** Name of the database in the AlloyDB instance. */
  private String databaseName;

  /** AlloyDB instance connection URI. */
  private String instanceConnectionUri;

  /** Type of IP to be used: PRIVATE, PUBLIC, PSC. */
  private String ipType;

  /** Service account impersonation. */
  private String targetPrincipal;

  /**
   * Comma-separated list of service accounts containing chained list of delegates
   * required to grant the final access_token.
   */
  private String delegates;

  /** Admin API Service Endpoint. */
  private String adminServiceEndpoint;

  /** GCP Project ID for quota and billing. */
  private String quotaProject;

  /** Named Connector */
  private String namedConnector;

  /** Specifies whether to enable IAM database authentication. */
  private boolean enableIamAuth;

  @Override
  public Credentials getCredentials() {
    return credentials;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getDatabaseName() {
    return this.databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getInstanceConnectionUri() {
    return this.instanceConnectionUri;
  }

  public void setInstanceConnectionUri(String instanceConnectionUri) {
    this.instanceConnectionUri = instanceConnectionUri;
  }

  public boolean isEnableIamAuth() {
    return enableIamAuth;
  }

  public void setEnableIamAuth(boolean enableIamAuth) {
    this.enableIamAuth = enableIamAuth;
  }

  public String getIpType() {
    return this.ipType;
  }

  public void setIpType(String ipType) {
    this.ipType = ipType;
  }

  public String getTargetPrincipal() {
    return this.targetPrincipal;
  }

  public void setTargetPrincipal(String targetPrincipal) {
    this.targetPrincipal = targetPrincipal;
  }

  public String getDelegates() {
    return this.delegates;
  }

  public void setDelegates(String delegates) {
    this.delegates = delegates;
  }

  public String getAdminServiceEndpoint() {
    return this.adminServiceEndpoint;
  }

  public void setAdminServiceEndpoint(String adminServiceEndpoint) {
    this.adminServiceEndpoint = adminServiceEndpoint;
  }

  public String getQuotaProject() {
    return this.quotaProject;
  }

  public void setQuotaProject(String quotaProject) {
    this.quotaProject = quotaProject;
  }

  public String getNamedConnector() {
    return this.namedConnector;
  }

  public void setNamedConnector(String namedConnector) {
    this.namedConnector = namedConnector;
  }
}
