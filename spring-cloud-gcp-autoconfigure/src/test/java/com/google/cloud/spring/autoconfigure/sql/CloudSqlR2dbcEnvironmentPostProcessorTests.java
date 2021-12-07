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

import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudSqlR2dbcEnvironmentPostProcessor}.
 */
public class CloudSqlR2dbcEnvironmentPostProcessorTests {
	private CloudSqlR2dbcEnvironmentPostProcessor initializer = new CloudSqlR2dbcEnvironmentPostProcessor();

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer(configurableApplicationContext -> initializer
					.postProcessEnvironment(configurableApplicationContext.getEnvironment(), new SpringApplication()));

	@Test
	void testIsR2dbc_gcpSocketFactoriesNotPresent() {
		this.contextRunner = this.contextRunner.withClassLoader(
				new FilteredClassLoader("com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql",
						"com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres"));
		this.contextRunner.run(
				context -> {
					assertThat(initializer.isR2dbc()).isFalse();
				});
	}
}
