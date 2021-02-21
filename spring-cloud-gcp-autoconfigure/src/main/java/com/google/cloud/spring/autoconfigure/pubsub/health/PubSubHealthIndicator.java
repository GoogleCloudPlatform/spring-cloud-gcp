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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Default implementation of
 * {@link org.springframework.boot.actuate.health.HealthIndicator} for Pub/Sub. Validates
 * if connection is successful by pulling message asynchronously from the pubSubHealthTemplate.
 * If no subscription is found we know the client is able to connect to GCP Pub/Sub APIs.
 *
 * @author Vinicius Carvalho
 * @author Patrik Hörlin
 *
 * @since 1.2.2
 */
public class PubSubHealthIndicator extends AbstractHealthIndicator {

	private final PubSubHealthTemplate pubSubHealthTemplate;

	public PubSubHealthIndicator(PubSubHealthTemplate pubSubHealthTemplate) {
		super("Failed to connect to Pub/Sub APIs. Check your credentials and verify you have proper access to the service.");
		Assert.notNull(pubSubHealthTemplate, "PubSubHealthTemplate can't be null");
		this.pubSubHealthTemplate = pubSubHealthTemplate;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> future = this.pubSubHealthTemplate.pullAsync();
		try {
			future.get(this.pubSubHealthTemplate.getTimeoutMillis(), TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			builder.withException(e).unknown();
		}
		catch (ExecutionException e) {
			Throwable t = e.getCause();
			if (t instanceof ApiException) {
				ApiException aex = (ApiException) t;
				Code errorCode = aex.getStatusCode().getCode();
				if (errorCode == StatusCode.Code.NOT_FOUND || errorCode == Code.PERMISSION_DENIED) {
					builder.up();
				}
				else {
					builder.down(aex);
				}
			}
			else {
				builder.down(t);
			}
		}
		catch (TimeoutException e) {
			builder.withException(e).unknown();
		}
	}
}
