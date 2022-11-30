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

package com.google.cloud.spring.pubsub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.NoCredentials;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for the Pub/Sub admin operations. */
@ExtendWith(MockitoExtension.class)
class PubSubAdminTests {

  @Mock private TopicAdminClient mockTopicAdminClient;

  @Mock private SubscriptionAdminClient mockSubscriptionAdminClient;

  @Test
  void testNewPubSubAdmin_nullProjectProvider() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                new PubSubAdmin(null, this.mockTopicAdminClient, this.mockSubscriptionAdminClient))
        .withMessage("The project ID provider can't be null.");
  }

  @Test
  void testNewPubSubAdmin_nullTopicAdminClient() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> new PubSubAdmin(() -> "test-project", null, this.mockSubscriptionAdminClient))
        .withMessage("The topic administration client can't be null");
  }

  @Test
  void testNewPubSubAdmin_nullSubscriptionAdminClient() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new PubSubAdmin(() -> "test-project", this.mockTopicAdminClient, null))
        .withMessage("The subscription administration client can't be null");
  }

  @Test
  void testNewPubSubAdmin() throws IOException {
    assertThat(new PubSubAdmin(() -> "test-project", NoCredentials::getInstance)).isNotNull();
  }

  @Test
  void testCreateTopic() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .createTopic("fooTopic");
    verify(this.mockTopicAdminClient).createTopic(TopicName.of("test-project", "fooTopic"));
  }

  @Test
  void testCreateTopic_fullName() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .createTopic("projects/differentProject/topics/fooTopic");
    verify(this.mockTopicAdminClient).createTopic(TopicName.of("differentProject", "fooTopic"));
  }

  @Test
  void testGetTopic() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .getTopic("fooTopic");
    verify(this.mockTopicAdminClient).getTopic(TopicName.of("test-project", "fooTopic"));
  }

  @Test
  void testGetTopic_fullName() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .getTopic("projects/differentProject/topics/fooTopic");
    verify(this.mockTopicAdminClient).getTopic(TopicName.of("differentProject", "fooTopic"));
  }

  @Test
  void testGetTopic_notFound() {
    when(this.mockTopicAdminClient.getTopic(any(TopicName.class)))
        .thenThrow(new ApiException(null, GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND), false));
    assertThat(
            new PubSubAdmin(
                    () -> "test-project",
                    this.mockTopicAdminClient,
                    this.mockSubscriptionAdminClient)
                .getTopic("fooTopic"))
        .isNull();
    verify(this.mockTopicAdminClient).getTopic(TopicName.of("test-project", "fooTopic"));
  }

  @Test
  void testGetTopic_serviceDown() {
    when(this.mockTopicAdminClient.getTopic(any(TopicName.class)))
        .thenThrow(
            new ApiException(null, GrpcStatusCode.of(io.grpc.Status.Code.UNAVAILABLE), false));
    PubSubAdmin psa =
        new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient);

    assertThatExceptionOfType(ApiException.class).isThrownBy(() -> psa.getTopic("fooTopic"));
    verify(this.mockTopicAdminClient).getTopic(TopicName.of("test-project", "fooTopic"));
  }

  @Test
  void testDeleteTopic() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .deleteTopic("fooTopic");
    verify(this.mockTopicAdminClient).deleteTopic(TopicName.of("test-project", "fooTopic"));
  }

  @Test
  void testDeleteTopic_fullName() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .deleteTopic("projects/differentProject/topics/fooTopic");
    verify(this.mockTopicAdminClient).deleteTopic(TopicName.of("differentProject", "fooTopic"));
  }

  @Test
  void testListTopic() throws ExecutionException, InterruptedException {
    when(this.mockTopicAdminClient.listTopics(any(ProjectName.class)))
        .thenReturn(mock(TopicAdminClient.ListTopicsPagedResponse.class));

    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .listTopics();
    verify(this.mockTopicAdminClient).listTopics(ProjectName.of("test-project"));
  }

  @Test
  void testCreateSubscription_nullArgs() {
    PubSubAdmin psa =
        new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> psa.createSubscription(null, "testTopic"));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> psa.createSubscription("testSubscription", null));
  }

  @Test
  void testCreateSubscription() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .createSubscription("testSubscription", "testTopic");

    Subscription expected =
        Subscription.newBuilder()
            .setName("projects/test-project/subscriptions/testSubscription")
            .setTopic("projects/test-project/topics/testTopic")
            .setAckDeadlineSeconds(10)
            .build();
    verify(this.mockSubscriptionAdminClient).createSubscription(expected);
  }

  @Test
  void testCreateSubscription_ackDeadline() {
    PubSubAdmin psa =
        new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> psa.createSubscription("a", "b", PubSubAdmin.MIN_ACK_DEADLINE_SECONDS - 1));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> psa.createSubscription("a", "b", PubSubAdmin.MAX_ACK_DEADLINE_SECONDS + 1));

    psa.createSubscription("testSubscription", "testTopic", 100);

    Subscription expected =
        Subscription.newBuilder()
            .setName("projects/test-project/subscriptions/testSubscription")
            .setTopic("projects/test-project/topics/testTopic")
            .setAckDeadlineSeconds(100)
            .build();
    verify(this.mockSubscriptionAdminClient).createSubscription(expected);
  }

  @Test
  void testCreateSubscription_pushEndpoint() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .createSubscription("testSubscription", "testTopic", "endpoint");

    Subscription expected =
        Subscription.newBuilder()
            .setName("projects/test-project/subscriptions/testSubscription")
            .setTopic("projects/test-project/topics/testTopic")
            .setPushConfig(PushConfig.newBuilder().setPushEndpoint("endpoint").build())
            .setAckDeadlineSeconds(10)
            .build();
    verify(this.mockSubscriptionAdminClient).createSubscription(expected);
  }

  @Test
  void testGetSubscription() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .getSubscription("fooSubscription");
    verify(this.mockSubscriptionAdminClient)
        .getSubscription(
            PubSubSubscriptionUtils.toProjectSubscriptionName("fooSubscription", "test-project").toString());
  }

  @Test
  void testGetSubscription_fullName() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .getSubscription("projects/differentProject/subscriptions/fooSubscription");
    verify(this.mockSubscriptionAdminClient)
        .getSubscription("projects/differentProject/subscriptions/fooSubscription");
  }

  @Test
  void testGetSubscription_notFound() {
    when(this.mockSubscriptionAdminClient.getSubscription(anyString()))
        .thenThrow(new ApiException(null, GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND), false));
    assertThat(
            new PubSubAdmin(
                    () -> "test-project",
                    this.mockTopicAdminClient,
                    this.mockSubscriptionAdminClient)
                .getSubscription("fooSubscription"))
        .isNull();
    verify(this.mockSubscriptionAdminClient)
        .getSubscription(
            PubSubSubscriptionUtils.toProjectSubscriptionName("fooSubscription", "test-project").toString());
  }

  @Test
  void testGetSubscription_serviceDown() {
    when(this.mockSubscriptionAdminClient.getSubscription(anyString()))
        .thenThrow(
            new ApiException(null, GrpcStatusCode.of(io.grpc.Status.Code.UNAVAILABLE), false));
    PubSubAdmin psa =
        new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient);

    assertThatExceptionOfType(ApiException.class)
        .isThrownBy(() -> psa.getSubscription("fooSubscription"));
    verify(this.mockSubscriptionAdminClient)
        .getSubscription(
            PubSubSubscriptionUtils.toProjectSubscriptionName("fooSubscription", "test-project").toString());
  }

  @Test
  void testDeleteSubscription() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .deleteSubscription("fooSubscription");
    verify(this.mockSubscriptionAdminClient)
        .deleteSubscription(
            PubSubSubscriptionUtils.toProjectSubscriptionName("fooSubscription", "test-project").toString());
  }

  @Test
  void testDeleteSubscription_fullName() {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .deleteSubscription("projects/differentProject/subscriptions/fooSubscription");
    verify(this.mockSubscriptionAdminClient)
        .deleteSubscription("projects/differentProject/subscriptions/fooSubscription");
  }

  @Test
  void testListSubscription() {
    when(this.mockSubscriptionAdminClient.listSubscriptions(any(ProjectName.class)))
        .thenReturn(mock(SubscriptionAdminClient.ListSubscriptionsPagedResponse.class));

    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .listSubscriptions();
    verify(this.mockSubscriptionAdminClient).listSubscriptions(ProjectName.of("test-project"));
  }

  @Test
  void testDefaultAckDeadline() throws IOException {
    PubSubAdmin psa = new PubSubAdmin(() -> "test-project", NoCredentials::getInstance);
    int defaultAckDeadline = psa.getDefaultAckDeadline();
    psa.setDefaultAckDeadline(defaultAckDeadline + 1);
    assertThat(psa.getDefaultAckDeadline()).isEqualTo(defaultAckDeadline + 1);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> psa.setDefaultAckDeadline(PubSubAdmin.MIN_ACK_DEADLINE_SECONDS - 1));
  }

  @Test
  void testClose() throws Exception {
    new PubSubAdmin(
            () -> "test-project", this.mockTopicAdminClient, this.mockSubscriptionAdminClient)
        .close();
    verify(this.mockTopicAdminClient).close();
    verify(this.mockSubscriptionAdminClient).close();
  }
}
