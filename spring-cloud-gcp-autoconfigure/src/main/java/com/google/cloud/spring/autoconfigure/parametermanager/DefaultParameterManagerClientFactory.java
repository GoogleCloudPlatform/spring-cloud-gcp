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

package com.google.cloud.spring.autoconfigure.parametermanager;

import static com.google.cloud.spring.parametermanager.ParameterManagerTemplate.GLOBAL_LOCATION;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerClientFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * A default implementation of the {@link ParameterManagerClientFactory} interface.
 *
 * <p>This factory provides a caching layer for {@link ParameterManagerClient} instances. Clients
 * are created using the provided {@link CredentialsProvider} and a {@link UserAgentHeaderProvider}
 * that adds the Spring Cloud GCP agent header to the client.
 */
@Component
public class DefaultParameterManagerClientFactory implements ParameterManagerClientFactory {

  private final CredentialsProvider credentialsProvider;
  private final Map<String, ParameterManagerClient> clientCache = new ConcurrentHashMap<>();

  DefaultParameterManagerClientFactory(
      CredentialsProvider credentialsProvider, ParameterManagerClient client) {
    this.credentialsProvider = credentialsProvider;
    this.clientCache.putIfAbsent(GLOBAL_LOCATION, client);
  }

  @Override
  public ParameterManagerClient getClient(@Nullable String location) {
    if (ObjectUtils.isEmpty(location)) {
      location = GLOBAL_LOCATION;
    }
    return clientCache.computeIfAbsent(
        location,
        loc -> {
          try {
            ParameterManagerSettings.Builder settings =
                ParameterManagerSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .setHeaderProvider(
                        new UserAgentHeaderProvider(ParameterManagerConfigDataLoader.class));
            if (!loc.equals(GLOBAL_LOCATION)) {
              settings.setEndpoint(
                  String.format("parametermanager.%s.rep.googleapis.com:443", loc));
            }
            return ParameterManagerClient.create(settings.build());
          } catch (IOException e) {
            throw new RuntimeException(
                "Failed to create ParameterManagerClient for location: " + loc, e);
          }
        });
  }
}
