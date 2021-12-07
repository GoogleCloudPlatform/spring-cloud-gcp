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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

/**
 * Sets credentials for Google Cloud SQL instance connectivity through Spring R2DBC.
 */
public class CloudSqlR2dbcEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final Log LOGGER = LogFactory.getLog(CloudSqlR2dbcEnvironmentPostProcessor.class);

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (isR2dbc()) {
			CredentialsPropertiesSetter.setCredentials(environment, LOGGER);
		}
	}

	boolean isR2dbc() {
		return isOnClasspath("com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql") ||
				isOnClasspath("com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres");
	}

	private boolean isOnClasspath(String className) {
		return ClassUtils.isPresent(className, null);
	}

}
