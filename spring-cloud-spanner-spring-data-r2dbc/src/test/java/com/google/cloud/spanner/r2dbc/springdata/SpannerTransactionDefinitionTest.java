/*
 * Copyright 2022-2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.google.cloud.spanner.r2dbc.springdata;

import static com.google.cloud.spanner.r2dbc.v2.SpannerConstants.TIMESTAMP_BOUND;
import static io.r2dbc.spi.IsolationLevel.SERIALIZABLE;
import static io.r2dbc.spi.TransactionDefinition.ISOLATION_LEVEL;
import static io.r2dbc.spi.TransactionDefinition.LOCK_WAIT_TIMEOUT;
import static io.r2dbc.spi.TransactionDefinition.NAME;
import static io.r2dbc.spi.TransactionDefinition.READ_ONLY;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.TimestampBound;
import io.r2dbc.spi.TransactionDefinition;
import java.time.Duration;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class SpannerTransactionDefinitionTest {

  private final Timestamp timestamp = Timestamp.now();

  private final TransactionDefinition delegate = new TestTransactionDefinition.Builder()
      .with(ISOLATION_LEVEL, SERIALIZABLE)
      .with(READ_ONLY, true)
      .with(NAME, "custom name")
      .with(LOCK_WAIT_TIMEOUT, Duration.of(5, SECONDS))
      .build();

  private final TransactionDefinition spannerTransactionDefinition = new SpannerTransactionDefinition(
      delegate, TimestampBound.ofReadTimestamp(timestamp)
  );

  @Test
  public void testIsolation() {
    assertThat(spannerTransactionDefinition.getAttribute(ISOLATION_LEVEL), is(SERIALIZABLE));
  }

  @Test
  public void testReadOnly() {
    assertThat(spannerTransactionDefinition.getAttribute(READ_ONLY), is(true));
  }

  @Test
  public void testName() {
    assertThat(spannerTransactionDefinition.getAttribute(NAME), is("custom name"));
  }

  @Test
  public void testLockWaitTimeout() {
    assertThat(spannerTransactionDefinition.getAttribute(LOCK_WAIT_TIMEOUT), is(Duration.of(5, SECONDS)));
  }

  @Test
  public void testTimestampBound() {
    assertThat(spannerTransactionDefinition.getAttribute(TIMESTAMP_BOUND), is(TimestampBound.ofReadTimestamp(timestamp)));
  }

  @Test
  public void testTimestampBoundShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> new SpannerTransactionDefinition(delegate, null));
  }
}