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

package com.google.cloud.spring.autoconfigure.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfiguration;
import com.google.cloud.spring.logging.TraceIdLoggingWebMvcInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import zipkin2.reporter.Reporter;

/** Tests for auto-config. */
class StackdriverLoggingAutoConfigurationTests {

  private WebApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner = new WebApplicationContextRunner()
        .withUserConfiguration(TestConfiguration.class)
        .withConfiguration(
            AutoConfigurations.of(
                StackdriverLoggingAutoConfiguration.class, GcpContextAutoConfiguration.class));
  }

  @Test
  void testDisabledConfiguration() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.logging.enabled=false")
        .run(
            context ->
                assertThat(context.getBeansOfType(TraceIdLoggingWebMvcInterceptor.class))
                    .isEmpty());
  }

  @Test
  void testNonWebAppConfiguration() {
    new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                StackdriverLoggingAutoConfiguration.class, GcpContextAutoConfiguration.class))
        .withUserConfiguration(TestConfiguration.class)
        .run(
            context ->
                assertThat(context.getBeansOfType(TraceIdLoggingWebMvcInterceptor.class))
                    .isEmpty());
  }

  @Test
  void testNonServletConfiguration() {
    new ReactiveWebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                StackdriverLoggingAutoConfiguration.class, GcpContextAutoConfiguration.class))
        .withUserConfiguration(TestConfiguration.class)
        .run(
            context ->
                assertThat(context.getBeansOfType(TraceIdLoggingWebMvcInterceptor.class))
                    .isEmpty());
  }

  @Test
  void testRegularConfiguration() {
    this.contextRunner.run(
        context ->
            assertThat(context.getBeansOfType(TraceIdLoggingWebMvcInterceptor.class)).hasSize(1));
  }

  @Test
  void testWithStackdriverTraceAutoConfiguration() {
    this.contextRunner
        .withConfiguration(AutoConfigurations.of(StackdriverTraceAutoConfiguration.class))
        .withPropertyValues("spring.cloud.gcp.project-id=pop-1")
        .run(
            context ->
                assertThat(context.getBeansOfType(TraceIdLoggingWebMvcInterceptor.class))
                    .isEmpty());
  }

  private static class TestConfiguration {

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> mock(Credentials.class);
    }

    @Bean
    public Reporter stackdriverReporter() {
      return r -> {};
    }
  }
}
