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

package com.google.cloud.spring.core;

import com.google.api.gax.rpc.HeaderProvider;
import java.util.Collections;
import java.util.Map;

/**
 * Provides the user-agent header to signal to the Google Cloud Client Libraries that requests
 * originate from a Spring Integration.
 */
public class UserAgentHeaderProvider implements HeaderProvider {

  private String userAgent;

  private final Map<String, String> headers;

  public UserAgentHeaderProvider(Class<?> clazz) {
    this.userAgent = computeUserAgent(clazz);
    this.headers = Collections.singletonMap("user-agent", this.userAgent);
  }

  /**
   * Returns the "user-agent" header whose value should be added to the google-cloud-java REST API
   * calls. e.g., {@code user-agent: Spring/1.0.0.RELEASE spring-cloud-gcp-pubsub/1.0.0.RELEASE}.
   */
  @Override
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  /**
   * Returns the "user-agent" header value which should be added to the google-cloud-java REST API
   * calls. e.g., {@code Spring/1.0.0.RELEASE spring-cloud-gcp-pubsub/1.0.0.RELEASE}.
   *
   * @return the user agent string.
   */
  public String getUserAgent() {
    return this.userAgent;
  }

  private String computeUserAgent(Class<?> clazz) {
    String[] packageTokens = clazz.getPackage().getName().split("\\.");
    String springLibrary = "spring-cloud-gcp-" + packageTokens[packageTokens.length - 1];
    String version = this.getClass().getPackage().getImplementationVersion();

    return "Spring/" + version + " " + springLibrary + "/" + version;
  }
}
