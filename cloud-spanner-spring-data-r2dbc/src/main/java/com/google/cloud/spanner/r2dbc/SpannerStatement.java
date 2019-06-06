/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.spanner.r2dbc;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.cloud.spanner.r2dbc.codecs.Codec;
import com.google.cloud.spanner.r2dbc.codecs.Codecs;
import com.google.cloud.spanner.r2dbc.codecs.DefaultCodecs;
import com.google.cloud.spanner.r2dbc.result.PartialResultRowExtractor;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.protobuf.Struct;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Type;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link Statement} implementation for Cloud Spanner.
 */
public class SpannerStatement implements Statement {

  private static final int DEFAULT_PARTIAL_FETCH_SIZE = 1;

  private Client client;

  private Session session;

  private SpannerTransactionContext transaction;

  private String sql;

  private Integer partialResultSetFetchSize;

  private List<Struct> bindingsStucts = new ArrayList<>();

  private Codecs codecs = new DefaultCodecs();

  private Map<String, Type> types = new HashMap<>();

  private Struct.Builder currentBindingsBuilder;

  private Map<String, Codec> resolvedCodecs = new HashMap<>();

  /**
   * Creates a Spanner statement for a given SQL statement.
   *
   * <p>If no transaction is present, a temporary strongly consistent readonly transaction will be
   * used.
   * @param client cloud spanner client to use for performing the query operation
   * @param session current cloud spanner session
   * @param transaction current cloud spanner transaction, or empty if no transaction is started
   * @param sql the query to execute
   */
  public SpannerStatement(
      Client client,
      Session session,
      @Nullable SpannerTransactionContext transaction,
      String sql) {

    this.client = client;
    this.session = session;
    this.transaction = transaction;
    this.sql = Assert.requireNonNull(sql, "SQL string can not be null");
  }

  @Override
  public Statement add() {
    if (this.currentBindingsBuilder != null) {
      this.bindingsStucts.add(this.currentBindingsBuilder.build());
    }
    this.currentBindingsBuilder = null;
    return this;
  }

  @Override
  public Statement bind(Object identifier, Object value) {
    Assert.requireNonNull(identifier, "Identifier must not be null.");
    Assert.requireNonNull(value, "Value bound must not be null.");
    if (identifier instanceof String) {
      String paramName = (String) identifier;
      // we assume all parameters with the same name have the same type

      Object valToStore;
      Class classToStore;

      if (value.getClass().equals(TypedNull.class)) {
        valToStore = null;
        classToStore = ((TypedNull) value).getType();
      } else {
        valToStore = value;
        classToStore = value.getClass();
      }

      Codec codec = this.resolvedCodecs
          .computeIfAbsent(paramName, n -> this.codecs.getCodec(classToStore));
      if (this.currentBindingsBuilder == null) {
        this.currentBindingsBuilder = Struct.newBuilder();
      }
      this.currentBindingsBuilder.putFields(paramName, codec.encode(valToStore));
      if (this.bindingsStucts.isEmpty()) {
        // first binding, fill types map
        this.types.put(paramName, Type.newBuilder().setCode(codec.getTypeCode()).build());
      }
      return this;
    }
    throw new IllegalArgumentException("Only String identifiers are supported");
  }

  @Override
  public Statement bind(int i, Object o) {
    throw new UnsupportedOperationException("Only named parameters are supported");
  }

  @Override
  public Statement bindNull(Object identifier, Class<?> type) {
    return bind(identifier, new TypedNull(type));
  }

  @Override
  public Statement bindNull(int i, Class<?> type) {
    throw new UnsupportedOperationException("Only named parameters are supported");
  }

  @Override
  public Publisher<? extends Result> execute() {
    add();
    if (this.bindingsStucts.size() == 0) {
      this.bindingsStucts.add(Struct.newBuilder().build());
    }
    Flux<Struct> structFlux = Flux.fromIterable(this.bindingsStucts);

    if (isSelectQuery()) {
      return structFlux.flatMap(this::runSingleStatement);
    }
    // DML statements have to be executed sequentially because they need seqNo to be in order
    return structFlux.concatMapDelayError(this::runSingleStatement);
  }

  private boolean isSelectQuery() {
    return this.sql.trim().toLowerCase().startsWith("select");
  }

  private Mono<? extends Result> runSingleStatement(Struct params) {
    PartialResultRowExtractor partialResultRowExtractor = new PartialResultRowExtractor();

    return this.client
        .executeStreamingSql(this.session, this.transaction, this.sql, params, this.types)
        .switchOnFirst((signal, flux) -> {
          if (signal.hasError()) {
            return Mono.error(signal.getThrowable());
          }
          if (signal.isOnComplete()) {
            // Empty response returned from Cloud Spanner.
            return flux.then(Mono.just(new SpannerResult(Flux.empty(), Mono.just(0))));
          }

          PartialResultSet firstPartialResultSet = signal.get();
          if (isDmlQuery(firstPartialResultSet)) {
            Mono<Integer> rowsChanged =
                flux.last()
                    .map(partialResultSet ->
                        Math.toIntExact(partialResultSet.getStats().getRowCountExact()));

            return Mono.just(new SpannerResult(Flux.empty(), rowsChanged));
          } else {
            return Mono.just(new SpannerResult(
                flux.flatMapIterable(partialResultRowExtractor, getPartialResultSetFetchSize()),
                Mono.just(0)));
          }
        })
        .next();
  }

  /**
   * Returns whether we think the query is a DML query.
   */
  private static boolean isDmlQuery(PartialResultSet firstPartialResultSet) {
    return firstPartialResultSet.getMetadata().getRowType().getFieldsList().isEmpty();
  }

  /**
   * Allows customizing the number of {@link PartialResultSet} objects to request at a time.
   * @param fetchSize prefetch size to request from Cloud Spanner
   */
  public void setPartialResultSetFetchSize(Integer fetchSize) {
    this.partialResultSetFetchSize = fetchSize;
  }

  public int getPartialResultSetFetchSize() {
    return this.partialResultSetFetchSize != null
        ? this.partialResultSetFetchSize : DEFAULT_PARTIAL_FETCH_SIZE;
  }


  /**
   * A helper class. Spanner queries require nulls to be bound with their column's actual type.
   */
  private static class TypedNull {

    private final Class type;

    private TypedNull(Class type) {
      this.type = type;
    }

    public Class getType() {
      return this.type;
    }
  }
}
