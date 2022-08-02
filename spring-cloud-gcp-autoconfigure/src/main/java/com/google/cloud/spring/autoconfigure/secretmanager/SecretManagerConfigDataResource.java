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

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResource;

public class SecretManagerConfigDataResource extends ConfigDataResource {

  //private final SecretManagerServiceClient secretManagerServiceClient;
  private final SecretManagerTemplate secretManagerTemplate;
  private final ConfigDataLocation location;

  public SecretManagerConfigDataResource(SecretManagerTemplate secretManagerTemplate,
      ConfigDataLocation location) {
    this.secretManagerTemplate = secretManagerTemplate;
    this.location = location;
  }

  public SecretManagerTemplate secretManagerTemplate() {
    return secretManagerTemplate;
  }

  public ConfigDataLocation getLocation() {
    return location;
  }
}

