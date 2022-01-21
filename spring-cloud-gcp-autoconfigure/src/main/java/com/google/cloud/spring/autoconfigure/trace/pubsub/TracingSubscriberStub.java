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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSnapshotsPagedResponse;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.Empty;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.CreateSnapshotRequest;
import com.google.pubsub.v1.DeleteSnapshotRequest;
import com.google.pubsub.v1.DeleteSubscriptionRequest;
import com.google.pubsub.v1.GetSnapshotRequest;
import com.google.pubsub.v1.GetSubscriptionRequest;
import com.google.pubsub.v1.ListSnapshotsRequest;
import com.google.pubsub.v1.ListSnapshotsResponse;
import com.google.pubsub.v1.ListSubscriptionsRequest;
import com.google.pubsub.v1.ListSubscriptionsResponse;
import com.google.pubsub.v1.ModifyAckDeadlineRequest;
import com.google.pubsub.v1.ModifyPushConfigRequest;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.SeekRequest;
import com.google.pubsub.v1.SeekResponse;
import com.google.pubsub.v1.Snapshot;
import com.google.pubsub.v1.StreamingPullRequest;
import com.google.pubsub.v1.StreamingPullResponse;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.UpdateSnapshotRequest;
import com.google.pubsub.v1.UpdateSubscriptionRequest;
import java.util.concurrent.TimeUnit;

final class TracingSubscriberStub extends SubscriberStub {
  private final SubscriberStub delegate;

  private final PubSubTracing pubSubTracing;

  TracingSubscriberStub(SubscriberStub subscriberStub, PubSubTracing pubSubTracing) {
    this.delegate = subscriberStub;
    this.pubSubTracing = pubSubTracing;
  }

  /** Trace instrumented pull method. * */
  @Override
  public UnaryCallable<PullRequest, PullResponse> pullCallable() {
    return new TracingPullCallable(delegate.pullCallable(), pubSubTracing);
  }

  // Simply delegated methods below...

  @Override
  public UnaryCallable<Subscription, Subscription> createSubscriptionCallable() {
    return delegate.createSubscriptionCallable();
  }

  @Override
  public UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable() {
    return delegate.getSubscriptionCallable();
  }

  @Override
  public UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable() {
    return delegate.updateSubscriptionCallable();
  }

  @Override
  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsPagedResponse>
      listSubscriptionsPagedCallable() {
    return delegate.listSubscriptionsPagedCallable();
  }

  @Override
  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsCallable() {
    return delegate.listSubscriptionsCallable();
  }

  @Override
  public UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable() {
    return delegate.deleteSubscriptionCallable();
  }

  @Override
  public UnaryCallable<GetSnapshotRequest, Snapshot> getSnapshotCallable() {
    return delegate.getSnapshotCallable();
  }

  @Override
  public UnaryCallable<ModifyAckDeadlineRequest, Empty> modifyAckDeadlineCallable() {
    return delegate.modifyAckDeadlineCallable();
  }

  @Override
  public UnaryCallable<AcknowledgeRequest, Empty> acknowledgeCallable() {
    return delegate.acknowledgeCallable();
  }

  @Override
  public BidiStreamingCallable<StreamingPullRequest, StreamingPullResponse>
      streamingPullCallable() {
    return delegate.streamingPullCallable();
  }

  @Override
  public UnaryCallable<ModifyPushConfigRequest, Empty> modifyPushConfigCallable() {
    return delegate.modifyPushConfigCallable();
  }

  @Override
  public UnaryCallable<ListSnapshotsRequest, ListSnapshotsPagedResponse>
      listSnapshotsPagedCallable() {
    return delegate.listSnapshotsPagedCallable();
  }

  @Override
  public UnaryCallable<ListSnapshotsRequest, ListSnapshotsResponse> listSnapshotsCallable() {
    return delegate.listSnapshotsCallable();
  }

  @Override
  public UnaryCallable<CreateSnapshotRequest, Snapshot> createSnapshotCallable() {
    return delegate.createSnapshotCallable();
  }

  @Override
  public UnaryCallable<UpdateSnapshotRequest, Snapshot> updateSnapshotCallable() {
    return delegate.updateSnapshotCallable();
  }

  @Override
  public UnaryCallable<DeleteSnapshotRequest, Empty> deleteSnapshotCallable() {
    return delegate.deleteSnapshotCallable();
  }

  @Override
  public UnaryCallable<SeekRequest, SeekResponse> seekCallable() {
    return delegate.seekCallable();
  }

  @Override
  public UnaryCallable<SetIamPolicyRequest, Policy> setIamPolicyCallable() {
    return delegate.setIamPolicyCallable();
  }

  @Override
  public UnaryCallable<GetIamPolicyRequest, Policy> getIamPolicyCallable() {
    return delegate.getIamPolicyCallable();
  }

  @Override
  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testIamPermissionsCallable() {
    return delegate.testIamPermissionsCallable();
  }

  @Override
  public void close() {
    delegate.close();
  }

  @Override
  public void shutdown() {
    delegate.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return delegate.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return delegate.isTerminated();
  }

  @Override
  public void shutdownNow() {
    delegate.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
    return delegate.awaitTermination(l, timeUnit);
  }
}
