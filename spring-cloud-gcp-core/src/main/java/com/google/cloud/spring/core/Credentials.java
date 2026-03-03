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

package com.google.cloud.spring.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.Resource;

/**
 * Credentials configuration.
 */
public final class Credentials {

  /** Overrides OAuth2 scopes list used by the credentials. */
  private List<String> scopes = new ArrayList<>();

  /**
   * Location of the OAuth2 credentials private key.
   *
   * <p>Since this is a Resource, the private key can be in a multitude of
   * locations, such as a local file system, classpath, URL, etc.
   */
  private Resource location;

  /**
   * The base64 encoded contents of an OAuth2 account private key, on the
   * JSON format.
   */
  private String encodedKey;

  /**
   * Constructor.
   *
   * @param defaultScopes default scopes to use
   */
  public Credentials(final String... defaultScopes) {
    this.scopes.addAll(Arrays.asList(defaultScopes));
  }

  /**
   * Returns the list of scopes.
   *
   * @return the list of scopes
   */
  public List<String> getScopes() {
    return this.scopes;
  }

  /**
   * Sets the list of scopes.
   *
   * @param scopesList the list of scopes
   */
  public void setScopes(final List<String> scopesList) {
    this.scopes = scopesList;
  }

  /**
   * Returns the location of the credentials file.
   *
   * @return the location of the credentials file
   */
  public Resource getLocation() {
    return this.location;
  }

  /**
   * Sets the location of the credentials file.
   *
   * @param locationResource the location of the credentials file
   */
  public void setLocation(final Resource locationResource) {
    this.location = locationResource;
  }

  /**
   * Returns the encoded credentials JSON.
   *
   * @return the encoded credentials JSON
   */
  public String getEncodedKey() {
    return this.encodedKey;
  }

  /**
   * Sets the encoded credentials JSON.
   *
   * @param encodedKeyString the encoded credentials JSON
   */
  public void setEncodedKey(final String encodedKeyString) {
    this.encodedKey = encodedKeyString;
  }

  /**
   * Checks if credentials key or location is set.
   *
   * @return true if encodedKey or location is not null
   */
  public boolean hasKey() {
    return this.encodedKey != null || this.location != null;
  }
}
