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

package com.google.cloud.spring.autoconfigure.emulator;

import com.google.auth.Credentials;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom credentials provider used when connecting to a local Firestore emulator.
 */
public class FirestoreEmulatorCredentials extends Credentials {

  private final Map<String, List<String>> headerMap;

  /**
   * Constructs emulator credentials targeting the specified root resource path.
   *
   * @param rootPath the Firestore root database path
   */
  public FirestoreEmulatorCredentials(String rootPath) {
    this.headerMap = new HashMap<>();
    this.headerMap.put("Authorization", Collections.singletonList("Bearer owner"));
    this.headerMap.put(
        "google-cloud-resource-prefix",
        Collections.singletonList(rootPath.substring(0, rootPath.lastIndexOf("/documents"))));
  }

  @Override
  public String getAuthenticationType() {
    return null;
  }

  @Override
  public Map<String, List<String>> getRequestMetadata(URI uri) {
    return this.headerMap;
  }

  @Override
  public boolean hasRequestMetadata() {
    return true;
  }

  @Override
  public boolean hasRequestMetadataOnly() {
    return true;
  }

  @Override
  public void refresh() {
    // no-op
  }
}
