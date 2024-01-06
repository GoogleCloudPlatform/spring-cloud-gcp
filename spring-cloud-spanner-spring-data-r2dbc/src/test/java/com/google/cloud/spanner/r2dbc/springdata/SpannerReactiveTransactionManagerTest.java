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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_SERIALIZABLE;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_MANDATORY;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NEVER;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_SUPPORTS;

import com.google.cloud.spanner.TimestampBound;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.TransactionDefinition;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class SpannerReactiveTransactionManagerTest {

  @Mock
  private ConnectionFactory connectionFactory;
  @Mock
  private Connection connection;
  private R2dbcTransactionManager reactiveTransactionManager;

  @Before
  public void setUp() {
    doReturn(Mono.just(connection)).when(connectionFactory).create();
    when(connection.beginTransaction(any())).thenReturn(Mono.empty());
    reactiveTransactionManager = new SpannerReactiveTransactionManager(mock(ConnectionFactory.class));
    reactiveTransactionManager.setConnectionFactory(connectionFactory);
  }

  @Test
  public void shouldIncludeCustomTimestampBoundInTransactionDefinitionDuringBeginTransaction() {
    // setup
    TimestampBound fiveSecondStaleness = TimestampBound.ofMaxStaleness(5, TimeUnit.SECONDS);
    R2dbcTransactionManager reactiveTransactionManager = new SpannerReactiveTransactionManager(
        mock(ConnectionFactory.class), fiveSecondStaleness); // configured with 5 second staleness
    reactiveTransactionManager.setConnectionFactory(connectionFactory);

    // action
    reactiveTransactionManager.getReactiveTransaction(getTransactionDefinition())
        .contextWrite(TransactionContextManager.getOrCreateContext())
        .contextWrite(TransactionContextManager.getOrCreateContextHolder()).block();

    // assertion
    ArgumentCaptor<TransactionDefinition> argumentCaptor = ArgumentCaptor.forClass(
        TransactionDefinition.class);
    verify(connection).beginTransaction(argumentCaptor.capture());
    TransactionDefinition actualTransactionDefinition = argumentCaptor.getValue();
    // standard attribute assertions
    assertThat(actualTransactionDefinition.getAttribute(ISOLATION_LEVEL), is(SERIALIZABLE));
    assertThat(actualTransactionDefinition.getAttribute(READ_ONLY), is(false));
    assertThat(actualTransactionDefinition.getAttribute(NAME), is("custom name"));
    assertThat(actualTransactionDefinition.getAttribute(LOCK_WAIT_TIMEOUT),
        is(Duration.of(5, SECONDS)));
    // spanner specific attribute assertion
    assertThat(actualTransactionDefinition.getAttribute(TIMESTAMP_BOUND), is(fiveSecondStaleness));
  }

  @Test
  public void shouldIncludeDefaultCustomTimestampBoundInTransactionDefinitionDuringBeginTransaction() {
    // action
    reactiveTransactionManager.getReactiveTransaction(getTransactionDefinition())
        .contextWrite(TransactionContextManager.getOrCreateContext())
        .contextWrite(TransactionContextManager.getOrCreateContextHolder()).block();

    // assertion
    ArgumentCaptor<TransactionDefinition> argumentCaptor = ArgumentCaptor.forClass(
        TransactionDefinition.class);
    verify(connection).beginTransaction(argumentCaptor.capture());
    TransactionDefinition actualTransactionDefinition = argumentCaptor.getValue();
    // standard attribute assertions
    assertThat(actualTransactionDefinition.getAttribute(ISOLATION_LEVEL), is(SERIALIZABLE));
    assertThat(actualTransactionDefinition.getAttribute(READ_ONLY), is(false));
    assertThat(actualTransactionDefinition.getAttribute(NAME), is("custom name"));
    assertThat(actualTransactionDefinition.getAttribute(LOCK_WAIT_TIMEOUT),
        is(Duration.of(5, SECONDS)));
    // spanner specific attribute assertion
    assertThat(actualTransactionDefinition.getAttribute(TIMESTAMP_BOUND),
        is(TimestampBound.strong()));

  }

  @Test
  public void shouldValidateIsolationDuringBeginTransaction() {
    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithIsolation(ISOLATION_SERIALIZABLE)) // ISOLATION_SERIALIZABLE
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithIsolation(ISOLATION_READ_UNCOMMITTED)) // ISOLATION_READ_UNCOMMITTED
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectError(UnsupportedOperationException.class).verify();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithIsolation(ISOLATION_READ_COMMITTED)) // ISOLATION_READ_COMMITTED
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectError(UnsupportedOperationException.class).verify();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithIsolation(ISOLATION_REPEATABLE_READ)) // ISOLATION_REPEATABLE_READ
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectError(UnsupportedOperationException.class).verify();
  }

  @Test
  public void shouldValidatePropagationDuringBeginTransaction() {
    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_REQUIRED)) // PROPAGATION_REQUIRED
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_SUPPORTS)) // PROPAGATION_SUPPORTS
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_MANDATORY)) // PROPAGATION_MANDATORY
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_REQUIRES_NEW)) // PROPAGATION_REQUIRES_NEW
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_NOT_SUPPORTED)) // PROPAGATION_NOT_SUPPORTED
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_NEVER)) // PROPAGATION_NEVER
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectSubscription().expectComplete();

    StepVerifier.create(
            reactiveTransactionManager.getReactiveTransaction(
                    transactionDefinitionWithPropagation(PROPAGATION_NESTED)) // PROPAGATION_NESTED
                .contextWrite(TransactionContextManager.getOrCreateContext())
                .contextWrite(TransactionContextManager.getOrCreateContextHolder()))
        .expectError(UnsupportedOperationException.class).verify();
  }

  private static DefaultTransactionDefinition getTransactionDefinition() {
    return transactionDefinitionWithIsolation(ISOLATION_SERIALIZABLE);
  }

  private static DefaultTransactionDefinition transactionDefinitionWithIsolation(
      int isolationLevel) {
    DefaultTransactionDefinition delegate = new DefaultTransactionDefinition();
    delegate.setName("custom name");
    delegate.setIsolationLevel(isolationLevel);
    delegate.setReadOnly(false);
    delegate.setTimeout(5);
    return delegate;
  }

  private static DefaultTransactionDefinition transactionDefinitionWithPropagation(
      int propagationBehavior) {
    DefaultTransactionDefinition delegate = new DefaultTransactionDefinition();
    delegate.setName("custom name");
    delegate.setIsolationLevel(ISOLATION_SERIALIZABLE);
    delegate.setReadOnly(false);
    delegate.setTimeout(5);
    delegate.setPropagationBehavior(propagationBehavior);
    return delegate;
  }


}