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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CredentialsPropertiesSetter}.
 */
public class CredentialsPropertiesSetterTests {

	@Mock
	Log mockLogger;

	@Test
	void testCredentials_encodedKey_setFromCloudSqlProperties() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues("spring.cloud.gcp.sql.credentials.encodedKey=myGcpEncodedKey");
		contextRunner.run(
				context -> {
					ConfigurableEnvironment environment = context.getEnvironment();
					CredentialsPropertiesSetter.setCredentials(environment, mockLogger);

					assertThat(environment
							.getProperty(SqlCredentialFactory.CREDENTIAL_ENCODED_KEY_PROPERTY_NAME))
									.isEqualTo("myGcpEncodedKey");
					assertThat(environment
							.getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
									.isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
				});
	}

	@Test
	void testCredentials_encodedKey_setFromGcpProperties() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues("spring.cloud.gcp.credentials.encodedKey=myGcpEncodedKey");
		contextRunner.run(
				context -> {
					ConfigurableEnvironment environment = context.getEnvironment();
					CredentialsPropertiesSetter.setCredentials(environment, mockLogger);

					assertThat(environment
							.getProperty(SqlCredentialFactory.CREDENTIAL_ENCODED_KEY_PROPERTY_NAME))
									.isEqualTo("myGcpEncodedKey");
					assertThat(environment
							.getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
									.isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
				});
	}
}
