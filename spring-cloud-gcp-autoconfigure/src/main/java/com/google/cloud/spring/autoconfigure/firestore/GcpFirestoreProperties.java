/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.firestore;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Properties for configuring Cloud Datastore.
 *
 * @since 1.2
 */
@ConfigurationProperties("spring.cloud.gcp.firestore")
public class GcpFirestoreProperties implements CredentialsSupplier {
  private static final String ROOT_PATH_FORMAT = "projects/%s/databases/%s/documents";

  /**
   * Overrides the GCP OAuth2 credentials specified in the Core module. Uses same URL as Datastore
   */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.DATASTORE.getUrl());

  private String projectId;

  private String databaseId;

  /**
   * The host and port of the Firestore emulator service; can be overridden to specify an emulator.
   */
  private String hostPort = "firestore.googleapis.com:443";

  @NestedConfigurationProperty
  private FirestoreEmulatorProperties emulator = new FirestoreEmulatorProperties();

  @Override
  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public String getResolvedProjectId(GcpProjectIdProvider projectIdProvider) {
    return (getProjectId() != null) ? getProjectId() : projectIdProvider.getProjectId();
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getDatabaseId() {
    return databaseId;
  }

  public String getResolvedDatabaseId() {
    return this.getDatabaseId() == null ? "(default)" : this.getDatabaseId();
  }

  public void setDatabaseId(String databaseId) {
    this.databaseId = databaseId;
  }

  public String getHostPort() {
    return hostPort;
  }

  public void setHostPort(String hostPort) {
    this.hostPort = hostPort;
  }

  public FirestoreEmulatorProperties getEmulator() {
    return emulator;
  }

  public void setEmulator(FirestoreEmulatorProperties emulator) {
    this.emulator = emulator;
  }

  public String getFirestoreRootPath(GcpProjectIdProvider projectIdProvider) {
    return String.format(ROOT_PATH_FORMAT, this.getResolvedProjectId(projectIdProvider),
        this.getResolvedDatabaseId());
  }

  public static class FirestoreEmulatorProperties {

    /**
     * Enables autoconfiguration to use the Firestore emulator. If this is set to true, then you
     * should set the spring.cloud.gcp.firestore.host-port to the host:port of your locally running
     * emulator instance
     */
    private boolean enabled = false;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
