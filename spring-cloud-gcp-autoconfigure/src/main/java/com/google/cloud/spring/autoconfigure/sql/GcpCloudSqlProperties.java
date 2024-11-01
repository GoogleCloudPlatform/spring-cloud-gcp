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

package com.google.cloud.spring.autoconfigure.sql;

import com.google.cloud.spring.core.Credentials;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Google Cloud SQL properties. */
@ConfigurationProperties("spring.cloud.gcp.sql")
public class GcpCloudSqlProperties {
  /** Name of the database in the Cloud SQL instance. */
  private String databaseName;

  /** Cloud SQL instance connection name. [GCP_PROJECT_ID]:[INSTANCE_REGION]:[INSTANCE_NAME]. */
  private String instanceConnectionName;

  /** A comma delimited list of preferred IP types for connecting to the Cloud SQL instance. */
  private String ipTypes;

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  private Credentials credentials = new Credentials();

  /** Specifies whether to enable IAM database authentication (PostgreSQL only). */
  private boolean enableIamAuth;
  /**
   * The target principal to use for service account impersonation. Corresponds to
   * Cloud SQL Java Connector JDBC property cloudSqlTargetPrincipal
   */
  private String targetPrincipal;

  /**
   * The chain of delegated service accounts to use for service account impersonation.
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlDelegates
   */
  private String delegates;

  /**
   * The alternate admin root url for the Cloud SQL Admin API.
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlAdminRootUrl.
   */
  private String adminRootUrl;
  /**
   * The alternate service path for the Cloud SQL Admin API
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlAdminServicePath.
   */
  private String adminServicePath;
  /**
   * The quota project to use for API requests.
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlAdminQuotaProject
   */
  private String adminQuotaProject;
  /**
   * The universe domain to use for API requests
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlUniverseDomain
   */
  private String universeDomain;
  /**
   * The refresh strategy to use for API requests
   * Corresponds to Cloud SQL Java Connector JDBC property cloudSqlRefreshStrategy
   */
  private String refreshStrategy;


  public String getDatabaseName() {
    return this.databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getInstanceConnectionName() {
    return this.instanceConnectionName;
  }

  public void setInstanceConnectionName(String instanceConnectionName) {
    this.instanceConnectionName = instanceConnectionName;
  }

  public String getIpTypes() {
    return this.ipTypes;
  }

  public void setIpTypes(String ipTypes) {
    this.ipTypes = ipTypes;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public boolean isEnableIamAuth() {
    return enableIamAuth;
  }

  public void setEnableIamAuth(boolean enableIamAuth) {
    this.enableIamAuth = enableIamAuth;
  }

  public String getTargetPrincipal() {
    return targetPrincipal;
  }

  public void setTargetPrincipal(String targetPrincipal) {
    this.targetPrincipal = targetPrincipal;
  }

  public String getDelegates() {
    return delegates;
  }

  public void setDelegates(String delegates) {
    this.delegates = delegates;
  }

  public String getAdminRootUrl() {
    return adminRootUrl;
  }

  public void setAdminRootUrl(String adminRootUrl) {
    this.adminRootUrl = adminRootUrl;
  }

  public String getAdminServicePath() {
    return adminServicePath;
  }

  public void setAdminServicePath(String adminServicePath) {
    this.adminServicePath = adminServicePath;
  }

  public String getAdminQuotaProject() {
    return adminQuotaProject;
  }

  public void setAdminQuotaProject(String adminQuotaProject) {
    this.adminQuotaProject = adminQuotaProject;
  }

  public String getUniverseDomain() {
    return universeDomain;
  }

  public void setUniverseDomain(String universeDomain) {
    this.universeDomain = universeDomain;
  }

  public String getRefreshStrategy() {
    return refreshStrategy;
  }

  public void setRefreshStrategy(String refreshStrategy) {
    this.refreshStrategy = refreshStrategy;
  }
}
