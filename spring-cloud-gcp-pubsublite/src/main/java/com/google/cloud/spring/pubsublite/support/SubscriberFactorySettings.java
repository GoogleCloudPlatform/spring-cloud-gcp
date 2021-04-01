/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.pubsublite.support;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectId;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;

@AutoValue
public abstract class SubscriberFactorySettings {
	abstract GcpProjectIdProvider projectIdProvider();
	abstract CloudZone location();
	abstract FlowControlSettings perPartitionFlowControlSettings();
	abstract CredentialsProvider credentialsProvider();

	public static Builder newBuilder() {
		return new AutoValue_SubscriberFactorySettings.Builder();
	}

	private SubscriptionPath makePath(SubscriptionName name) {
		return SubscriptionPath.newBuilder()
				.setProject(ProjectId.of(projectIdProvider().getProjectId()))
				.setLocation(location())
				.setName(name)
				.build();
	}

	SubscriberInterface instantiate(SubscriptionName name, MessageReceiver receiver) {
		return Subscriber.create(SubscriberSettings.newBuilder()
				.setCredentialsProvider(credentialsProvider())
				.setPerPartitionFlowControlSettings(perPartitionFlowControlSettings())
				.setSubscriptionPath(makePath(name))
				.setReceiver(receiver)
				.build());
	}

	@AutoValue.Builder
	public abstract static class Builder {
		public abstract Builder setProjectIdProvider(GcpProjectIdProvider projectIdProvider);
		public abstract Builder setLocation(CloudZone zone);
		public abstract Builder setPerPartitionFlowControlSettings(FlowControlSettings settings);
		public abstract Builder setCredentialsProvider(CredentialsProvider credentialsProvider);
		public abstract SubscriberFactorySettings build();
	}
}
