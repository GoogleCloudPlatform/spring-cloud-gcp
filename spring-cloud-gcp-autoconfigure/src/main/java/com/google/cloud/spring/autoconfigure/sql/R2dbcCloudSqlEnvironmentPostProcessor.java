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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Builds connection string for Cloud SQL through Spring R2DBC by requiring only a
 * database and instance connection name.
 *
 */
public class R2dbcCloudSqlEnvironmentPostProcessor implements EnvironmentPostProcessor {
	private static final Log LOGGER = LogFactory.getLog(R2dbcCloudSqlEnvironmentPostProcessor.class);

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

		R2dbcDatabaseType databaseType = getEnabledDatabaseType(environment);
		if (databaseType != null) {
			// Bind properties without resolving Secret Manager placeholders
			Binder binder = new Binder(ConfigurationPropertySources.get(environment),
					new NonSecretsManagerPropertiesPlaceholdersResolver(environment),
					null, null, null);
			String cloudSqlPropertiesPrefix = GcpCloudSqlProperties.class.getAnnotation(ConfigurationProperties.class)
					.value();
			GcpCloudSqlProperties sqlProperties = binder
					.bind(cloudSqlPropertiesPrefix, GcpCloudSqlProperties.class)
					.orElse(new GcpCloudSqlProperties());

			String r2dbcUrl = createUrl(databaseType, sqlProperties);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Default " + databaseType.name()
						+ " R2dbcUrl provider. Connecting to "
						+ r2dbcUrl);
			}

			// Add default username as fallback when not specified
			Map<String, Object> fallbackMap = new HashMap<>();
			fallbackMap.put("spring.r2dbc.username", databaseType.getDefaultUsername());
			environment.getPropertySources().addLast(new MapPropertySource("CLOUD_SQL_R2DBC_USERNAME", fallbackMap));

			Map<String, Object> primaryMap = new HashMap<>();
			primaryMap.put("spring.r2dbc.url", r2dbcUrl);
			environment.getPropertySources().addFirst(new MapPropertySource("CLOUD_SQL_R2DBC_URL", primaryMap));
		}
	}

	String createUrl(R2dbcDatabaseType databaseType, GcpCloudSqlProperties sqlProperties) {
		Assert.hasText(sqlProperties.getDatabaseName(), "A database name must be provided.");
		Assert.hasText(sqlProperties.getInstanceConnectionName(),
				"An instance connection name must be provided in the format <PROJECT_ID>:<REGION>:<INSTANCE_ID>.");

		return String.format(databaseType.getUrlTemplate(),
				sqlProperties.getInstanceConnectionName(), sqlProperties.getDatabaseName());
	}

	R2dbcDatabaseType getEnabledDatabaseType(ConfigurableEnvironment environment) {
		if (Boolean.parseBoolean(environment.getProperty("spring.cloud.gcp.sql.enabled", "true"))
				&& isOnClasspath("com.google.cloud.sql.CredentialFactory")) {
			if (isOnClasspath("com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql") &&
					isOnClasspath("dev.miku.r2dbc.mysql.MySqlConnectionFactoryProvider")) {
				return R2dbcDatabaseType.MYSQL;
			}
			else if (isOnClasspath("com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres")
					&& isOnClasspath("io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider")) {
				return R2dbcDatabaseType.POSTGRESQL;
			}
		}
		return null;
	}

	private boolean isOnClasspath(String className) {
		return ClassUtils.isPresent(className, null);
	}
}
