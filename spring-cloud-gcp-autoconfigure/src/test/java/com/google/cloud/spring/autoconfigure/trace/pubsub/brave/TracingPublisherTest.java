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

package com.google.cloud.spring.autoconfigure.trace.pubsub.brave;

import brave.handler.MutableSpan;
import brave.propagation.CurrentTraceContext.Scope;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static brave.Span.Kind.PRODUCER;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TracingPublisherTest extends PubSubTest {

	PublisherInterface mockPublisher = mock(PublisherInterface.class);

	TracingPublisher tracingPublisher =
			pubSubTracing.publisher(mockPublisher, TEST_TOPIC);

	ArgumentCaptor<PubsubMessage> messageCaptor =
			ArgumentCaptor.forClass(PubsubMessage.class);

	@Test
	public void should_add_b3_headers_to_messages() {
		tracingPublisher.publish(producerMessage.build());

		when(mockPublisher.publish(any())).thenReturn(null);

		verify(mockPublisher).publish(messageCaptor.capture());
		assertThat(messageCaptor.getValue().getAttributesOrThrow("b3")).isNotNull();
		assertThat(messageCaptor.getValue().getAttributesCount()).isEqualTo(1);
	}

	@Test
	public void should_add_b3_headers_when_other_headers_exist() {
		PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");

		tracingPublisher.publish(message.build());

		verify(mockPublisher).publish(messageCaptor.capture());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertThat(messageCaptor.getValue().getAttributesMap())
				.containsEntry("tx-id", "1")
				.containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
	}


	@Test
	public void should_inject_child_context() {
		try (Scope scope = currentTraceContext.newScope(parent)) {
			tracingPublisher.publish(producerMessage.build());
		}

		verify(mockPublisher).publish(messageCaptor.capture());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertChildOf(producerSpan, parent);
		assertThat(messageCaptor.getValue().getAttributesMap())
				.containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
	}

	@Test
	public void should_add_parent_trace_when_context_injected_on_headers() {
		PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");

		pubSubTracing.producerInjector.inject(parent, new PubSubProducerRequest(message, "myTopic"));

		tracingPublisher.publish(message.build());

		verify(mockPublisher).publish(messageCaptor.capture());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertChildOf(producerSpan, parent);
		assertThat(messageCaptor.getValue().getAttributesMap())
				.containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
	}

	@Test
	public void should_call_wrapped_producer() {
		PubsubMessage message = producerMessage.build();
		tracingPublisher.publish(message);
		verify(mockPublisher, times(1)).publish(any());
	}

	@Test
	public void send_should_set_name() {
		tracingPublisher.publish(producerMessage.build());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertThat(producerSpan.name()).isEqualTo("publish");
	}

	@Test
	public void send_should_tag_topic() {
		tracingPublisher.publish(producerMessage.build());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertThat(producerSpan.tags())
				.containsOnly(entry("pubsub.topic", TEST_TOPIC));
	}

	@Test
	public void send_shouldnt_tag_null_topic() {
		TracingPublisher tracingPublisher =
				pubSubTracing.publisher(mockPublisher, null);
		tracingPublisher.publish(producerMessage.build());

		MutableSpan producerSpan = spans.get(0);
		assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
		assertThat(producerSpan.tags()).isEmpty();
	}

}