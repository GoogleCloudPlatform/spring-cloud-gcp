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

package com.google.cloud.spring.autoconfigure.sql;

import javax.sql.DataSource;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql;
import com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Cloud SQL {@link CloudSqlEnvironmentPostProcessor}.
 *
 * @author João André Martins
 * @author Artem Bilan
 * @author Øystein Urdahl Hardeng
 * @author Mike Eltsufin
 */
class CloudSqlEnvironmentPostProcessorTests {
	private CloudSqlEnvironmentPostProcessor initializer = new CloudSqlEnvironmentPostProcessor();

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("spring.cloud.gcp.sql.databaseName=test-database")
			.withInitializer(configurableApplicationContext -> initializer.postProcessEnvironment(configurableApplicationContext.getEnvironment(), new SpringApplication()))
			.withUserConfiguration(Config.class);

	@Test
	void testJdbc_cloudSqlDataSource() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance",
				"spring.datasource.password=")
				.run(context -> {
					HikariDataSource dataSource =
							(HikariDataSource) context.getBean(DataSource.class);
					assertThat(dataSource.getDriverClassName()).matches("com.mysql.cj.jdbc.Driver");
					assertThat(dataSource.getJdbcUrl()).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:singapore:test-instance");
					assertThat(dataSource.getUsername()).matches("root");
					assertThat(dataSource.getPassword()).isNull();
				});
	}

	@Test
	void testJdbc_cloudSqlSpringDataSourceUrlPropertyOverride() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance",
				"spring.datasource.password=",
				"spring.datasource.url=jdbc:h2:mem:none;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE")
				.run(context -> {
					HikariDataSource dataSource =
							(HikariDataSource) context.getBean(DataSource.class);
					assertThat(dataSource.getDriverClassName()).matches("com.mysql.cj.jdbc.Driver");
					assertThat(dataSource.getJdbcUrl()).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:singapore:test-instance");
					assertThat(dataSource.getUsername()).matches("root");
					assertThat(dataSource.getPassword()).isNull();
					assertThat(getSpringDatasourceDriverClassName(context)).matches("com.mysql.cj.jdbc.Driver");
					assertThat(context.getEnvironment().getProperty("spring.datasource.url"))
							.isEqualTo("jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:singapore:test-instance");
				});
	}

	@Test
	void testJdbc_cloudSqlDataSourceWithIgnoredProvidedUrl() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance",
				"spring.datasource.password=",
				"spring.datasource.url=test-url")
				.run(context -> {
					HikariDataSource dataSource =
							(HikariDataSource) context.getBean(DataSource.class);
					assertThat(dataSource.getDriverClassName()).matches("com.mysql.cj.jdbc.Driver");
					assertThat(dataSource.getJdbcUrl()).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:singapore:test-instance");
					assertThat(dataSource.getUsername()).matches("root");
					assertThat(dataSource.getPassword()).isNull();
					assertThat(getSpringDatasourceDriverClassName(context)).matches("com.mysql.cj.jdbc.Driver");
				});
	}

	@Test
	void testJdbc_cloudSqlAppEngineDataSourceDefaultUserNameMySqlTest() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.project-id=im-not-used-for-anything",
						"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:australia:test-instance",
						"spring.datasource.password=")
				.withSystemProperties(
						"com.google.appengine.runtime.version=Google App Engine/Some Server")
				.run(context -> {
					HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
					assertThat(getSpringDatasourceDriverClassName(context))
							.matches("com.mysql.cj.jdbc.Driver");
					assertThat(dataSource.getDriverClassName())
							.matches("com.mysql.cj.jdbc.Driver");
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:australia:test-instance");
					assertThat(dataSource.getUsername()).matches("root");
					assertThat(dataSource.getPassword()).isNull();
				});
	}

	@Test
	void testJdbc_userAndPassword() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues("spring.datasource.username=watchmaker",
						"spring.datasource.password=pass",
						"spring.cloud.gcp.sql.instance-connection-name=proj:reg:test-instance")
				.run(context -> {
					HikariDataSource dataSource =
							(HikariDataSource) context.getBean(DataSource.class);
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=proj:reg:test-instance");
					assertThat(dataSource.getUsername()).matches("watchmaker");
					assertThat(dataSource.getPassword()).matches("pass");
					assertThat(getSpringDatasourceDriverClassName(context)).matches("com.mysql.cj.jdbc.Driver");
				});
	}

	@Test
	void testJdbc_userSpecifiedDriverOverride() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=proj:reg:test-instance",
						"spring.datasource.driver-class-name=org.postgresql.Driver")
				.run(context -> {
					HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=proj:reg:test-instance");
					assertThat(dataSource.getDriverClassName()).matches("org.postgresql.Driver");
				});
	}

	@Test
	void testJdbc_dataSourceProperties() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=proj:reg:test-instance",
						"spring.datasource.hikari.connectionTestQuery=select 1",
						"spring.datasource.hikari.maximum-pool-size=19")
				.run(context -> {
					HikariDataSource dataSource =
							(HikariDataSource) context.getBean(DataSource.class);
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=proj:reg:test-instance");
					assertThat(getSpringDatasourceDriverClassName(context)).matches("com.mysql.cj.jdbc.Driver");
					assertThat(dataSource.getMaximumPoolSize()).isEqualTo(19);
					assertThat(dataSource.getConnectionTestQuery()).matches("select 1");
				});
	}

	@Test
	void testJdbc_instanceConnectionName() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan")
				.run(context -> {
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:mysql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
									+ "&cloudSqlInstance=world:asia:japan");
					assertThat(getSpringDatasourceDriverClassName(context)).matches("com.mysql.cj.jdbc.Driver");
				});
	}

	@Test
	void testJdbc_postgres() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance")
				.withClassLoader(
						new FilteredClassLoader("com.google.cloud.sql.mysql"))
				.run(context -> {
					HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
					assertThat(getSpringDatasourceUrl(context)).isEqualTo(
							"jdbc:postgresql://google/test-database?"
									+ "socketFactory=com.google.cloud.sql.postgres.SocketFactory"
									+ "&cloudSqlInstance=tubular-bells:singapore:test-instance");
					assertThat(getSpringDatasourceDriverClassName(context)).matches("org.postgresql.Driver");
					assertThat(dataSource.getUsername()).matches("postgres");
				});
	}

	@Test
	void testNoJdbc() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=tubular-bells:singapore:test-instance")
				.withClassLoader(
						new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(context -> {
					assertThat(context.getBeanNamesForType(DataSource.class)).isEmpty();
					assertThat(context.getBeanNamesForType(DataSourceProperties.class)).isEmpty();
					assertThat(context.getBeanNamesForType(CloudSqlJdbcInfoProvider.class)).isEmpty();
				});
	}

	@Test
	void testJdbc_ipTypes() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
						"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
						"spring.cloud.gcp.sql.ip-types=PRIVATE")
				.run(context -> {
					DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
					assertThat(dataSourceProperties.getUrl()).contains(
							"&ipTypes=PRIVATE");
				});
	}

	@Test
	void testJdbc_iamAuth() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(
				DataSourceAutoConfiguration.class)).withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
				"spring.cloud.gcp.sql.enableIamAuth=true")
				.run(context -> {
					DataSourceProperties dataSourceProperties =
							context.getBean(DataSourceProperties.class);
					assertThat(dataSourceProperties.getUrl()).contains(
							"&enableIamAuth=true&sslmode=disable");
				});
	}

	@Test
	void testJdbc_secretManagerPlaceholdersNotResolved() {
		this.contextRunner.withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
				"spring.cloud.gcp.sql.database-name=${sm://my-db}")
				.run(context -> {
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_DATA_SOURCE_URL")
							.getProperty("spring.datasource.url"))
									.isEqualTo("jdbc:mysql://google/${sm://my-db}?"
											+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
											+ "&cloudSqlInstance=world:asia:japan");
				});
	}

	@Test
	void testJdbc_envPlaceholdersResolved() {
		this.contextRunner.withPropertyValues(
				"DB_NAME=mydb",
				"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
				"spring.cloud.gcp.sql.database-name=${DB_NAME:not_available}")
				.run(context -> {
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_DATA_SOURCE_URL")
							.getProperty("spring.datasource.url"))
									.isEqualTo("jdbc:mysql://google/mydb?"
											+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
											+ "&cloudSqlInstance=world:asia:japan");
				});
	}

	@Test
	void testSkipOnBootstrap() {
		new ApplicationContextRunner()
				.withPropertyValues("spring.cloud.gcp.sql.databaseName=test-database")
				.withInitializer(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
					@Override
					public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
						// add a property source called "bootstrap" to mark it as the bootstrap phase
						configurableApplicationContext.getEnvironment().getPropertySources().addFirst(new PropertySource<Object>("bootstrap") {
							@Override
							public Object getProperty(String name) {
								return null;
							}
						});
					}
				})
				.withInitializer(configurableApplicationContext -> initializer.postProcessEnvironment(configurableApplicationContext.getEnvironment(), new SpringApplication()))
				.run(context -> {
					assertThat(getSpringDatasourceUrl(context)).isNull();
				});
	}

	@Test
	void testR2dbc_secretManagerPlaceholdersNotResolved() {
		this.contextRunner.withPropertyValues(
				"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
				"spring.cloud.gcp.sql.database-name=${sm://my-db}")
				.withClassLoader(new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(context -> {
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_R2DBC_URL")
							.getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:mysql://world:asia:japan/${sm://my-db}");
				});
	}

	@Test
	void testR2dbc_envPlaceholdersResolved() {
		this.contextRunner.withPropertyValues(
				"DB_NAME=mydb",
				"spring.cloud.gcp.sql.instance-connection-name=world:asia:japan",
				"spring.cloud.gcp.sql.database-name=${DB_NAME:not_available}")
				.withClassLoader(
						new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(context -> {
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_R2DBC_URL")
							.getProperty("spring.r2dbc.url"))
							.isEqualTo("r2dbc:gcp:mysql://world:asia:japan/mydb");
				});
	}

	@Test
	void testCreateR2dbcUrl_mySql() {
		GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
		properties.setDatabaseName("test-database");
		properties.setInstanceConnectionName("my-instance-connection-name");
		String r2dbcUrl = initializer.createR2dbcUrl(R2dbcDatabaseType.MYSQL, properties);
		assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:mysql://my-instance-connection-name/test-database");
	}

	@Test
	void testCreateR2dbcUrl_postgres() {
		GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
		properties.setDatabaseName("test-database");
		properties.setInstanceConnectionName("my-instance-connection-name");
		String r2dbcUrl = initializer.createR2dbcUrl(R2dbcDatabaseType.POSTGRESQL, properties);
		assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:postgres://my-instance-connection-name/test-database");
	}

	@Test
	void testR2dbc_mySql_defaultUsername() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
				.withClassLoader(new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(context -> {
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_R2DBC_URL")
							.getProperty("spring.r2dbc.url"))
									.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/test-database");
					assertThat(context.getEnvironment().getPropertySources()
							.get("CLOUD_SQL_R2DBC_FALLBACK")
							.getProperty("spring.r2dbc.username")).isEqualTo("root");
				});
	}

	@Test
	void testR2dbc_mySql_usernameProvided() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
						"spring.r2dbc.username=my-username")
				.withClassLoader(
						new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(
						context -> {
							assertThat(context.getEnvironment().getPropertySources().get("CLOUD_SQL_R2DBC_URL")
									.getProperty("spring.r2dbc.url"))
											.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/test-database");
							assertThat(context.getEnvironment()
									.getProperty("spring.r2dbc.username"))
											.isEqualTo("my-username");
						});
	}

	@Test
	void testR2dbc_mySql_urlProvidedByUserIgnored() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
						"spring.r2dbc.url=ignored")
				.withClassLoader(new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class))
				.run(
						context -> {
							assertThat(context.getEnvironment().getPropertySources().get("CLOUD_SQL_R2DBC_URL")
									.getProperty("spring.r2dbc.url"))
											.isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/test-database");
						});
	}

	@Test
	void testR2dbc_postgres_defaultUsername() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
				.withClassLoader(new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class,
						GcpConnectionFactoryProviderMysql.class))
				.run(
						context -> {
							assertThat(context.getEnvironment().getPropertySources().get("CLOUD_SQL_R2DBC_URL")
									.getProperty("spring.r2dbc.url"))
											.isEqualTo(
													"r2dbc:gcp:postgres://my-project:region:my-instance/test-database");
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
									.isEqualTo("postgres");
						});
	}

	@Test
	void testNoJdbcAndNoR2dbc() {
		this.contextRunner.withClassLoader(
				new FilteredClassLoader(EmbeddedDatabaseType.class, DataSource.class,
						GcpConnectionFactoryProviderMysql.class,
						GcpConnectionFactoryProviderPostgres.class))
				.run(
						context -> {
							assertThat(initializer.getEnabledR2dbcDatabaseType(context.getEnvironment())).isNull();
							assertThat(initializer.getEnabledDatabaseType(context.getEnvironment())).isNull();
							assertThat(context.getEnvironment().getProperty("spring.datasource.url")).isNull();
							assertThat(context.getEnvironment().getProperty("spring.r2dbc.url")).isNull();
						});
	}

	private String getSpringDatasourceUrl(ApplicationContext context) {
		return context.getEnvironment().getProperty("spring.datasource.url");
	}

	private String getSpringDatasourceDriverClassName(ApplicationContext context) {
		return context.getEnvironment().getProperty("spring.datasource.driver-class-name");
	}


	@Configuration
	static class Config {

		@Bean
		public CredentialsProvider credentialsProvider() {
			return NoCredentialsProvider.create();
		}
	}
}
