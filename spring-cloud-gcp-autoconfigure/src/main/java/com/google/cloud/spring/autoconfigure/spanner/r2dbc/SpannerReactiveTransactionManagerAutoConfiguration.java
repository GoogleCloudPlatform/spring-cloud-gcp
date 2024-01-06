package com.google.cloud.spring.autoconfigure.spanner.r2dbc;

import com.google.cloud.spanner.r2dbc.springdata.SpannerReactiveTransactionManager;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@AutoConfiguration
@ConditionalOnClass({SpannerReactiveTransactionManager.class,  R2dbcTransactionManager.class })
@ConditionalOnSingleCandidate(ConnectionFactory.class)
public class SpannerReactiveTransactionManagerAutoConfiguration {

  /**
   * This TransactionManager bean is intended for standard use cases. It is identical to the
   * {@link org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration} autoconfiguration,
   * except that it is preferred for standard use cases. As a result, it is annotated with the
   * {@link Primary} annotation.
   */
  @Bean
  @Primary
  @ConditionalOnMissingBean(ReactiveTransactionManager.class)
  public R2dbcTransactionManager connectionFactoryTransactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  /**
   * This TransactionManager bean is intended for spanner specific use cases. Ex: Staleness configuration.
   * {@link org.springframework.transaction.annotation.Transactional} annotation can be used to use a
   * custom transaction manager for specific use cases.
   *
   * {@code @Transactional(transactionManager = "spannerReactiveTransactionManager")}
   */
  @Bean
  @ConditionalOnMissingBean
  public SpannerReactiveTransactionManager spannerReactiveTransactionManager(ConnectionFactory connectionFactory) {
    return new SpannerReactiveTransactionManager(connectionFactory);
  }

}
