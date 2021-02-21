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

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;

import org.springframework.util.concurrent.ListenableFuture;

public class PubSubHealthTemplate {

	private PubSubTemplate pubSubTemplate;

	private String subscription;

	private long timeoutMillis;

	public PubSubHealthTemplate(PubSubTemplate pubSubTemplate, String subscription,
			long timeoutMillis) {
		super();
		this.pubSubTemplate = pubSubTemplate;
		this.subscription = subscription;
		this.timeoutMillis = timeoutMillis;
	}

	public PubSubTemplate getPubSubTemplate() {
		return pubSubTemplate;
	}

	public String getSubscription() {
		return subscription;
	}

	public long getTimeoutMillis() {
		return timeoutMillis;
	}

	public ListenableFuture<List<AcknowledgeablePubsubMessage>> pullAsync() {
		return this.pubSubTemplate.pullAsync(this.subscription, 1, true);
	}
}
