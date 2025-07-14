/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.secretmanager;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import javax.annotation.Nullable;

/** Interface for creating Secret Manager service clients. */
public interface SecretManagerServiceClientFactory {
  /**
   * Creates a Secret Manager service client for the given location.
   *
   * @param location the location to create a client for.
   * @return a Secret Manager service client.
   */
  SecretManagerServiceClient getClient(@Nullable String location);
}
