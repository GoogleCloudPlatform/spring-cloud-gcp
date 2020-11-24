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

import java.util.Collections;

import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerPropertySource;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;

public class SecretManagerConfigDataLoader
		implements ConfigDataLoader<SecretManagerConfigDataResource> {

	@Override
	public ConfigData load(
			ConfigDataLoaderContext context,
			SecretManagerConfigDataResource resource) {
		SecretManagerServiceClient secretManagerServiceClient =
				resource.getSecretManagerServiceClient();

		GcpProjectIdProvider projectIdProvider =
				context.getBootstrapContext().get(GcpProjectIdProvider.class);

		SecretManagerTemplate template = new SecretManagerTemplate(secretManagerServiceClient, projectIdProvider);
		SecretManagerPropertySource propertySource = new SecretManagerPropertySource(
				"spring-cloud-gcp-secret-manager", template, projectIdProvider);

		return new ConfigData(Collections.singleton(propertySource));
	}
}
