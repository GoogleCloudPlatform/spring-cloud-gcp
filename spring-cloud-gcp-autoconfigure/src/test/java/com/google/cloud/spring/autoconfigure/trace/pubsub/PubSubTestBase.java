/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static org.assertj.core.api.Assertions.assertThat;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.handler.MutableSpan;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.StrictCurrentTraceContext;
import brave.propagation.TraceContext;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.After;

public class PubSubTestBase {
  static final String TEST_TOPIC = "myTopic";

  static final String TEST_SUBSCRIPTION = "mySubscription";

  static final ByteString TEST_VALUE = ByteString.copyFromUtf8("bar");

  static final BaggageField BAGGAGE_FIELD = BaggageField.create("some_baggage_field");

  static final String BAGGAGE_FIELD_KEY = "some_baggage_field_key";

  StrictCurrentTraceContext currentTraceContext = StrictCurrentTraceContext.create();

  TestSpanHandler spans = new TestSpanHandler();

  Tracing tracing =
      Tracing.newBuilder()
          .currentTraceContext(currentTraceContext)
          .addSpanHandler(spans)
          .propagationFactory(
              BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                  .add(
                      BaggagePropagationConfig.SingleBaggageField.newBuilder(BAGGAGE_FIELD)
                          .addKeyName(BAGGAGE_FIELD_KEY)
                          .build())
                  .build())
          .build();

  PubSubTracing pubSubTracing = PubSubTracing.create(tracing);

  TraceContext parent = tracing.tracer().newTrace().context();

  TraceContext incoming = tracing.tracer().newTrace().context();

  PubsubMessage.Builder consumerMessage = PubsubMessage.newBuilder().setData(TEST_VALUE);

  PubsubMessage.Builder producerMessage = PubsubMessage.newBuilder().setData(TEST_VALUE);

  RuntimeException error = new RuntimeException("Test exception");

  static void assertChildOf(MutableSpan child, TraceContext parent) {
    assertThat(child.parentId()).isEqualTo(parent.spanIdString());
  }

  static void addB3MultiHeaders(TraceContext parent, PubsubMessage.Builder message) {
    Propagation.B3_STRING
        .injector(PubSubConsumerRequest.SETTER)
        .inject(parent, new PubSubConsumerRequest(message, TEST_SUBSCRIPTION));
  }

  @After
  public void close() {
    tracing.close();
    currentTraceContext.close();
  }
}
