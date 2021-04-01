/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsublite;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("spring.cloud.gcp.pubsublite")
public class GcpPubSubLiteProperties implements CredentialsSupplier {
  /**
	 * Contains settings specific to the subscriber factory.
	 */
	private final Subscriber subscriber = new Subscriber();

  public Subscriber getSubscriber() {
    return subscriber;
  }

  /**
   * Overrides the GCP project ID specified in the Core module.
   */
  private String projectId;

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * Location of Pub/Sub Lite topic or subscription. Must be a valid cloud zone.
   */
  private String location;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Overrides the GCP OAuth2 credentials specified in the Core module.
   */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.CLOUD_PLATFORM.getUrl());

  @Override
  public Credentials getCredentials() {
    return credentials;
  }

  /**
   * Subscriber settings.
   */
  public static class Subscriber {
    /**
     * Flow control settings for subscriber factory.
     */
    private FlowControl perPartitionFlowControl = new FlowControl();

    public FlowControl getPerPartitionFlowControl() {
      return perPartitionFlowControl;
    }

    public void setPerPartitionFlowControl(FlowControl perPartitionFlowControl) {
      this.perPartitionFlowControl = perPartitionFlowControl;
    }
  }

  /**
   * flow control settings.
   */
  public static class FlowControl {
    /**
     * Maximum number of outstanding elements to be sent to this client.
     */
    private Long maxOutstandingElementCount;

    /**
     * Maximum number of outstanding bytes to be sent to this client.
     */
    private Long maxOutstandingRequestBytes;

    public Long getMaxOutstandingElementCount() {
      return this.maxOutstandingElementCount;
    }

    public void setMaxOutstandingElementCount(
        Long maxOutstandingElementCount) {
      this.maxOutstandingElementCount = maxOutstandingElementCount;
    }

    public Long getMaxOutstandingRequestBytes() {
      return this.maxOutstandingRequestBytes;
    }

    public void setMaxOutstandingRequestBytes(
        Long maxOutstandingRequestBytes) {
      this.maxOutstandingRequestBytes = maxOutstandingRequestBytes;
    }
  }
}
