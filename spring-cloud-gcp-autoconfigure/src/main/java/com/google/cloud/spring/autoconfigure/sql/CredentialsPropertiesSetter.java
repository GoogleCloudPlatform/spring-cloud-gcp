/*
 * Copyright 2021-2022 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.sql;

import java.io.File;
import java.io.IOException;

import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.sql.CredentialFactory;
import org.apache.commons.logging.Log;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

/**
 * Sets credentials to be used by GCP socket factory.
 */
final class CredentialsPropertiesSetter {

	private CredentialsPropertiesSetter() {

	}

	/**
	 * Set credentials to be used by the Google Cloud SQL socket factory.
	 *
	 * <p>
	 * The only way to pass a {@link CredentialFactory} to the socket factory is by passing a
	 * class name through a system property. The socket factory creates an instance of
	 * {@link CredentialFactory} using reflection without any arguments. Because of that, the
	 * credential location needs to be stored somewhere where the class can read it without
	 * any context. It could be possible to pass in a Spring context to
	 * {@link SqlCredentialFactory}, but this is a tricky solution that needs some thinking
	 * about.
	 *
	 * <p>
	 * If user didn't specify credentials, the socket factory already does the right thing by
	 * using the application default credentials by default. So we don't need to do anything.
	 */
	static void setCredentials(ConfigurableEnvironment environment, Log logger) {
		// Bind properties without resolving Secret Manager placeholders
		Binder binder = new Binder(ConfigurationPropertySources.get(environment),
				new NonSecretsManagerPropertiesPlaceholdersResolver(environment),
				null, null, null);
		String cloudSqlPropertiesPrefix = GcpCloudSqlProperties.class.getAnnotation(ConfigurationProperties.class)
				.value();
		String gcpPropertiesPrefix = GcpProperties.class.getAnnotation(ConfigurationProperties.class)
				.value();
		GcpCloudSqlProperties sqlProperties = binder
				.bind(cloudSqlPropertiesPrefix, GcpCloudSqlProperties.class)
				.orElse(new GcpCloudSqlProperties());
		GcpProperties gcpProperties = binder
				.bind(gcpPropertiesPrefix, GcpProperties.class)
				.orElse(new GcpProperties());

		Credentials credentials = null;

		// First tries the SQL configuration credential.
		if (sqlProperties.getCredentials().hasKey()) {
			credentials = sqlProperties.getCredentials();
		}
		// Then, the global credential.
		else {
			credentials = gcpProperties.getCredentials();
		}

		if (credentials.getEncodedKey() != null) {
			setCredentialsEncodedKeyProperty(credentials.getEncodedKey());
		}
		else if (credentials.getLocation() != null) {
			setCredentialsFileProperty(credentials.getLocation(), logger);
		}
		// Else do nothing, let sockets factory use application default credentials.
	}

	private static void setCredentialsEncodedKeyProperty(String encodedKey) {
		System.setProperty(SqlCredentialFactory.CREDENTIAL_ENCODED_KEY_PROPERTY_NAME,
				encodedKey);

		System.setProperty(CredentialFactory.CREDENTIAL_FACTORY_PROPERTY,
				SqlCredentialFactory.class.getName());
	}

	private static void setCredentialsFileProperty(Resource credentialsLocation, Log logger) {
		try {
			// A resource might not be in the filesystem, but the Cloud SQL credential must.
			File credentialsLocationFile = credentialsLocation.getFile();

			System.setProperty(SqlCredentialFactory.CREDENTIAL_LOCATION_PROPERTY_NAME,
					credentialsLocationFile.getAbsolutePath());

			// If there are specified credentials, tell sockets factory to use them.
			System.setProperty(CredentialFactory.CREDENTIAL_FACTORY_PROPERTY,
					SqlCredentialFactory.class.getName());
		}
		catch (IOException ioe) {
			logger.info("Error reading Cloud SQL credentials file.", ioe);
		}
	}
}
