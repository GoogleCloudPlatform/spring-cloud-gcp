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

package com.google.cloud.spring.autoconfigure.spanner.health;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import org.junit.Test;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for Spanner health check auto-config.
 *
 * @author ikeyat
 *
 * @since 2.0.6
 */
public class SpannerHealthIndicatorAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(GcpSpannerAutoConfiguration.class,
					GcpContextAutoConfiguration.class,
					SpannerHealthIndicatorAutoConfiguration.class))
			.withUserConfiguration(TestConfiguration.class)
			.withPropertyValues("spring.cloud.gcp.spanner.project-id=test-project",
					"spring.cloud.gcp.spanner.instance-id=testInstance",
					"spring.cloud.gcp.spanner.database=testDatabase");

	@Test
	public void testSpannerHealthIndicatorCreated() {
		final String DEFAULT_QUERY = "SELECT 1";
		this.contextRunner.run(context -> {
			SpannerHealthIndicator indicator = context.getBean(SpannerHealthIndicator.class);
			assertThat(indicator).isNotNull();
			assertThat(indicator).hasFieldOrPropertyWithValue("validationQuery", DEFAULT_QUERY);
		});
	}

	@Test
	public void testSpannerHealthIndicatorCreatedWithQuery() {
		final String CUSTOM_QUERY = "SELECT 2";
		this.contextRunner.withPropertyValues("spring.cloud.gcp.spanner.health.query=" + CUSTOM_QUERY)
				.run(context -> {
					SpannerHealthIndicator indicator = context.getBean(SpannerHealthIndicator.class);
					assertThat(indicator).isNotNull();
					assertThat(indicator).hasFieldOrPropertyWithValue("validationQuery", CUSTOM_QUERY);
				});
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testSpannerHealthIndicatorNotCreated() {
		this.contextRunner.withPropertyValues("management.health.spanner.enabled=false")
				.run(context -> context.getBean(SpannerHealthIndicator.class));
	}

	/**
	 * Spring Boot config for tests.
	 */
	@Configuration
	static class TestConfiguration {

		@Bean
		public CredentialsProvider credentialsProvider() {
			return () -> mock(Credentials.class);
		}
	}
}
