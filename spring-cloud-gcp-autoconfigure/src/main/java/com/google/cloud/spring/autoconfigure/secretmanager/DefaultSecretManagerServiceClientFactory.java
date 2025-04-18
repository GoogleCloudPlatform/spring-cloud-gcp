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

import static com.google.cloud.spring.secretmanager.SecretManagerTemplate.GLOBAL_LOCATION;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.secretmanager.SecretManagerServiceClientFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * A default implementation of the {@link SecretManagerServiceClientFactory} interface.
 *
 * <p>This factory provides a caching layer for {@link SecretManagerServiceClient} instances.
 * Clients are created using the provided {@link CredentialsProvider} and a {@link
 * UserAgentHeaderProvider} that adds the Spring Cloud GCP agent header to the client.
 */
public class DefaultSecretManagerServiceClientFactory implements SecretManagerServiceClientFactory {

  private final CredentialsProvider credentialsProvider;
  private final Map<String, SecretManagerServiceClient> clientCache = new ConcurrentHashMap<>();

  DefaultSecretManagerServiceClientFactory(CredentialsProvider credentialsProvider, SecretManagerServiceClient client) {
    this.credentialsProvider = credentialsProvider;
    this.clientCache.putIfAbsent(GLOBAL_LOCATION, client);
  }

  @Override
  public SecretManagerServiceClient getClient(@Nullable String location) {
    if (ObjectUtils.isEmpty(location)) {
      location = GLOBAL_LOCATION;
    }
    return clientCache.computeIfAbsent(location, loc -> {
      try {
        SecretManagerServiceSettings.Builder settings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setHeaderProvider(new UserAgentHeaderProvider(SecretManagerConfigDataLoader.class));
        if (!loc.equals(GLOBAL_LOCATION)) {
          settings.setEndpoint(String.format("secretmanager.%s.rep.googleapis.com:443", loc));
        }
        return SecretManagerServiceClient.create(settings.build());
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to create SecretManagerServiceClient for location: " + loc, e);
      }
    });
  }
}
