/*
 * Copyright 2021-2021 Google LLC
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.AsyncResultSet;
import com.google.cloud.spanner.AsyncResultSet.CallbackResponse;
import com.google.cloud.spanner.AsyncResultSet.CursorState;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

class ReactiveResultSetCallbackTest {

  AsyncResultSet mockResultSet;

  @BeforeEach
  void setUp() {
    this.mockResultSet = mock(AsyncResultSet.class);
  }

  @Test
  void callbackStopsSinkOnCompletion() {
    when(this.mockResultSet.tryNext()).thenReturn(CursorState.DONE);

    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(sink -> {
          CallbackResponse response =
              new ReactiveResultSetCallback(sink, this.mockResultSet)
                  .cursorReady(this.mockResultSet);
          assertThat(response).isSameAs(CallbackResponse.DONE);
        })
    ).verifyComplete();
  }

  @Test
  void callbackEmitsOnOk() {
    when(this.mockResultSet.tryNext()).thenReturn(CursorState.OK);
    Struct struct = Struct.newBuilder().add(Value.string("some result")).build();
    when(this.mockResultSet.getCurrentRowAsStruct()).thenReturn(struct);


    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(sink -> {
          ReactiveResultSetCallback cb = new ReactiveResultSetCallback(sink, this.mockResultSet);
          CallbackResponse response = cb.cursorReady(this.mockResultSet);
          assertThat(response).isSameAs(CallbackResponse.CONTINUE);
        })
    ).assertNext(r -> assertThat(r.get(0)).isEqualTo("some result")
    ).thenCancel() // without CallbackResponse.DONE signal, sink will not complete by itself.
        .verify();
  }

  @Test
  void callbackWaitsOnNotReady() {
    when(this.mockResultSet.tryNext()).thenReturn(CursorState.NOT_READY);

    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(sink -> {
          CallbackResponse response =
              new ReactiveResultSetCallback(sink, this.mockResultSet)
                  .cursorReady(this.mockResultSet);

          assertThat(response).isSameAs(CallbackResponse.CONTINUE);
        })
    ).thenCancel().verify();
  }

  @Test
  void callbackWaitsOnNullResponse() {
    when(this.mockResultSet.tryNext()).thenReturn(null);

    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(sink -> {
          CallbackResponse response =
              new ReactiveResultSetCallback(sink, this.mockResultSet)
                  .cursorReady(this.mockResultSet);

          assertThat(response).isSameAs(CallbackResponse.CONTINUE);
        })
    ).thenCancel().verify();
  }

  @Test
  void callbackSendsErrorOnException() {
    Exception exception = new RuntimeException("boom");
    when(this.mockResultSet.tryNext()).thenThrow(exception);

    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(sink -> {
          CallbackResponse response =
              new ReactiveResultSetCallback(sink, this.mockResultSet)
                  .cursorReady(this.mockResultSet);
          assertThat(response).isSameAs(CallbackResponse.DONE);
        })
    ).expectErrorMessage("boom")
        .verify();
  }

  @Test
  void callbackUnboundedDemand() {
    setUpResultSet("result1", "result2", "result3", "result4", "result5");

    StepVerifier.create(
        Flux.<SpannerClientLibraryRow>create(
            sink -> {
              ReactiveResultSetCallback cb =
                  new ReactiveResultSetCallback(sink, this.mockResultSet);

              // more callback invocations than results available
              for (int i = 0; i < 7; i++) {
                cb.cursorReady(this.mockResultSet);
              }
            })) // unbounded demand
        .expectNextMatches(r -> r.get(0, String.class).equals("result1"))
        .expectNextMatches(r -> r.get(0, String.class).equals("result2"))
        .expectNextMatches(r -> r.get(0, String.class).equals("result3"))
        .expectNextMatches(r -> r.get(0, String.class).equals("result4"))
        .expectNextMatches(r -> r.get(0, String.class).equals("result5"))
        .verifyComplete();

    verify(this.mockResultSet, times(5)).getCurrentRowAsStruct();
  }

  @Test
  void callbackWithBackpressure() {
    setUpResultSet("result1", "result2", "result3");
    FluxSink<SpannerClientLibraryRow> mockSink = mock(FluxSink.class);
    when(mockSink.requestedFromDownstream()).thenReturn(1L, 0L, 3L, 2L, 1L);

    ReactiveResultSetCallback cb = new ReactiveResultSetCallback(mockSink, this.mockResultSet);
    int numCalls = 0;

    // initial demand = 1
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.CONTINUE);
    verify(mockSink, times(++numCalls)).next(any()); // item emitted


    // demand disappeared: demand = 0
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.PAUSE);
    verify(mockSink, times(numCalls)).next(any()); // no additional items emitted
    verify(this.mockResultSet, times(0)).resume();

    // demand came back: demand = 3
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.CONTINUE);
    verify(mockSink, times(++numCalls)).next(any()); // item emitted
    cb.resumeOnAddedDemand(3);
    verify(this.mockResultSet).resume();

    // demand continues: demand = 2
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.CONTINUE);
    verify(mockSink, times(++numCalls)).next(any()); // item emitted

    // ran out of result set rows; demand = 1
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.DONE);

    verify(this.mockResultSet, times(3)).getCurrentRowAsStruct();
  }

  @Test
  void callbackPausesWhenNoDemand() {
    FluxSink<SpannerClientLibraryRow> mockSink = mock(FluxSink.class);
    when(mockSink.requestedFromDownstream()).thenReturn(0L);

    ReactiveResultSetCallback cb = new ReactiveResultSetCallback(mockSink, this.mockResultSet);
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.PAUSE);
  }


  @Test
  void callbackWhenNoDemandAndCalledMoreThanOnce() {
    FluxSink<SpannerClientLibraryRow> mockSink = mock(FluxSink.class);
    when(mockSink.requestedFromDownstream()).thenReturn(0L);

    ReactiveResultSetCallback cb = new ReactiveResultSetCallback(mockSink, this.mockResultSet);
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.PAUSE);

    // TODO: after  googleapis/java-spanner#1192 is released, update to expect PAUSE
    // when called repeatedly on insufficient demand.
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.DONE);
  }

  @Test
  void callbackEmitsWhenDemandPresent() {
    FluxSink<SpannerClientLibraryRow> mockSink = mock(FluxSink.class);
    when(mockSink.requestedFromDownstream()).thenReturn(1L);
    when(this.mockResultSet.tryNext()).thenReturn(CursorState.OK);
    Struct struct = Struct.newBuilder().add(Value.string("some result")).build();
    when(this.mockResultSet.getCurrentRowAsStruct()).thenReturn(struct);

    ReactiveResultSetCallback cb = new ReactiveResultSetCallback(mockSink, this.mockResultSet);
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.CONTINUE);
    verify(mockSink).next(any());
  }

  @Test
  void callbackEmitsWhenUnboundedDemand() {
    FluxSink<SpannerClientLibraryRow> mockSink = mock(FluxSink.class);
    when(mockSink.requestedFromDownstream()).thenReturn(Long.MAX_VALUE);
    when(this.mockResultSet.tryNext()).thenReturn(CursorState.OK);
    Struct struct = Struct.newBuilder().add(Value.string("some result")).build();
    when(this.mockResultSet.getCurrentRowAsStruct()).thenReturn(struct);

    ReactiveResultSetCallback cb = new ReactiveResultSetCallback(mockSink, this.mockResultSet);
    assertThat(cb.cursorReady(this.mockResultSet)).isEqualTo(CallbackResponse.CONTINUE);
    verify(mockSink).next(any());
  }

  /**
   * Mocks results corresponding to the passed in column values, assuming a single-column table.
   * Emits DONE status after emitting N (number of columns) OK statuses.
   *
   * @param columnValues assumes a single-column table
   */
  private void setUpResultSet(String... columnValues) {
    OngoingStubbing<CursorState> tryNextStub = when(this.mockResultSet.tryNext());

    for (String value : columnValues) {
      tryNextStub = tryNextStub.thenReturn(CursorState.OK);
    }
    tryNextStub.thenReturn(CursorState.DONE);

    OngoingStubbing<Struct> getRowStub = when(this.mockResultSet.getCurrentRowAsStruct());
    for (String value : columnValues) {
      Struct struct = Struct.newBuilder()
          .add(Value.string(value))
          .build();

      getRowStub = getRowStub.thenReturn(struct);
    }
  }
}
