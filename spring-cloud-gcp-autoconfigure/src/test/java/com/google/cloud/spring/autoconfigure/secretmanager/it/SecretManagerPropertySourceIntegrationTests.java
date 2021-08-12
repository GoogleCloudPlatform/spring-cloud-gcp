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

package com.google.cloud.spring.autoconfigure.secretmanager.it;

import java.util.Collections;

import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.autoconfigure.secretmanager.GcpSecretManagerBootstrapConfiguration;
import com.google.cloud.spring.core.GcpScope;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import io.grpc.StatusRuntimeException;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

public class SecretManagerPropertySourceIntegrationTests {

	private ConfigurableApplicationContext context =
			new SpringApplicationBuilder(GcpSecretManagerBootstrapConfiguration.class)
					.child(TestConfiguration.class)
					.web(WebApplicationType.NONE)
					.properties("spring.cloud.bootstrap.enabled=true", "spring.autoconfigure.exclude=CloudSqlEnvironmentPostProcessor",
							"spring.cloud.gcp.sql.databaseName=test-database",
							"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance",
							"spring.datasource.password=",
							"debug=true")
					.run();

	private static final String TEST_SECRET_ID = "spring-cloud-gcp-it-secret";

	@BeforeClass
	public static void prepare() throws Exception {
		assumeThat(System.getProperty("it.secretmanager"))
				.as("Secret manager integration tests are disabled. "
						+ "Please use '-Dit.secretmanager=true' to enable them.")
				.isEqualTo("true");

		// Create the test secret if it does not already currently exist.
		SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
				.setCredentialsProvider(
						GoogleCredentialsProvider.newBuilder()
								.setScopesToApply(Collections.singletonList(GcpScope.CLOUD_PLATFORM.getUrl()))
								.build())
				.build();

		SecretManagerTemplate template = new SecretManagerTemplate(SecretManagerServiceClient.create(settings),
				() -> ServiceOptions.getDefaultProjectId());
		if (!template.secretExists(TEST_SECRET_ID)) {
			template.createSecret(TEST_SECRET_ID, "the secret data.");
		}
	}

	@Test
	public void testConfiguration() {
		assertThat(context.getEnvironment().getProperty("sm://" + TEST_SECRET_ID))
				.isEqualTo("the secret data.");

		byte[] byteArraySecret = context.getEnvironment().getProperty(
				"sm://" + TEST_SECRET_ID + "/latest", byte[].class);
		assertThat(byteArraySecret).isEqualTo("the secret data.".getBytes());
	}

	@Test
	public void testValueAnnotation() {
		String secret = context.getBean("secret", String.class);
		assertThat(secret).isEqualTo("the secret data.");
	}

	@Test
	public void testMissingSecret() {
		assertThatThrownBy(() ->
				context.getEnvironment().getProperty("sm://missing-secret/10", String.class))
				.hasCauseInstanceOf(StatusRuntimeException.class)
				.hasMessageContaining("NOT_FOUND");
	}

	@Configuration
	static class TestConfiguration {

		@Value("${sm://" + TEST_SECRET_ID + "}")
		private String secret;

		@Bean
		public String secret() {
			return secret;
		}
	}
}
