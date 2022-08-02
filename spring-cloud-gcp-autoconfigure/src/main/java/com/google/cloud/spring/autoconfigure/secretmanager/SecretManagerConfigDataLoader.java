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

package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerPropertySource;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.io.IOException;
import java.util.Collections;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretManagerConfigDataLoader implements
    ConfigDataLoader<SecretManagerConfigDataResource> {

  private SecretManagerTemplate template;
  private GcpProjectIdProvider idProvider;

  SecretManagerConfigDataLoader(SecretManagerTemplate template, GcpProjectIdProvider idProvider) {
    this.template = template;
    this.idProvider = idProvider;
  }

  @Override
  public ConfigData load(
      ConfigDataLoaderContext context,
      SecretManagerConfigDataResource resource)
      throws IOException, ConfigDataResourceNotFoundException {
    // SecretManagerServiceClient secretManagerServiceClient =
    //     resource.getSecretManagerServiceClient();
    //
    // GcpProjectIdProvider projectIdProvider =
    //     context.getBootstrapContext().get(GcpProjectIdProvider.class);
    //
    // SecretManagerTemplate template = new SecretManagerTemplate(secretManagerServiceClient,
    //     projectIdProvider);
    if (!template.secretExists(resource.getLocation().toString())) {
      throw new ConfigDataResourceNotFoundException(resource);
    }
    SecretManagerPropertySource propertySource = new SecretManagerPropertySource(
        "spring-cloud-gcp-secret-manager", template, idProvider);

    return new ConfigData(Collections.singleton(propertySource));
  }
}
