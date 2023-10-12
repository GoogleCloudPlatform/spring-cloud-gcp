package com.google.cloud.spanner.r2dbc.springdata;

import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.r2dbc.v2.SpannerConstants;
import com.google.common.base.Preconditions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.TransactionDefinition;

public class SpannerTransactionDefinition implements TransactionDefinition {

  private final TransactionDefinition delegate;
  private final TimestampBound timestampBound;

  public SpannerTransactionDefinition(TransactionDefinition delegate,
      TimestampBound timestampBound) {
    Preconditions.checkArgument(timestampBound != null, "TimestampBound shouldn't be null");
    this.delegate = delegate;
    this.timestampBound = timestampBound;
  }

  @Override
  public <T> T getAttribute(Option<T> option) {
    if (SpannerConstants.TIMESTAMP_BOUND.equals(option)) {
      return (T) this.timestampBound;
    }
    return this.delegate.getAttribute(option);
  }
}
