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

import java.util.List;

import brave.handler.MutableSpan;
import com.google.api.core.AbstractApiFuture;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import org.junit.Test;

import static brave.Span.Kind.CONSUMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TracingSubscriberStubTest extends PubSubTest {
	SubscriberStub mockSubscriberStub = mock(SubscriberStub.class);

	TracingSubscriberStub tracingSubscriberStub =
			pubSubTracing.subscriberStub(mockSubscriberStub);

	@Test
	public void should_call_wrapped_pull_and_close_spans() {
		PubsubMessage pulledMessage = pullMessage(consumerMessage.build());

		// message received
		assertThat(pulledMessage).isNotNull();

		MutableSpan consumerSpan = spans.get(0);
		assertThat(consumerSpan.kind()).isEqualTo(CONSUMER);
		assertThat(consumerSpan.name()).isEqualTo("pull");
		assertThat(consumerSpan.tags())
				.containsOnly(entry("pubsub.subscription", TEST_SUBSCRIPTION));
	}

	@Test
	public void should_add_new_trace_headers_if_b3_missing() {
		PubsubMessage pulledMessage = pullMessage(consumerMessage.build());

		assertThat(pulledMessage.getAttributesOrThrow("b3")).isNotNull();
		assertThat(pulledMessage.getAttributesCount()).isEqualTo(1);

		MutableSpan consumerSpan = spans.get(0);
		assertThat(consumerSpan.kind()).isEqualTo(CONSUMER);
		assertThat(consumerSpan.parentId()).isNull();
	}

	@Test
	public void should_create_child_of_trace_headers() {
		addB3MultiHeaders(parent, consumerMessage);

		PubsubMessage pulledMessage = pullMessage(consumerMessage.build());

		assertThat(pulledMessage.getAttributesOrThrow("b3")).startsWith(parent.traceIdString());
		assertThat(pulledMessage.getAttributesCount()).isEqualTo(1);

		assertChildOf(spans.get(0), parent);
	}


	@Test
	public void should_create_only_one_consumer_span_per_subscription_whenSharingEnabled() {
		pullMessages(consumerMessage.build(), consumerMessage.build());

		// only one consumer span reported
		assertThat(spans).hasSize(1);
		MutableSpan consumerSpan = spans.get(0);
		assertThat(consumerSpan.kind()).isEqualTo(CONSUMER);
		assertThat(consumerSpan.name()).isEqualTo("pull");
		assertThat(consumerSpan.tags())
				.containsOnly(entry("pubsub.subscription", TEST_SUBSCRIPTION));
	}


	@Test
	public void should_create_individual_span_per_subscription_whenSharingDisabled() {
		pubSubTracing = PubSubTracing.newBuilder(tracing).singleRootSpanOnReceiveBatch(false).build();
		tracingSubscriberStub =
				pubSubTracing.subscriberStub(mockSubscriberStub);

		pullMessages(consumerMessage.build(), consumerMessage.build());

		// only one consumer span reported
		assertThat(spans).hasSize(2);
		for (int i = 0; i < 2; i++) {
			MutableSpan consumerSpan = spans.get(i);
			assertThat(consumerSpan.kind()).isEqualTo(CONSUMER);
			assertThat(consumerSpan.name()).isEqualTo("pull");
			assertThat(consumerSpan.tags())
					.containsOnly(entry("pubsub.subscription", TEST_SUBSCRIPTION));
		}
	}

	private PubsubMessage pullMessage(PubsubMessage sourceMessage) {
		return pullMessages(sourceMessage).get(0).getMessage();
	}

		private List<ReceivedMessage> pullMessages(PubsubMessage... sourceMessages) {
			when(mockSubscriberStub.pullCallable()).thenReturn(new UnaryCallable<PullRequest, PullResponse>() {
				@Override
				public ApiFuture<PullResponse> futureCall(PullRequest pullRequest, ApiCallContext apiCallContext) {
					return new AbstractApiFuture<PullResponse>() {
						@Override
						public PullResponse get() {
							PullResponse.Builder pullResponse = PullResponse.newBuilder();
							for (PubsubMessage sourceMessage : sourceMessages) {
								pullResponse.addReceivedMessages(ReceivedMessage.newBuilder().setMessage(sourceMessage).build());
							}
							return pullResponse.build();
						}
					};
				}
			});
			return tracingSubscriberStub.pullCallable()
					.call(PullRequest.newBuilder().setSubscription(TEST_SUBSCRIPTION).build())
					.getReceivedMessagesList();
	}
}
