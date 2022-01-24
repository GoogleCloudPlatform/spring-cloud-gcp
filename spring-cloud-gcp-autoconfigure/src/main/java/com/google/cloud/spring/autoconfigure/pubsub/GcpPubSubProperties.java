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

package com.google.cloud.spring.autoconfigure.pubsub;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/** Properties for Pub/Sub. */
@ConfigurationProperties("spring.cloud.gcp.pubsub")
public class GcpPubSubProperties extends PubSubConfiguration implements CredentialsSupplier {

  /** Overrides the GCP project ID specified in the Core module. */
  private String projectId;

  /**
   * The host and port of the local running emulator. If provided, this will setup the client to
   * connect against a running pub/sub emulator.
   */
  private String emulatorHost;

  /** How often to ping the server to keep the channel alive. */
  private int keepAliveIntervalMinutes = 5;

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.PUBSUB.getUrl());

  public String getProjectId() {
    return this.projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getEmulatorHost() {
    return this.emulatorHost;
  }

  /**
   * Set the emulator host.
   *
   * @param emulatorHost the emulator host.
   */
  public void setEmulatorHost(String emulatorHost) {
    this.emulatorHost = emulatorHost;
  }

  public int getKeepAliveIntervalMinutes() {
    return keepAliveIntervalMinutes;
  }

  public void setKeepAliveIntervalMinutes(int keepAliveIntervalMinutes) {
    this.keepAliveIntervalMinutes = keepAliveIntervalMinutes;
  }
}
