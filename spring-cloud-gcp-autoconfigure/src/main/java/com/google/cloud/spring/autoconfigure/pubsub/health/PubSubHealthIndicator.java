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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.rpc.StatusCode;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;

/**
 * Default implementation of
 * {@link org.springframework.boot.actuate.health.HealthIndicator} for Pub/Sub. Validates
 * if connection is successful by pulling message asynchronously from the pubSubHealthTemplate.
 *
 * If a custom subscription has been specified, this health indicator will only signal up
 * if messages are successfully pulled and acknowledged.
 *
 * If no subscription has been specified, this health indicator will pull messages from a random subscription
 * that is expected not to exist. It will signal up if it is able to connect to GCP Pub/Sub APIs,
 * i.e. the pull results in a response of {@link StatusCode.Code#NOT_FOUND} or
 * {@link StatusCode.Code#PERMISSION_DENIED}.
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
		Assert.notNull(pubSubHealthTemplate, "pubSubHealthTemplate can't be null");
		this.pubSubHealthTemplate = pubSubHealthTemplate;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) {
		try {
			pubSubHealthTemplate.probeHealth();
			builder.up();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			builder.withException(e).unknown();
		}
		catch (ExecutionException e) {
			if (pubSubHealthTemplate.isHealthyException(e)) {
				builder.up();
			}
			else {
				builder.down(e);
			}
		}
		catch (TimeoutException e) {
			builder.withException(e).unknown();
		}
		catch (Exception e) {
			builder.down(e);
		}
	}
}
