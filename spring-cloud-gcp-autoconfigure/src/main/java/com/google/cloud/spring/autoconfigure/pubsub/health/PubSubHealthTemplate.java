/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub.health;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;

import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Template for performing health checks against GCP Pub/Sub.
 *
 * @author Patrik Hörlin
 *
 */
class PubSubHealthTemplate {

	/**
	 * Template used when performing health check calls.
	 */
	private PubSubTemplate pubSubTemplate;

	/**
	 * Subscription used when health checking.
	 */
	private String userSubscription;

	/**
	 * Subscription used when health checking if {@link #userSubscription} is {@code null}.
	 */
	private String subscription;

	/**
	 * Timeout when performing health check.
	 */
	private long timeoutMillis;

	PubSubHealthTemplate(PubSubTemplate pubSubTemplate, String userSubscription, long timeoutMillis) {
		this.pubSubTemplate = pubSubTemplate;
		this.userSubscription = userSubscription;
		this.subscription = userSubscription;
		if (!StringUtils.hasText(this.subscription)) {
			this.subscription = UUID.randomUUID().toString();
		}
		this.timeoutMillis = timeoutMillis;
	}

	void pullAndAckAsync() throws InterruptedException, ExecutionException, TimeoutException {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> future = pubSubTemplate.pullAsync(this.subscription, 1, true);
		List<AcknowledgeablePubsubMessage> messages = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
		messages.forEach(AcknowledgeablePubsubMessage::ack);
	}

	boolean isExpectedExecutionException(ExecutionException e) {
		if (!hasUserSubscription() && isExpectedResponseForUnspecifiedSubscription(e)) {
			return true;
		}
		return false;
	}

	private boolean hasUserSubscription() {
		return StringUtils.hasText(userSubscription);
	}

	private boolean isExpectedResponseForUnspecifiedSubscription(ExecutionException e) {
		Throwable t = e.getCause();
		if (t instanceof ApiException) {
			ApiException aex = (ApiException) t;
			Code errorCode = aex.getStatusCode().getCode();
			return errorCode == StatusCode.Code.NOT_FOUND || errorCode == Code.PERMISSION_DENIED;
		}
		return false;
	}
}
