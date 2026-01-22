/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.spanner.test;

import com.google.cloud.spanner.SessionPoolOptions;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.data.spanner.core.it.SpannerTemplateIntegrationTests.TemplateTransactionalService;
import com.google.cloud.spring.data.spanner.repository.config.EnableSpannerRepositories;
import com.google.cloud.spring.data.spanner.repository.it.SpannerRepositoryIntegrationTests.TradeRepositoryTransactionalService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for integration tests for Spanner.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("application-test.properties")
@EnableSpannerRepositories
public class IntegrationTestConfiguration {

  @Value("${test.integration.spanner.db}")
  private String databaseName;

  @Value("${test.integration.spanner.instance}")
  private String instanceId;

  private static final String TABLE_SUFFIX = String.valueOf(System.currentTimeMillis());

  @Bean
  public String getDatabaseName() {
    return this.databaseName;
  }

  @Bean
  public String getInstanceId() {
    return this.instanceId;
  }

  @Bean
  public String getProjectId() {
    String projectId = new DefaultGcpProjectIdProvider().getProjectId();
    return (projectId != null) ? projectId : "test-project";
  }

  @Bean
  public com.google.auth.Credentials getCredentials() {
    // If the emulator host is set, we use NoCredentials to avoid looking for local ADC.
    if (System.getProperty("spring.cloud.gcp.spanner.emulator-host") != null
        || System.getenv("SPANNER_EMULATOR_HOST") != null) {
      return com.google.cloud.NoCredentials.getInstance();
    }

    try {
      return new DefaultCredentialsProvider(Credentials::new).getCredentials();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Bean
  public TemplateTransactionalService templateTransactionalService() {
    return new TemplateTransactionalService();
  }

  @Bean
  public TradeRepositoryTransactionalService tradeRepositoryTransactionalService() {
    return new TradeRepositoryTransactionalService();
  }

  @Bean
  public SpannerOptions spannerOptions() {
    String emulatorHost = System.getProperty("spring.cloud.gcp.spanner.emulator-host");
    if (emulatorHost == null) {
      emulatorHost = System.getenv("SPANNER_EMULATOR_HOST");
    }

    SpannerOptions.Builder builder = SpannerOptions.newBuilder()
        .setProjectId(getProjectId())
        .setSessionPoolOption(SessionPoolOptions.newBuilder().setMaxSessions(10).build())
        .setCredentials(getCredentials());

    if (emulatorHost != null) {
      String hostWithProtocol =
          emulatorHost.startsWith("http") ? emulatorHost : "http://" + emulatorHost;
      builder.setHost(hostWithProtocol);

      String hostRaw = emulatorHost.replace("http://", "").replace("https://", "");
      builder.setEmulatorHost(hostRaw);
    }

    return builder.build();
  }
}