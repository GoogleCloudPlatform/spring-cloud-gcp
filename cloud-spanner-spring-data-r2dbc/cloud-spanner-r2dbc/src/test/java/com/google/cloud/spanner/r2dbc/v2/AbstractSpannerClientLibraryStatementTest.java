/*
 * Copyright 2019-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Value;
import com.google.spanner.v1.ExecuteSqlRequest.QueryOptions;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AbstractSpannerClientLibraryStatementTest {

  DatabaseClientReactiveAdapter mockAdapter;

  /** Mocks DatabaseClientReactiveAdapter to return hard-coded results for DML running methods. */
  @BeforeEach
  public void setUpAdapterResponse() {
    this.mockAdapter = mock(DatabaseClientReactiveAdapter.class);
    when(this.mockAdapter.runDmlStatement(any(Statement.class)))
        .thenReturn(Mono.just(new SpannerClientLibraryResult(Flux.empty(), 19)));

    when(this.mockAdapter.runBatchDml(anyList()))
        .thenReturn(Flux.just(
            new SpannerClientLibraryResult(Flux.empty(), 7),
            new SpannerClientLibraryResult(Flux.empty(), 11),
            new SpannerClientLibraryResult(Flux.empty(), 13)
        ));
    when(this.mockAdapter.getQueryOptions()).thenReturn(QueryOptions.getDefaultInstance());
  }

  @Test
  void noParametersSendsSingleStatement() {
    String query = "SELECT * FROM tbl";
    FakeStatement statement = new FakeStatement(this.mockAdapter, query);

    StepVerifier.create(Flux.from(statement.execute()).flatMap(r -> r.getRowsUpdated()))
        .expectNext(19)
        .verifyComplete();

    ArgumentCaptor<Statement> capturedStatement = ArgumentCaptor.forClass(Statement.class);
    verify(this.mockAdapter).runDmlStatement(capturedStatement.capture());
    assertThat(capturedStatement.getValue().getSql()).isEqualTo(query);
    assertThat(capturedStatement.getValue().getParameters()).isEmpty();
    verify(this.mockAdapter, times(0)).runSelectStatement(any());
    verify(this.mockAdapter, times(0)).runBatchDml(any());
  }

  @Test
  void singleSetOfParametersWithNoAddSendsOneStatement() {
    String query = "SELECT * FROM tbl WHERE col1=@one AND col2=@two AND col3=@three";
    FakeStatement statement = new FakeStatement(this.mockAdapter, query);

    statement.bind("one", "111");
    statement.bind("two", "222");
    statement.bind("three", "333");

    StepVerifier.create(Flux.from(statement.execute()).flatMap(r -> r.getRowsUpdated()))
        .expectNext(19)
        .verifyComplete();

    ArgumentCaptor<Statement> capturedStatement = ArgumentCaptor.forClass(Statement.class);
    verify(this.mockAdapter).runDmlStatement(capturedStatement.capture());
    assertThat(capturedStatement.getValue().getSql()).isEqualTo(query);
    assertThat(capturedStatement.getValue().getParameters())
        .hasSize(3)
        .containsEntry("one", Value.string("111"))
        .containsEntry("two", Value.string("222"))
        .containsEntry("three", Value.string("333"));
    verify(this.mockAdapter, times(0)).runSelectStatement(any());
    verify(this.mockAdapter, times(0)).runBatchDml(any());
  }

  @Test
  void singleSetOfParametersWithAddTriggersBatchWithOneStatement() {
    String query = "SELECT * FROM tbl WHERE col1=@one AND col2=@two AND col3=@three";
    FakeStatement statement = new FakeStatement(this.mockAdapter, query);

    statement.bind("one", "111");
    statement.bind("two", "222");
    statement.bind("three", "333");
    statement.add();

    StepVerifier.create(Flux.from(statement.execute()).flatMap(r -> r.getRowsUpdated()))
        .expectNext(7, 11, 13)
        .verifyComplete();

    ArgumentCaptor<List<Statement>> params = ArgumentCaptor.forClass(List.class);
    verify(this.mockAdapter).runBatchDml(params.capture());
    assertThat(params.getValue()).hasSize(1);
    assertThat(params.getValue().get(0).getSql()).isEqualTo(query);
    assertThat(params.getValue().get(0).getParameters())
        .hasSize(3)
        .containsEntry("one", Value.string("111"))
        .containsEntry("two", Value.string("222"))
        .containsEntry("three", Value.string("333"));

    verify(this.mockAdapter, times(0)).runSelectStatement(any());
    verify(this.mockAdapter, times(0)).runDmlStatement(any());
  }

  @Test
  void twoParameterSetsWithNoTrailingAddSendsTwoStatements() {
    String query = "SELECT * FROM tbl WHERE col1=@one AND col2=@two AND col3=@three";
    FakeStatement statement = new FakeStatement(this.mockAdapter, query);

    statement.bind("one", "A111");
    statement.bind("two", "A222");
    statement.bind("three", "A333");
    statement.add();
    statement.bind("one", "B111");
    statement.bind("two", "B222");
    statement.bind("three", "B333");

    StepVerifier.create(Flux.from(statement.execute()).flatMap(r -> r.getRowsUpdated()))
        .expectNext(7, 11, 13)
        .verifyComplete();

    ArgumentCaptor<List<Statement>> params = ArgumentCaptor.forClass(List.class);

    verify(this.mockAdapter).runBatchDml(params.capture());
    assertThat(params.getValue()).hasSize(2);
    assertThat(params.getValue().get(0).getParameters()).hasSize(3);
    assertThat(params.getValue().get(0).getParameters())
        .containsEntry("one", Value.string("A111"))
        .containsEntry("two", Value.string("A222"))
        .containsEntry("three", Value.string("A333"));
    assertThat(params.getValue().get(1).getParameters())
        .containsEntry("one", Value.string("B111"))
        .containsEntry("two", Value.string("B222"))
        .containsEntry("three", Value.string("B333"));

    verify(this.mockAdapter, times(0)).runSelectStatement(any());
    verify(this.mockAdapter, times(0)).runDmlStatement(any());
  }

  @Test
  void twoParameterSetsWithTrailingAddSendsTwoStatements() {
    String query = "SELECT * FROM tbl WHERE col1=@one AND col2=@two AND col3=@three";
    FakeStatement statement = new FakeStatement(this.mockAdapter, query);

    statement.bind("one", "A111");
    statement.bind("two", "A222");
    statement.bind("three", "A333");
    statement.add();
    statement.bind("one", "B111");
    statement.bind("two", "B222");
    statement.bind("three", "B333");
    statement.add();

    StepVerifier.create(Flux.from(statement.execute()).flatMap(r -> r.getRowsUpdated()))
        .expectNext(7, 11, 13)
        .verifyComplete();

    ArgumentCaptor<List<Statement>> params = ArgumentCaptor.forClass(List.class);

    verify(this.mockAdapter).runBatchDml(params.capture());
    assertThat(params.getValue()).hasSize(2);
    assertThat(params.getValue().get(0).getParameters()).hasSize(3);
    assertThat(params.getValue().get(0).getParameters())
        .containsEntry("one", Value.string("A111"))
        .containsEntry("two", Value.string("A222"))
        .containsEntry("three", Value.string("A333"));
    assertThat(params.getValue().get(1).getParameters())
        .containsEntry("one", Value.string("B111"))
        .containsEntry("two", Value.string("B222"))
        .containsEntry("three", Value.string("B333"));

    verify(this.mockAdapter, times(0)).runSelectStatement(any());
    verify(this.mockAdapter, times(0)).runDmlStatement(any());
  }

  /* Exercises the mock `DatabaseClientReactiveAdapter`; return values don't matter */
  static class FakeStatement extends AbstractSpannerClientLibraryStatement {

    public FakeStatement(DatabaseClientReactiveAdapter adapter, String query) {
      super(adapter, query);
    }

    @Override
    public Mono<SpannerClientLibraryResult> executeSingle(Statement statement) {
      return this.clientLibraryAdapter.runDmlStatement(statement);
    }

    @Override
    public Flux<SpannerClientLibraryResult> executeMultiple(List<Statement> statements) {
      return this.clientLibraryAdapter.runBatchDml(statements);
    }
  }
}
