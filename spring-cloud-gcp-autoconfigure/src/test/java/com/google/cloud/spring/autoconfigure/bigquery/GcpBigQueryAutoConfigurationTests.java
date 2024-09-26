/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.bigquery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class GcpBigQueryAutoConfigurationTests {

  private static final GoogleCredentials MOCK_CREDENTIALS = mock(GoogleCredentials.class);

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpBigQueryAutoConfiguration.class, GcpContextAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues("spring.cloud.gcp.bigquery.project-id=test-project")
          .withPropertyValues("spring.cloud.gcp.bigquery.datasetName=test-dataset")
          .withPropertyValues("spring.cloud.gcp.bigquery.jsonWriterBatchSize=2000");

  @Test
  void testSettingBigQueryOptions() {
    this.contextRunner.run(
        context -> {
          BigQueryOptions bigQueryOptions = context.getBean(BigQuery.class).getOptions();
          assertThat(bigQueryOptions.getProjectId()).isEqualTo("test-project");
          assertThat(bigQueryOptions.getCredentials()).isEqualTo(MOCK_CREDENTIALS);

          BigQueryTemplate bigQueryTemplate = context.getBean(BigQueryTemplate.class);
          assertThat(bigQueryTemplate.getDatasetName()).isEqualTo("test-dataset");

          assertThat(bigQueryTemplate.getJsonWriterBatchSize()).isEqualTo(2000);
        });
  }

  @Test
  void testBigQuery_universeDomain() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.bigquery.universe-domain=myUniverseDomain")
        .run(
            ctx -> {
              BigQueryOptions options = ctx.getBean(BigQuery.class).getOptions();
              assertThat(options.getUniverseDomain()).isEqualTo("myUniverseDomain");
              assertThat(options.getResolvedApiaryHost("bigquery"))
                  .isEqualTo("https://bigquery.myUniverseDomain/");
            });
  }

  @Test
  void testBigQuery_noUniverseDomainAndEndpointSet_useClientDefault() {
    this.contextRunner.run(
        ctx -> {
          BigQueryOptions options = ctx.getBean(BigQuery.class).getOptions();
          assertThat(options.getUniverseDomain()).isNull();
          assertThat(options.getResolvedApiaryHost("bigquery"))
              .isEqualTo("https://bigquery.googleapis.com/");
        });
  }

  @Test
  void testBigQuery_endpoint() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.bigquery.endpoint=bigquery.example.com:443")
        .run(
            ctx -> {
              BigQueryOptions options = ctx.getBean(BigQuery.class).getOptions();
              assertThat(options.getResolvedApiaryHost("bigquery"))
                  .isEqualTo("https://bigquery.example.com/");
            });
  }

  @Test
  void testBigQuery_bothEndpointAndUniverseDomainSet() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.bigquery.endpoint=bigquery.example.com:123")
        .withPropertyValues("spring.cloud.gcp.bigquery.universe-domain=myUniverseDomain")
        .run(
            ctx -> {
              BigQueryOptions options = ctx.getBean(BigQuery.class).getOptions();
              assertThat(options.getResolvedApiaryHost("bigquery"))
                  .isEqualTo("https://bigquery.example.com/");
              assertThat(options.getUniverseDomain()).isEqualTo("myUniverseDomain");
            });
  }

  @Test
  void testBigQueryWrite_universeDomain() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.bigquery.universe-domain=myUniverseDomain")
        .run(
            ctx -> {
              BigQueryWriteClient writeClient = ctx.getBean(BigQueryWriteClient.class);
              assertThat(writeClient.getSettings().getUniverseDomain())
                  .isEqualTo("myUniverseDomain");
            });
  }

  @Test
  void testBigQueryWrite_endpoint() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.bigquery.endpoint=bigquerystorage.example.com:123")
        .run(
            ctx -> {
              BigQueryWriteClient client = ctx.getBean(BigQueryWriteClient.class);
              assertThat(client.getSettings().getEndpoint())
                  .isEqualTo("bigquerystorage.example.com:123");
            });
  }

  @Test
  void testBigQueryWrite_bothUniverseDomainAndEndpointSet() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.bigquery.universe-domain=myUniverseDomain")
        .withPropertyValues(
            "spring.cloud.gcp.bigquery.endpoint=bigquerystorage.example.com:123")
        .run(
            ctx -> {
              BigQueryWriteClient client = ctx.getBean(BigQueryWriteClient.class);
              assertThat(client.getSettings().getUniverseDomain()).isEqualTo("myUniverseDomain");
              assertThat(client.getSettings().getEndpoint())
                  .isEqualTo("bigquerystorage.example.com:123");
            });
  }

  @Test
  void testBigQueryWrite_noUniverseDomainOrEndpointSet_useClientDefault() {
    this.contextRunner.run(
        ctx -> {
          BigQueryWriteClient client = ctx.getBean(BigQueryWriteClient.class);
          assertThat(client.getSettings().getUniverseDomain()).isEqualTo("googleapis.com");
          assertThat(client.getSettings().getEndpoint())
              .isEqualTo("bigquerystorage.googleapis.com:443");
        });
  }

  /** Spring Boot config for tests. */
  @AutoConfigurationPackage
  static class TestConfiguration {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> MOCK_CREDENTIALS;
    }
  }
}
