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

package com.google.cloud.spring.autoconfigure.storage;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolver;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolverSettings;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * An auto-configuration for Google {@link GoogleStorageProtocolResolverSettings} bean definition.
 * Also it {@link Import} a {@link GoogleStorageProtocolResolver} to register it with the {@code
 * DefaultResourceLoader}.
 *
 * @see GoogleStorageProtocolResolver
 */
@AutoConfiguration
@ConditionalOnClass({GoogleStorageProtocolResolverSettings.class, Storage.class})
@ConditionalOnProperty(value = "spring.cloud.gcp.storage.enabled", matchIfMissing = true)
@EnableConfigurationProperties({GcpProperties.class, GcpStorageProperties.class})
@Import(GoogleStorageProtocolResolver.class)
public class GcpStorageAutoConfiguration { // NOSONAR squid:S1610 must be a class for Spring

  private final GcpProjectIdProvider gcpProjectIdProvider;

  private final CredentialsProvider credentialsProvider;

  private final String universeDomain;

  private final String host;

  public GcpStorageAutoConfiguration(
      GcpProjectIdProvider coreProjectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpStorageProperties gcpStorageProperties)
      throws IOException {

    this.gcpProjectIdProvider =
        gcpStorageProperties.getProjectId() != null
            ? gcpStorageProperties::getProjectId
            : coreProjectIdProvider;

    this.credentialsProvider =
        gcpStorageProperties.getCredentials().hasKey()
            ? new DefaultCredentialsProvider(gcpStorageProperties)
            : credentialsProvider;

    this.universeDomain = gcpStorageProperties.getUniverseDomain();
    this.host = gcpStorageProperties.getHost();
  }

  @Bean
  @ConditionalOnMissingBean
  public Storage storage() throws IOException {
    StorageOptions.Builder storageOptionsBuilder =
        StorageOptions.newBuilder()
            .setHeaderProvider(new UserAgentHeaderProvider(GcpStorageAutoConfiguration.class))
            .setProjectId(this.gcpProjectIdProvider.getProjectId())
            .setCredentials(this.credentialsProvider.getCredentials());

    if (this.universeDomain != null) {
      storageOptionsBuilder.setUniverseDomain(this.universeDomain);
    }
    if (this.host != null) {
      storageOptionsBuilder.setHost(verifyAndFetchHost(this.host));
    }
    return storageOptionsBuilder.build().getService();
  }

  /**
   * Verifies and returns host in the `https://${service}.${universeDomain}/` format, following
   * convention in com.google.cloud.ServiceOptions#getResolvedApiaryHost().
   *
   * @param host host provided through `spring.cloud.gcp.storage.host` property
   * @return host formatted as `https://${service}.${universeDomain}/`
   */
  private String verifyAndFetchHost(String host) {
    URL url;
    try {
      url = new URL(host);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "Invalid host format: "
              + host
              + ". Please verify that the specified host follows the 'https://${service}.${universeDomain}/' format");
    }
    return url.getProtocol() + "://" + url.getHost() + "/";
  }
}
