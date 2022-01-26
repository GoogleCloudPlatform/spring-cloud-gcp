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

package com.google.cloud.spring.pubsub.reactive;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.DeadlineExceededException;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberOperations;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Status;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.annotation.AsyncResult;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

/**
 * Tests for streams generated by PubSubReactiveFactory.
 *
 * @author Elena Felder
 * @author Maurice Zeijen
 * @since 1.2
 */
@RunWith(MockitoJUnitRunner.class)
public class PubSubReactiveFactoryTests {

  @Mock PubSubSubscriberOperations subscriberOperations;

  PubSubReactiveFactory factory;

  @Before
  public void setUp() {
    factory = new PubSubReactiveFactory(subscriberOperations, VirtualTimeScheduler.getOrSet());
  }

  @Test
  public void testIllegalArgumentExceptionWithMaxMessagesLessThanOne() {
    VirtualTimeScheduler vts = VirtualTimeScheduler.getOrSet();
    assertThatThrownBy(() -> new PubSubReactiveFactory(subscriberOperations, vts, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("maxMessages cannot be less than 1.");
  }

  @Test
  public void testSequentialRequests() throws InterruptedException {
    setUpMessages("msg1", "msg2", "msg3", "msg4");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString), 1)
        .expectSubscription()
        .expectNext("msg1")
        .thenRequest(3)
        .expectNext("msg2", "msg3", "msg4")
        .expectNoEvent(Duration.ofSeconds(10))
        .thenCancel()
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder.verify(this.subscriberOperations).pullAsync("sub1", 1, false);
    methodOrder.verify(this.subscriberOperations).pullAsync("sub1", 3, false);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testSequentialRequestWithInsufficientDemandGetsSplitIntoTwoRequests()
      throws InterruptedException {
    setUpMessages("msg1", "stop", "msg2", "msg3", "msg4");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString), 4)
        .expectSubscription()
        .expectNext("msg1", "msg2", "msg3", "msg4")
        .expectNoEvent(Duration.ofSeconds(10))
        .thenCancel()
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder.verify(this.subscriberOperations).pullAsync("sub1", 4, false);
    methodOrder.verify(this.subscriberOperations).pullAsync("sub1", 3, false);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testDeadlineExceededCausesRetry() throws InterruptedException {
    setUpMessages("timeout", "msg1", "msg2");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString), 2)
        .expectSubscription()
        .expectNext("msg1", "msg2")
        .expectNoEvent(Duration.ofSeconds(10))
        .thenCancel()
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder.verify(this.subscriberOperations, times(2)).pullAsync("sub1", 2, false);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testExceptionThrownByPubSubClientResultingInErrorStream()
      throws InterruptedException {
    setUpMessages("msg1", "msg2", "throw");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString), 2)
        .expectSubscription()
        .expectNext("msg1", "msg2")
        .thenRequest(2)
        .expectError(RuntimeException.class)
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder.verify(this.subscriberOperations, times(2)).pullAsync("sub1", 2, false);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testUnlimitedDemand() throws InterruptedException {
    setUpMessages("msg1", "msg2", "stop", "msg3", "msg4", "stop", "msg5", "stop");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString))
        .expectSubscription()
        .expectNext("msg1", "msg2")
        .expectNoEvent(Duration.ofMillis(10))
        .expectNext("msg3", "msg4")
        .expectNoEvent(Duration.ofMillis(10))
        .expectNext("msg5")
        .thenCancel()
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder
        .verify(this.subscriberOperations, times(3))
        .pullAsync("sub1", Integer.MAX_VALUE, true);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testUnlimitedDemandWithMaxMessages() throws InterruptedException {
    setUpMessages("msg1", "msg2", "stop", "msg3", "stop", "msg4", "msg5", "msg6", "stop");

    PubSubReactiveFactory factory =
        new PubSubReactiveFactory(subscriberOperations, VirtualTimeScheduler.getOrSet(), 2);

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString))
        .expectSubscription()
        .expectNext("msg1", "msg2")
        .expectNoEvent(Duration.ofMillis(20))
        .expectNext("msg3")
        .expectNoEvent(Duration.ofMillis(10))
        .expectNext("msg4", "msg5")
        .expectNoEvent(Duration.ofMillis(10))
        .expectNext("msg6")
        .thenCancel()
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder.verify(this.subscriberOperations, times(5)).pullAsync("sub1", 2, true);
    methodOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testUnlimitedDemandWithException() throws InterruptedException {
    setUpMessages("msg1", "msg2", "stop", "throw");

    StepVerifier.withVirtualTime(() -> factory.poll("sub1", 10).map(this::messageToString))
        .expectSubscription()
        .expectNext("msg1", "msg2")
        .expectNoEvent(Duration.ofMillis(10))
        .expectError(RuntimeException.class)
        .verify();

    InOrder methodOrder = Mockito.inOrder(this.subscriberOperations);
    methodOrder
        .verify(this.subscriberOperations, times(2))
        .pullAsync("sub1", Integer.MAX_VALUE, true);
    methodOrder.verifyNoMoreInteractions();
  }

  private String messageToString(AcknowledgeablePubsubMessage message) {
    return new String(message.getPubsubMessage().getData().toByteArray(), Charset.defaultCharset());
  }

  /**
   * Replays provided messages. If a synthetic message "stop" is encountered, immediately returns
   * previously collected messages. If a synthetic message "timeout" is encountered, throws an
   * {@link DeadlineExceededException}. If a synthetic message "throw" is encountered, throws an
   * {@link RuntimeException}. Fails the calling test if there are not enough messages to fulfill
   * demand from cumulative calls to {@code pull()}.
   *
   * @param messages messages to replay
   */
  private void setUpMessages(String... messages) {
    List<String> msgList = new ArrayList<>(Arrays.asList(messages));

    when(subscriberOperations.pullAsync(eq("sub1"), any(Integer.class), any(Boolean.class)))
        .then(
            invocationOnMock -> {
              List<AcknowledgeablePubsubMessage> result = new ArrayList<>();
              for (int i = 0; i < (Integer) invocationOnMock.getArgument(1); i++) {
                if (msgList.isEmpty()) {
                  fail("Ran out of provided messages.");
                }

                String nextPayload = msgList.remove(0);
                switch (nextPayload) {
                  case "stop":
                    return AsyncResult.forValue(result);
                  case "timeout":
                    if (!result.isEmpty()) {
                      fail("Bad setup -- 'throw' should be the first event in batch");
                    }
                    return AsyncResult.forExecutionException(
                        new DeadlineExceededException(
                            "this is a noop",
                            null,
                            GrpcStatusCode.of(Status.Code.DEADLINE_EXCEEDED),
                            true));
                  case "throw":
                    return AsyncResult.forExecutionException(
                        new RuntimeException("expected exception during pull of messages"));
                }

                AcknowledgeablePubsubMessage msg = mock(AcknowledgeablePubsubMessage.class);
                PubsubMessage pubsubMessage =
                    PubsubMessage.newBuilder()
                        .setData(ByteString.copyFrom((nextPayload).getBytes()))
                        .build();
                when(msg.getPubsubMessage()).thenReturn(pubsubMessage);
                result.add(msg);
              }
              return AsyncResult.forValue(result);
            });
  }
}
