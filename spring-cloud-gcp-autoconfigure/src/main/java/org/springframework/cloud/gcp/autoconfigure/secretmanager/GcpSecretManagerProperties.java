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

package org.springframework.cloud.gcp.autoconfigure.secretmanager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.gcp.core.Credentials;
import org.springframework.cloud.gcp.core.CredentialsSupplier;
import org.springframework.cloud.gcp.core.GcpScope;

@ConfigurationProperties("spring.cloud.gcp.secretmanager")
public class GcpSecretManagerProperties implements CredentialsSupplier {

	/**
	 * Overrides the GCP OAuth2 credentials specified in the Core module.
	 */
	@NestedConfigurationProperty
	private final Credentials credentials = new Credentials(GcpScope.CLOUD_PLATFORM.getUrl());

	/**
	 * Overrides the GCP Project ID specified in the Core module.
	 */
	private String projectId;

	/**
	 * Defines a prefix String that will be prepended to the environment property names
	 * of secrets in Secret Manager.
	 */
	private String secretNamePrefix = "";

	/**
	 * Defines the secret's version to be used.
	 */
	private String version;

	/**
	 * Defines versions for specific secret-ids.
	 */
	private Map<String, String> versions = new HashMap<>();

	public Credentials getCredentials() {
		return credentials;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getSecretNamePrefix() {
		return secretNamePrefix;
	}

	public void setSecretNamePrefix(String secretNamePrefix) {
		this.secretNamePrefix = secretNamePrefix;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, String> getVersions() {
		return versions;
	}

	public void setVersions(Map<String, String> versions) {
		this.versions = versions;
	}
}
