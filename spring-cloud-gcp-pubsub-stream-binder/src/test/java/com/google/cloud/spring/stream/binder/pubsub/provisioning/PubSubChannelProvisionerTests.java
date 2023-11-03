/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.stream.binder.pubsub.provisioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;
import com.google.cloud.spring.pubsub.support.PubSubTopicUtils;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubProducerProperties;
import com.google.pubsub.v1.DeadLetterPolicy;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;

/**
 * Tests for Pub/Sub provisioner.
 *
 * @since 1.1
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PubSubChannelProvisionerTests {

  @Mock PubSubAdmin pubSubAdminMock;

  @Mock ExtendedConsumerProperties<PubSubConsumerProperties> extendedConsumerProperties;

  @Mock ExtendedProducerProperties<PubSubProducerProperties> extendedProducerProperties;

  @Mock PubSubConsumerProperties pubSubConsumerProperties;

  @Mock PubSubProducerProperties pubSubProducerProperties;

  // class under test
  PubSubChannelProvisioner pubSubChannelProvisioner;

  @BeforeEach
  void setup() {
    when(this.pubSubAdminMock.getSubscription(any())).thenReturn(null);
    doAnswer(
            invocation -> {
              Subscription.Builder arg = invocation.getArgument(0, Subscription.Builder.class);
              return Subscription.newBuilder()
                  .setName(
                      PubSubSubscriptionUtils.toProjectSubscriptionName(
                              arg.getName(), "test-project")
                          .toString())
                  .setTopic(PubSubTopicUtils.toTopicName(arg.getTopic(), "test-project").toString())
                  .build();
            })
        .when(this.pubSubAdminMock)
        .createSubscription(any());
    doAnswer(
            invocation ->
                Topic.newBuilder()
                    .setName("projects/test-project/topics/" + invocation.getArgument(0))
                    .build())
        .when(this.pubSubAdminMock)
        .getTopic(any());
    when(this.extendedConsumerProperties.getExtension()).thenReturn(this.pubSubConsumerProperties);
    when(this.pubSubConsumerProperties.isAutoCreateResources()).thenReturn(true);

    when(this.extendedProducerProperties.getExtension()).thenReturn(this.pubSubProducerProperties);
    when(this.pubSubProducerProperties.isAutoCreateResources()).thenReturn(true);

    this.pubSubChannelProvisioner = new PubSubChannelProvisioner(this.pubSubAdminMock);
  }

  @Test
  void testProvisionConsumerDestination_specifiedGroup() {
    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", "group_A", this.extendedConsumerProperties);

    assertThat(result.getName()).isEqualTo("topic_A.group_A");

    ArgumentCaptor<Subscription.Builder> argCaptor =
        ArgumentCaptor.forClass(Subscription.Builder.class);
    verify(this.pubSubAdminMock).createSubscription(argCaptor.capture());
    assertThat(argCaptor.getValue().getName()).isEqualTo("topic_A.group_A");
    assertThat(argCaptor.getValue().getTopic()).isEqualTo("topic_A");
  }

  @Test
  void testProvisionConsumerDestination_specifiedGroupTopicInDifferentProject() {
    String fullTopicName = "projects/differentProject/topics/topic_A";
    when(this.pubSubAdminMock.getTopic(fullTopicName))
        .thenReturn(Topic.newBuilder().setName(fullTopicName).build());

    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                fullTopicName, "group_A", this.extendedConsumerProperties);

    assertThat(result.getName()).isEqualTo("topic_A.group_A");

    ArgumentCaptor<Subscription.Builder> argCaptor =
        ArgumentCaptor.forClass(Subscription.Builder.class);
    verify(this.pubSubAdminMock).createSubscription(argCaptor.capture());
    assertThat(argCaptor.getValue().getName()).isEqualTo("topic_A.group_A");
    assertThat(argCaptor.getValue().getTopic())
        .isEqualTo("projects/differentProject/topics/topic_A");
  }

  @Test
  void testProvisionConsumerDestination_customSubscription() {
    when(this.extendedConsumerProperties.getExtension()).thenReturn(this.pubSubConsumerProperties);
    when(this.pubSubConsumerProperties.getSubscriptionName()).thenReturn("my-custom-subscription");

    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", "group_A", this.extendedConsumerProperties);

    assertThat(result.getName()).isEqualTo("my-custom-subscription");
  }

  @Test
  void testProvisionConsumerDestination_anonymousGroup() {
    when(this.pubSubConsumerProperties.isAutoCreateResources()).thenReturn(true);

    String subscriptionNameRegex = "anonymous\\.topic_A\\.[a-f0-9\\-]{36}";

    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", null, this.extendedConsumerProperties);

    assertThat(result.getName()).matches(subscriptionNameRegex);

    ArgumentCaptor<Subscription.Builder> argCaptor =
        ArgumentCaptor.forClass(Subscription.Builder.class);
    verify(this.pubSubAdminMock).createSubscription(argCaptor.capture());
    assertThat(argCaptor.getValue().getName()).matches(subscriptionNameRegex);
    assertThat(argCaptor.getValue().getTopic()).isEqualTo("topic_A");
  }

  @Test
  void testProvisionConsumerDestination_deadLetterQueue() {
    PubSubConsumerProperties.DeadLetterPolicy dlp = new PubSubConsumerProperties.DeadLetterPolicy();
    dlp.setDeadLetterTopic("deadLetterTopic");
    dlp.setMaxDeliveryAttempts(12);
    when(this.pubSubConsumerProperties.getDeadLetterPolicy()).thenReturn(dlp);

    when(this.pubSubAdminMock.getTopic("deadLetterTopic")).thenReturn(null);
    when(this.pubSubAdminMock.createTopic("deadLetterTopic"))
        .thenReturn(
            Topic.newBuilder().setName("projects/test-project/topics/deadLetterTopic").build());

    this.pubSubChannelProvisioner.provisionConsumerDestination(
        "topic_A", "group_A", this.extendedConsumerProperties);

    ArgumentCaptor<Subscription.Builder> argCaptor =
        ArgumentCaptor.forClass(Subscription.Builder.class);
    verify(this.pubSubAdminMock).createSubscription(argCaptor.capture());
    Subscription.Builder sb = argCaptor.getValue();
    assertThat(sb.getName()).isEqualTo("topic_A.group_A");
    assertThat(sb.getTopic()).isEqualTo("topic_A");
    assertThat(sb.getDeadLetterPolicy()).isNotNull();
    DeadLetterPolicy policy = sb.getDeadLetterPolicy();
    assertThat(policy.getDeadLetterTopic())
        .isEqualTo("projects/test-project/topics/deadLetterTopic");
    assertThat(policy.getMaxDeliveryAttempts()).isEqualTo(12);
  }

  @Test
  void testAfterUnbindConsumer_anonymousGroup() {
    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", null, this.extendedConsumerProperties);

    this.pubSubChannelProvisioner.afterUnbindConsumer(result);

    verify(this.pubSubAdminMock).deleteSubscription(result.getName());
  }

  @Test
  void testAfterUnbindConsumer_twice() {
    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", null, this.extendedConsumerProperties);

    this.pubSubChannelProvisioner.afterUnbindConsumer(result);
    this.pubSubChannelProvisioner.afterUnbindConsumer(result);

    verify(this.pubSubAdminMock, times(1)).deleteSubscription(result.getName());
  }

  @Test
  void testAfterUnbindConsumer_nonAnonymous() {
    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "topic_A", "group1", this.extendedConsumerProperties);

    this.pubSubChannelProvisioner.afterUnbindConsumer(result);

    verify(this.pubSubAdminMock, never()).deleteSubscription(result.getName());
  }

  @Test
  void testProvisionConsumerDestination_concurrentTopicCreation() {
    when(this.pubSubAdminMock.createTopic(any())).thenThrow(AlreadyExistsException.class);
    when(this.pubSubAdminMock.getTopic("already_existing_topic"))
            .thenReturn(null)
            .thenReturn(Topic.newBuilder().setName("already_existing_topic").build());

    // Ensure no exceptions occur if topic already exists on create call
    assertThat(
            this.pubSubChannelProvisioner.ensureTopicExists(
                    "already_existing_topic", true))
            .isNotNull();
  }

  @Test
  void testProvisionConsumerDestination_recursiveExistCalls() {
    when(this.pubSubAdminMock.getTopic("new_topic")).thenReturn(null);
    when(this.pubSubAdminMock.createTopic(any())).thenThrow(AlreadyExistsException.class);

    // Ensure no infinite loop on recursive call
    assertThatExceptionOfType(ProvisioningException.class)
        .isThrownBy(() -> this.pubSubChannelProvisioner.ensureTopicExists("new_topic", true));
  }

  @Test
  void testProvisionConsumerDestination_subscriptionNameCannotBeNull() {
    when(this.pubSubConsumerProperties.isAutoCreateResources()).thenReturn(false);
    when(this.pubSubConsumerProperties.getSubscriptionName()).thenReturn(null);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                this.pubSubChannelProvisioner.provisionConsumerDestination(
                    "topic_A", null, this.extendedConsumerProperties))
        .withMessage("Subscription Name cannot be null or empty");
  }

  @Test
  void testProvisionConsumerDestination_createSubscription() {
    when(this.pubSubAdminMock.getSubscription("subscription_A"))
        .thenReturn(
            Subscription.newBuilder().setTopic("topic_A").setName("subscription_A").build());

    Subscription subscription =
        this.pubSubChannelProvisioner.ensureSubscriptionExists(
            "subscription_A", "topic_A", null, true);

    assertThat(subscription.getName()).isEqualTo("subscription_A");
    assertThat(subscription.getTopic()).isEqualTo("topic_A");
  }


  @Test
  void testProvisionConsumerDestination_createTopic_whenAutoCreateResources_isTrue() {
    doReturn(null).when(this.pubSubAdminMock).getTopic("not_yet_created");

    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "not_yet_created", "group_A", this.extendedConsumerProperties);

    verify(pubSubAdminMock).getTopic("not_yet_created");
    verify(pubSubAdminMock).createTopic("not_yet_created");
  }

  @Test
  void testProvisionConsumerDestination_dontCreateTopic_whenAutoCreateResources_isFalse() {
    when(this.pubSubConsumerProperties.isAutoCreateResources()).thenReturn(false);

    PubSubConsumerDestination result =
        (PubSubConsumerDestination)
            this.pubSubChannelProvisioner.provisionConsumerDestination(
                "not_yet_created", "group_A", this.extendedConsumerProperties);

    verify(pubSubAdminMock, never()).getTopic("not_yet_created");
    verify(pubSubAdminMock, never()).createTopic("not_yet_created");
  }

  @Test
  void testProvisionProducerDestination_createTopic() {
    ProducerDestination destination = this.pubSubChannelProvisioner.provisionProducerDestination(
        "topic_A", extendedProducerProperties);

    assertThat(destination.getName()).isEqualTo("topic_A");
  }

  @Test
  void testProvisionProducerDestination_dontCreateTopic() {
    when(this.pubSubProducerProperties.isAutoCreateResources()).thenReturn(false);
    when(this.pubSubAdminMock.getTopic(any())).thenReturn(null);

    assertThatExceptionOfType(ProvisioningException.class)
        .isThrownBy(() -> this.pubSubChannelProvisioner.provisionProducerDestination(
            "not_yet_created", extendedProducerProperties))
        .withMessageContaining("Non-existing");
  }
}
