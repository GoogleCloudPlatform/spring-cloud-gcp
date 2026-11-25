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

package com.google.cloud.spring.autoconfigure.trace;

import static com.google.auth.Credentials.GOOGLE_DEFAULT_UNIVERSE;

import com.google.api.gax.rpc.EndpointContext;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import java.util.function.Function;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

/** Stackdriver Trace Properties. */
@ConfigurationProperties("spring.cloud.gcp.trace")
public class GcpTraceProperties implements CredentialsSupplier {

  /** Overrides the GCP project ID specified in the Core module. */
  private String projectId;

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.TRACE_APPEND.getUrl());

  /** Number of threads to be used by the Trace executor. */
  private int numExecutorThreads = 4;

  /** HTTP/2 authority the channel claims to be connecting to. */
  private String authority;

  /** Compression to use for the call. */
  private String compression;

  /** Call deadline. */
  private Long deadlineMs;

  /** Maximum size for an inbound message. */
  private Integer maxInboundSize;

  /** Maximum size for an outbound message. */
  private Integer maxOutboundSize;

  /**
   * Waits for the channel to be ready in case of a transient failure. Defaults to failing fast in
   * that case.
   */
  private Boolean waitForReady;

  /** Timeout in seconds before pending spans will be sent in batches to GCP Stackdriver Trace. */
  private int messageTimeout = 1;

  /** Timeout in seconds for server response when sending traces. */
  private Long serverResponseTimeoutMs;

  /**
   * Universe domain of the Trace client. When unset, falls back to the {@code
   * GOOGLE_CLOUD_UNIVERSE_DOMAIN} environment variable and then {@code googleapis.com}.
   */
  private String universeDomain;

  /**
   * Custom gRPC target endpoint for the Trace channel (for example, {@code
   * dns:///cloudtrace.example.com}). When unset, defaults to {@code
   * dns:///cloudtrace.<universeDomain>}.
   */
  private String endpoint;

  public String getProjectId() {
    return this.projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public int getNumExecutorThreads() {
    return this.numExecutorThreads;
  }

  public void setNumExecutorThreads(int numExecutorThreads) {
    this.numExecutorThreads = numExecutorThreads;
  }

  public String getAuthority() {
    return this.authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  public String getCompression() {
    return this.compression;
  }

  public void setCompression(String compression) {
    this.compression = compression;
  }

  public Long getDeadlineMs() {
    return this.deadlineMs;
  }

  public void setDeadlineMs(long deadlineMs) {
    this.deadlineMs = deadlineMs;
  }

  public Integer getMaxInboundSize() {
    return this.maxInboundSize;
  }

  public void setMaxInboundSize(int maxInboundSize) {
    this.maxInboundSize = maxInboundSize;
  }

  public Integer getMaxOutboundSize() {
    return this.maxOutboundSize;
  }

  public void setMaxOutboundSize(int maxOutboundSize) {
    this.maxOutboundSize = maxOutboundSize;
  }

  public Boolean isWaitForReady() {
    return this.waitForReady;
  }

  public void setWaitForReady(boolean waitForReady) {
    this.waitForReady = waitForReady;
  }

  public int getMessageTimeout() {
    return this.messageTimeout;
  }

  public void setMessageTimeout(int messageTimeout) {
    this.messageTimeout = messageTimeout;
  }

  public Long getServerResponseTimeoutMs() {
    return serverResponseTimeoutMs;
  }

  public void setServerResponseTimeoutMs(Long serverResponseTimeoutMs) {
    this.serverResponseTimeoutMs = serverResponseTimeoutMs;
  }

  /** Returns the configured universe domain, or {@code null} if unset. */
  public String getUniverseDomain() {
    return this.universeDomain;
  }

  /** Sets the universe domain for the Trace client. */
  public void setUniverseDomain(String universeDomain) {
    this.universeDomain = universeDomain;
  }

  /** Returns the configured gRPC target endpoint, or {@code null} if unset. */
  public String getEndpoint() {
    return this.endpoint;
  }

  /** Sets a custom gRPC target endpoint for the Trace channel. */
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
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
   * Resolves the gRPC target endpoint, using the explicitly configured {@code endpoint} when set or
   * computing {@code dns:///cloudtrace.<universeDomain>} otherwise.
   */
  String getResolvedEndpoint(Function<String, String> envProvider) {
    // Respect explicit custom endpoint overrides.
    if (StringUtils.hasText(this.endpoint)) {
      return this.endpoint;
    }
    // Derive the default target from the resolved universe domain instead of hardcoding
    // googleapis.com.
    return String.format("dns:///cloudtrace.%s", getResolvedUniverseDomain(envProvider));
  }
}
