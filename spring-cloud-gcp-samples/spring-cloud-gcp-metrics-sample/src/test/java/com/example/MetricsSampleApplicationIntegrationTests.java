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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.api.MetricDescriptor;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for the metrics sample app. */
@EnabledIfSystemProperty(named = "it.metrics", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = MetricsApplication.class)
@AutoConfigureMetrics // needed to enable metrics export in Spring Boot tests
class MetricsSampleApplicationIntegrationTests {

  @Autowired private GcpProjectIdProvider projectIdProvider;

  @Autowired private TestRestTemplate testRestTemplate;

  @LocalServerPort private int port;

  private MetricServiceClient metricClient;

  @BeforeEach
  void setupLogging() throws IOException {
    this.metricClient = MetricServiceClient.create();
  }

  @Test
  void testMetricRecordedInStackdriver() {
    String projectId = this.projectIdProvider.getProjectId();

    String id = "integration_test_" + UUID.randomUUID().toString().replace('-', '_');
    String url = String.format("http://localhost:%s/%s", this.port, id);

    ResponseEntity<String> responseEntity =
        this.testRestTemplate.getForEntity(url, String.class, String.class);
    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

    String metricType = "custom.googleapis.com/" + id;
    String metricName = "projects/" + projectId + "/metricDescriptors/" + metricType;

    await()
        .atMost(4, TimeUnit.MINUTES)
        .pollInterval(5, TimeUnit.SECONDS)
        .ignoreExceptionsMatching(
            e -> e.getMessage().contains("Could not find descriptor for metric"))
        .untilAsserted(
            () -> {
              MetricDescriptor metricDescriptor = this.metricClient.getMetricDescriptor(metricName);
              assertThat(metricDescriptor.getName()).isEqualTo(metricName);
              assertThat(metricDescriptor.getType()).isEqualTo(metricType);
            });
  }
}
