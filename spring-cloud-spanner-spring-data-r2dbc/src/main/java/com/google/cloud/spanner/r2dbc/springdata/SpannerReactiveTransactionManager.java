package com.google.cloud.spanner.r2dbc.springdata;

import com.google.cloud.spanner.TimestampBound;
import io.r2dbc.spi.TransactionDefinition;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

public class SpannerReactiveTransactionManager extends R2dbcTransactionManager {

  private final TimestampBound timestampBound;

  public SpannerReactiveTransactionManager(TimestampBound timestampBound) {
    this.timestampBound = timestampBound;
  }

  public SpannerReactiveTransactionManager() {
    this.timestampBound = TimestampBound.strong();
  }

  @Override
  protected TransactionDefinition createTransactionDefinition(
      org.springframework.transaction.TransactionDefinition definition) {
    TransactionDefinition delegate = super.createTransactionDefinition(definition);
    return new SpannerTransactionDefinition(delegate, timestampBound);
  }
}
