/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.autoconfigure.TestUtils;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.storage.GoogleStorageResource;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/** Config for Storage auto config tests. */
class GcpStorageAutoConfigurationTests {

  private static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(GcpStorageAutoConfiguration.class))
          .withPropertyValues("spring.cloud.gcp.storage.project-id=" + PROJECT_NAME)
          .withUserConfiguration(TestConfiguration.class);

  @Test
  void testValidObject() {
    this.contextRunner
        .withUserConfiguration(TestStorageConfiguration.class)
        .run(
            context -> {
              Resource resource = context.getBean("mockResource", Resource.class);
              assertThat(resource.contentLength()).isEqualTo(4096);
            });
  }

  @Test
  void testAutoCreateFilesTrueByDefault() {
    this.contextRunner
        .withUserConfiguration(TestStorageConfiguration.class)
        .run(
            context -> {
              Resource resource = context.getBean("mockResource", Resource.class);
              assertThat(((GoogleStorageResource) resource).isAutoCreateFiles()).isTrue();
            });
  }

  @Test
  void testAutoCreateFilesRespectsProperty() {
    this.contextRunner
        .withUserConfiguration(TestStorageConfiguration.class)
        .withPropertyValues("spring.cloud.gcp.storage.auto-create-files=false")
        .run(
            context -> {
              Resource resource = context.getBean("mockResource", Resource.class);
              assertThat(((GoogleStorageResource) resource).isAutoCreateFiles()).isFalse();
            });
  }

  @Test
  void testUniverseDomain() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.storage.universe-domain=example.com")
        .run(
            context -> {
              Storage storage = context.getBean("storage", Storage.class);
              assertThat(storage.getOptions().getUniverseDomain()).isEqualTo("example.com");
              assertThat(storage.getOptions().getHost()).isEqualTo("https://storage.example.com/");
            });
  }

  @Test
  void testHost() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.storage.host=https://storage.example.com/")
        .run(
            context -> {
              Storage storage = context.getBean("storage", Storage.class);
              assertThat(storage.getOptions().getHost()).isEqualTo("https://storage.example.com/");
            });
  }

  @Test
  void testUniverseDomainAndHostSet() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.storage.universe-domain=example.com",
            "spring.cloud.gcp.storage.host=https://storage.example.com")
        .run(
            context -> {
              Storage storage = context.getBean("storage", Storage.class);
              assertThat(storage.getOptions().getUniverseDomain()).isEqualTo("example.com");
              assertThat(storage.getOptions().getHost()).isEqualTo("https://storage.example.com/");
            });
  }

  @Test
  void testNoUniverseDomainOrHostSet_useDefaults() {
    this.contextRunner.run(
        context -> {
          Storage storage = context.getBean("storage", Storage.class);
          assertThat(storage.getOptions().getUniverseDomain()).isNull();
          assertThat(storage.getOptions().getHost()).isEqualTo("https://storage.googleapis.com/");
        });
  }

  @Test
  void testInvalidHost_throwsException() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.storage.host=storage.example.com")
        .run(
            context -> {
              Exception exception =
                  assertThrows(Exception.class, () -> context.getBean("storage", Storage.class));
              assertThat(exception).hasRootCauseInstanceOf(IllegalArgumentException.class);
              assertThat(exception)
                  .hasRootCauseMessage(
                      "Invalid host format: storage.example.com. Please verify that the specified host follows the 'https://${service}.${universeDomain}/' format");
            });
  }

  @Configuration
  static class TestConfiguration {

    @Bean
    public static CredentialsProvider googleCredentials() {
      return () -> TestUtils.MOCK_CREDENTIALS;
    }

    @Bean
    public static GcpProjectIdProvider gcpProjectIdProvider() {
      return () -> "default-project";
    }
  }

  @Configuration
  static class TestStorageConfiguration {

    @Value("gs://test-spring/images/spring.png")
    private Resource remoteResource;

    @Bean(name = "mockResource")
    public Resource getResource() throws IOException {
      return this.remoteResource;
    }

    @Bean
    public static Storage mockStorage() throws Exception {
      Storage storage = mock(Storage.class);
      BlobId validBlob = BlobId.of("test-spring", "images/spring.png");
      Blob mockedBlob = mock(Blob.class);
      when(mockedBlob.exists()).thenReturn(true);
      when(mockedBlob.getSize()).thenReturn(4096L);
      when(storage.get(validBlob)).thenReturn(mockedBlob);
      return storage;
    }
  }
}
