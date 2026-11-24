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

import static com.google.auth.Credentials.GOOGLE_DEFAULT_UNIVERSE;

import com.google.api.gax.rpc.EndpointContext;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.GcpScope;
import java.util.function.Function;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

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
   * The host and port of the Firestore service; can be overridden to specify an emulator or custom
   * endpoint. When unset, defaults to {@code firestore.<universeDomain>:443}.
   */
  private String hostPort;

  /**
   * Universe domain of the Firestore client. When unset, falls back to the {@code
   * GOOGLE_CLOUD_UNIVERSE_DOMAIN} environment variable and then {@code googleapis.com}.
   */
  private String universeDomain;

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
    return getResolvedHostPort(System::getenv);
  }

  public void setHostPort(String hostPort) {
    this.hostPort = hostPort;
  }

  /** Returns the configured universe domain, or {@code null} if unset. */
  public String getUniverseDomain() {
    return universeDomain;
  }

  /** Sets the universe domain for the Firestore client. */
  public void setUniverseDomain(String universeDomain) {
    this.universeDomain = universeDomain;
  }

  /**
   * Resolves the universe domain from explicit configuration, falling back to {@code
   * GOOGLE_CLOUD_UNIVERSE_DOMAIN} and then {@code googleapis.com}.
   */
  String getResolvedUniverseDomain(Function<String, String> envProvider) {
    if (StringUtils.hasText(this.universeDomain)) {
      return this.universeDomain;
    }
    String envUniverseDomain = envProvider.apply(EndpointContext.GOOGLE_CLOUD_UNIVERSE_DOMAIN);
    if (StringUtils.hasText(envUniverseDomain)) {
      return envUniverseDomain;
    }
    return GOOGLE_DEFAULT_UNIVERSE;
  }

  /**
   * Resolves the Firestore host and port, using the explicitly configured {@code hostPort} when set
   * or computing {@code firestore.<universeDomain>:443} otherwise.
   */
  String getResolvedHostPort(Function<String, String> envProvider) {
    // Respect explicit emulator or custom host-port overrides.
    if (StringUtils.hasText(this.hostPort)) {
      return this.hostPort;
    }
    // Derive the default endpoint from the resolved universe domain instead of hardcoding
    // googleapis.com.
    return String.format("firestore.%s:443", getResolvedUniverseDomain(envProvider));
  }

  public FirestoreEmulatorProperties getEmulator() {
    return emulator;
  }

  public void setEmulator(FirestoreEmulatorProperties emulator) {
    this.emulator = emulator;
  }

  public String getFirestoreRootPath(GcpProjectIdProvider projectIdProvider) {
    return String.format(
        ROOT_PATH_FORMAT,
        this.getResolvedProjectId(projectIdProvider),
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
