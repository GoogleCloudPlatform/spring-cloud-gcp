package com.google.cloud.spring.autoconfigure.spanner.r2dbc;

import com.google.cloud.spanner.r2dbc.springdata.SpannerReactiveTransactionManager;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

@AutoConfiguration
@ConditionalOnClass({SpannerReactiveTransactionManager.class,  R2dbcTransactionManager.class })
@ConditionalOnSingleCandidate(ConnectionFactory.class)
public class SpannerReactiveTransactionManagerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SpannerReactiveTransactionManager spannerReactiveTransactionManager(ConnectionFactory connectionFactory) {
    return new SpannerReactiveTransactionManager(connectionFactory);
  }

}
