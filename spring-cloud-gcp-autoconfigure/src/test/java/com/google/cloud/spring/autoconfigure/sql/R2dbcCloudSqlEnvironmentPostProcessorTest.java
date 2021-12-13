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
 * Tests for {@link R2dbcCloudSqlEnvironmentPostProcessor}.
 */
class R2dbcCloudSqlEnvironmentPostProcessorTest {

	private R2dbcCloudSqlEnvironmentPostProcessor initializer = new R2dbcCloudSqlEnvironmentPostProcessor();

	ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer(
					configurableApplicationContext -> initializer.postProcessEnvironment(
							configurableApplicationContext.getEnvironment(), new SpringApplication()));

	@Test
	void testCreateUrl_mySql() {
		GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
		properties.setDatabaseName("my-database");
		properties.setInstanceConnectionName("my-instance-connection-name");
		String r2dbcUrl = initializer.createUrl(R2dbcDatabaseType.MYSQL, properties);
		assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:mysql://my-instance-connection-name/my-database");
	}

	@Test
	void testCreateUrl_postgres() {
		GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
		properties.setDatabaseName("my-database");
		properties.setInstanceConnectionName("my-instance-connection-name");
		String r2dbcUrl = initializer.createUrl(R2dbcDatabaseType.POSTGRESQL, properties);
		assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:postgres://my-instance-connection-name/my-database");
	}

	@Test
	void testSetR2dbcProperty_mySql_defaultUsername() {
		this.contextRunner
				.withPropertyValues("spring.cloud.gcp.sql.databaseName=my-database",
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
				.run(
						context -> {
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.username")).isEqualTo("root");
						});
	}

	@Test
	void testSetR2dbcProperty_mySql_usernameProvided() {
		this.contextRunner
				.withPropertyValues("spring.cloud.gcp.sql.databaseName=my-database",
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
						"spring.r2dbc.username=my-username")
				.run(
						context -> {
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
									.isEqualTo("my-username");
						});
	}

	@Test
	void testSetR2dbcProperty_mySql_urlProvidedByUserIgnored() {
		this.contextRunner
				.withPropertyValues("spring.cloud.gcp.sql.databaseName=my-database",
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
						"spring.r2dbc.url=ignored")
				.run(
						context -> {
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
						});
	}

	@Test
	void testSetR2dbcProperty_postgres_defaultUsername() {
		this.contextRunner
				.withPropertyValues("spring.cloud.gcp.sql.databaseName=my-database",
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
				.withClassLoader(new FilteredClassLoader("dev.miku.r2dbc.mysql"))
				.run(
						context -> {
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:postgres://my-project:region:my-instance/my-database");
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
									.isEqualTo("postgres");
						});
	}

	@Test
	void testGetEnabledDatatype_noR2dbcConnectorsPresent() {
		this.contextRunner.withClassLoader(
				new FilteredClassLoader("com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql",
						"com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres"))
				.run(
						context -> {
							assertThat(initializer.getEnabledDatabaseType(context.getEnvironment())).isNull();
						});
	}
}
