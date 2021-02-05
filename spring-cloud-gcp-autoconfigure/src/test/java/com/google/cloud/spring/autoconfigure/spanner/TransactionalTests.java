/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.spanner;

import java.util.Collections;
import java.util.Map;

import com.google.cloud.spanner.AbortedException;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TransactionalTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(GcpSpannerAutoConfiguration.class,
					GcpContextAutoConfiguration.class, SpannerTransactionManagerAutoConfiguration.class,
					SpannerRepositoriesAutoConfiguration.class))
			.withUserConfiguration(TestConfig.class)
			.withPropertyValues("spring.cloud.gcp.spanner.project-id=test-project",
					"spring.cloud.gcp.spanner.instance-id=testInstance",
					"spring.cloud.gcp.spanner.database=testDatabase");



	@Test
	public void testTestRepositoryCreated() {
		this.contextRunner.run((context) -> {

			TestRepository repo = context.getBean(TestRepository.class);
			assertThat(repo).isNotNull();

			TestConfig.TransactionalService service = context.getBean(TestConfig.TransactionalService.class);
			assertThatThrownBy(() -> service.transactionalSave())
					.hasCauseInstanceOf(AbortedException.class);

			// TODO: assert to make sure the session was released into the wild
			//SpannerTransactionManager tm = context.getBean(SpannerTransactionManager.class);

		});
	}


	@EnableTransactionManagement
	@AutoConfigurationPackage
	static class TestConfig {

		@Bean
		public Spanner spanner() {
			Map<String, Object> responses = ReplacingGrpcCallInterceptor.fakeSpannerResponses();
			responses.put("Commit", Status.Code.ABORTED);
			return SpannerOptions.newBuilder()
					.setInterceptorProvider(() -> Collections.singletonList(
							new ReplacingGrpcCallInterceptor(responses)))
					.build()
					.getService();

		}

		@Bean
		public TransactionalService transactionalService() {
			return new TransactionalService();
		}

		static class TransactionalService {
			@Autowired
			TestRepository spannerRepository;

			@Transactional
			public void transactionalSave() {
				Data data = new Data();
				data.id = "new object id";
				data.num = 42;
				spannerRepository.save(data);
			}
		}


	}



}

