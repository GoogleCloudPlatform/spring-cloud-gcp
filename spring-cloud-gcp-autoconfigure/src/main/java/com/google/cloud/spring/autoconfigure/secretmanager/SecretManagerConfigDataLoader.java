/*
 * Copyright 2022-2022 the original author or authors.
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

public class SecretManagerConfigDataLoader implements
    ConfigDataLoader<SecretManagerConfigDataResource> {

  @Override
  public ConfigData load(
      ConfigDataLoaderContext context,
      SecretManagerConfigDataResource resource)
      throws IOException, ConfigDataResourceNotFoundException {
    SecretManagerTemplate secretManagerTemplate = context.getBootstrapContext()
        .get(SecretManagerTemplate.class);

    GcpProjectIdProvider projectIdProvider = context.getBootstrapContext()
        .get(GcpProjectIdProvider.class);

    return new ConfigData(Collections.singleton(new SecretManagerPropertySource(
        "spring-cloud-gcp-secret-manager", secretManagerTemplate, projectIdProvider)));
  }
}
