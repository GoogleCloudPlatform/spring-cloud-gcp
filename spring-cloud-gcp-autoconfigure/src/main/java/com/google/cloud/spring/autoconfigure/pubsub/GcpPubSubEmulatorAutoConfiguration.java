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

package com.google.cloud.spring.autoconfigure.pubsub;

import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javax.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * If <code>spring.cloud.gcp.pubsub.emulator-host</code> is set, spring stream will connect to a
 * running pub/sub emulator.
 */
@AutoConfiguration
@ConditionalOnClass({ManagedChannel.class, PubSubTemplate.class})
@ConditionalOnProperty(prefix = "spring.cloud.gcp.pubsub", name = "enabled", matchIfMissing = true)
@AutoConfigureBefore(GcpPubSubAutoConfiguration.class)
@EnableConfigurationProperties(GcpPubSubProperties.class)
public class GcpPubSubEmulatorAutoConfiguration {
  private ManagedChannel channel;

  @Bean(name = {"subscriberTransportChannelProvider", "publisherTransportChannelProvider"})
  @ConditionalOnMissingBean(
      name = {"subscriberTransportChannelProvider", "publisherTransportChannelProvider"})
  @ConditionalOnProperty(prefix = "spring.cloud.gcp.pubsub", name = "emulator-host")
  public TransportChannelProvider transportChannelProvider(
      GcpPubSubProperties gcpPubSubProperties) {
    this.channel =
        ManagedChannelBuilder.forTarget("dns:///" + gcpPubSubProperties.getEmulatorHost())
            .usePlaintext()
            .build();
    return FixedTransportChannelProvider.create(GrpcTransportChannel.create(this.channel));
  }

  @PreDestroy
  public void closeManagedChannel() {
    if (this.channel != null) {
      this.channel.shutdown();
    }
  }
}
