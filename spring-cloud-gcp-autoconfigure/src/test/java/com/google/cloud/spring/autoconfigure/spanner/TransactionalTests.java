package com.google.cloud.spring.autoconfigure.spanner;

import java.util.Collections;
import java.util.Map;

import com.google.cloud.spanner.AbortedException;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;


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

			BasicRepository repo = context.getBean(BasicRepository.class);
			assertNotNull(repo);

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
			BasicRepository spannerRepository;

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


@Repository
interface BasicRepository extends SpannerRepository<Data, String> {

}

class Data {
	@Id
	String id;

	Integer num;
}
