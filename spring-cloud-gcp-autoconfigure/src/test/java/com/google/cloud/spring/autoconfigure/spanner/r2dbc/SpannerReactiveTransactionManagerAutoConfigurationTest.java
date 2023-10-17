package com.google.cloud.spring.autoconfigure.spanner.r2dbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.r2dbc.springdata.SpannerReactiveTransactionManager;
import io.r2dbc.spi.ConnectionFactory;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

public class SpannerReactiveTransactionManagerAutoConfigurationTest {

  private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
          // R2dbcTransactionManagerAutoConfiguration.class,
          SpannerReactiveTransactionManagerAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class);

  @Test
  public void testSpannerReactiveTransactionManagerCreated() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            SpannerReactiveTransactionManagerAutoConfiguration.class))
        .withUserConfiguration(TestConfiguration.class);

    contextRunner.run(context -> {
      // get bean by class
      assertThat(context.getBean(SpannerReactiveTransactionManager.class), notNullValue());

      // get bean by name
      assertThat(context.getBean("spannerReactiveTransactionManager"), notNullValue());

      // verifying default timestamp bound
      assertThat(context.getBean(SpannerReactiveTransactionManager.class).getTimestampBound(),
          is(TimestampBound.strong()));
    });
  }

  @Test
  public void testSpannerReactiveTransactionManagerCustomization() {
    this.contextRunner.withUserConfiguration(CustomUserConfiguration.class).run(context -> {

      // get bean by class
      assertThat(context.getBean(SpannerReactiveTransactionManager.class), notNullValue());

      // get bean by name
      assertThat(context.getBean("customSpannerReactiveTransactionManager"), notNullValue());

      // verifying custom timestamp bound
      assertThat(context.getBean(SpannerReactiveTransactionManager.class).getTimestampBound(),
          is(TimestampBound.ofMaxStaleness(5, TimeUnit.SECONDS)));
    });
  }

  // @Test
  // public void testCoExistenceOfSpannerReactiveTransactionManagerWithR2dbcTransactionManager() {
  //   this.contextRunner.run(context -> {
  //     SpannerReactiveTransactionManager spannerReactiveTransactionManager = context.getBean(
  //         SpannerReactiveTransactionManager.class);
  //     R2dbcTransactionManager r2dbcTransactionManager = context.getBean(R2dbcTransactionManager.class);
  //     assertThat(spannerReactiveTransactionManager, not(sameInstance(r2dbcTransactionManager)));
  //   });
  // }

  static class CustomUserConfiguration {
    @Bean
    public SpannerReactiveTransactionManager customSpannerReactiveTransactionManager(ConnectionFactory connectionFactory) {
      return new SpannerReactiveTransactionManager(connectionFactory, TimestampBound.ofMaxStaleness(5, TimeUnit.SECONDS));
    }

  }
  static class TestConfiguration {
    /**
     * Mock bean for ConnectionFactory.
     */
    @Bean
    public ConnectionFactory customConnectionFactory() {
      return mock(ConnectionFactory.class);
    }
  }
}