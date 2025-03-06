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

package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.secretmanager.SecretManagerServiceClientFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * A default implementation of the {@link SecretManagerServiceClientFactory} interface.
 *
 * <p>This factory provides a caching layer for {@link SecretManagerServiceClient} instances.
 * Clients are created using the provided {@link CredentialsProvider} and a {@link
 * UserAgentHeaderProvider} that adds the Spring Cloud GCP agent header to the client.
 *
 */
@Component
public class DefaultSecretManagerServiceClientFactory implements SecretManagerServiceClientFactory {

  private final CredentialsProvider credentialsProvider;
  private final Map<String, SecretManagerServiceClient> clientCache = new ConcurrentHashMap<>();

  public DefaultSecretManagerServiceClientFactory(CredentialsProvider credentialsProvider) {
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  public SecretManagerServiceClient getClient(String location) {
    if (ObjectUtils.isEmpty(location)) {
      return getClient();
    }
    return clientCache.computeIfAbsent(location, loc -> {
      try {
        String endpoint = String.format("secretmanager.%s.rep.googleapis.com:443", loc);
        SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setHeaderProvider(new UserAgentHeaderProvider(SecretManagerConfigDataLoader.class))
            .setEndpoint(endpoint).build();
        return SecretManagerServiceClient.create(settings);
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to create SecretManagerServiceClient for location: " + loc, e);
      }
    });
  }

  @Override
  public SecretManagerServiceClient getClient() {
    return clientCache.computeIfAbsent("", loc -> {
      try {
        SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setHeaderProvider(new UserAgentHeaderProvider(SecretManagerConfigDataLoader.class)
            ).build();

        return SecretManagerServiceClient.create(settings);
      } catch (IOException e) {
        throw new RuntimeException("Failed to create SecretManagerServiceClient", e);
      }
    });
  }
}
