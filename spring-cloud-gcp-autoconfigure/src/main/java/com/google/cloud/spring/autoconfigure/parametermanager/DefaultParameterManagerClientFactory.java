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
import java.util.function.Function;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A default implementation of the {@link ParameterManagerClientFactory} interface.
 *
 * <p>This factory provides a caching layer for {@link ParameterManagerClient} instances. Clients
 * are created using the provided {@link CredentialsProvider} and a {@link UserAgentHeaderProvider}
 * that adds the Spring Cloud GCP agent header to the client.
 */
@Component
public class DefaultParameterManagerClientFactory implements ParameterManagerClientFactory {

  private static final String DEFAULT_UNIVERSE_DOMAIN = "googleapis.com";
  private static final String UNIVERSE_DOMAIN_ENV_VAR = "GOOGLE_CLOUD_UNIVERSE_DOMAIN";

  private final CredentialsProvider credentialsProvider;
  @Nullable private final String universeDomain;
  private final Function<String, String> envProvider;
  private final Map<String, ParameterManagerClient> clientCache = new ConcurrentHashMap<>();

  DefaultParameterManagerClientFactory(
      CredentialsProvider credentialsProvider, ParameterManagerClient client) {
    this(credentialsProvider, client, null, System::getenv);
  }

  DefaultParameterManagerClientFactory(
      CredentialsProvider credentialsProvider,
      ParameterManagerClient client,
      @Nullable String universeDomain) {
    this(credentialsProvider, client, universeDomain, System::getenv);
  }

  DefaultParameterManagerClientFactory(
      CredentialsProvider credentialsProvider,
      ParameterManagerClient client,
      @Nullable String universeDomain,
      Function<String, String> envProvider) {
    this.credentialsProvider = credentialsProvider;
    this.universeDomain = universeDomain;
    this.envProvider = envProvider;
    this.clientCache.putIfAbsent(GLOBAL_LOCATION, client);
  }

  /**
   * Resolves the universe domain from explicit configuration, falling back to {@code
   * GOOGLE_CLOUD_UNIVERSE_DOMAIN} and then {@code googleapis.com}.
   */
  String resolveUniverseDomain() {
    if (StringUtils.hasText(this.universeDomain)) {
      return this.universeDomain;
    }
    String envUniverseDomain = this.envProvider.apply(UNIVERSE_DOMAIN_ENV_VAR);
    if (StringUtils.hasText(envUniverseDomain)) {
      return envUniverseDomain;
    }
    return DEFAULT_UNIVERSE_DOMAIN;
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
            String resolvedUniverseDomain = resolveUniverseDomain();
            ParameterManagerSettings.Builder settings =
                ParameterManagerSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .setHeaderProvider(
                        new UserAgentHeaderProvider(ParameterManagerConfigDataLoader.class))
                    .setUniverseDomain(resolvedUniverseDomain);
            if (!loc.equals(GLOBAL_LOCATION)) {
              settings.setEndpoint(
                  String.format("parametermanager.%s.rep.%s:443", loc, resolvedUniverseDomain));
            }
            return ParameterManagerClient.create(settings.build());
          } catch (IOException e) {
            throw new RuntimeException(
                "Failed to create ParameterManagerClient for location: " + loc, e);
          }
        });
  }
}
