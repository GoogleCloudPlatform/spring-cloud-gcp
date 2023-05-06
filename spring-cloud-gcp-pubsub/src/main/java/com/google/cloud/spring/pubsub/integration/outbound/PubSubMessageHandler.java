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

package com.google.cloud.spring.pubsub.integration.outbound;

import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherOperations;
import com.google.cloud.spring.pubsub.integration.PubSubHeaderMapper;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * Outbound channel adapter to publish messages to Google Cloud Pub/Sub.
 *
 * <p>It delegates Google Cloud Pub/Sub interaction to {@link
 * com.google.cloud.spring.pubsub.core.PubSubTemplate}.
 */
public class PubSubMessageHandler extends AbstractMessageHandler {

  private static final long DEFAULT_PUBLISH_TIMEOUT = 10000;

  private final PubSubPublisherOperations pubSubPublisherOperations;

  private Expression topicExpression;

  private boolean sync;

  private EvaluationContext evaluationContext;

  private Expression publishTimeoutExpression = new ValueExpression<>(DEFAULT_PUBLISH_TIMEOUT);

  private BiConsumer<String, Throwable> publishCallback;

  private SuccessCallback successCallback;

  private FailureCallback failureCallback;

  private HeaderMapper<Map<String, String>> headerMapper = new PubSubHeaderMapper();

  /**
   * Instantiates an outbound adapter for publishing messages to a topic.
   *
   * @param pubSubPublisherOperations {@link PubSubPublisherOperations} to use
   * @param topic short or fully qualified destination topic name
   */
  public PubSubMessageHandler(PubSubPublisherOperations pubSubPublisherOperations, String topic) {
    Assert.notNull(pubSubPublisherOperations, "Pub/Sub publisher template can't be null.");
    Assert.hasText(topic, "Pub/Sub topic can't be null or empty.");
    this.pubSubPublisherOperations = pubSubPublisherOperations;
    this.topicExpression = new LiteralExpression(topic);
  }

  public boolean isSync() {
    return this.sync;
  }

  /**
   * Set publish method to be synchronous or asynchronous.
   *
   * <p>Publish is asynchronous be default.
   *
   * @param sync true for synchronous, false for asynchronous
   */
  public void setSync(boolean sync) {
    this.sync = sync;
  }

  public Expression getPublishTimeoutExpression() {
    return this.publishTimeoutExpression;
  }

  /**
   * Set the SpEL expression to evaluate a timeout in milliseconds for a synchronous publish call to
   * Google Cloud Pub/Sub.
   *
   * @param publishTimeoutExpression the {@link Expression} for the publish timeout in milliseconds
   */
  public void setPublishTimeoutExpression(Expression publishTimeoutExpression) {
    Assert.notNull(publishTimeoutExpression, "Publish timeout expression can't be null.");
    this.publishTimeoutExpression = publishTimeoutExpression;
  }

  /**
   * Set the SpEL expression to evaluate a timeout in milliseconds for a synchronous publish call to
   * Google Cloud Pub/Sub from a string.
   *
   * @param publishTimeoutExpression a string with an expression for the publish timeout in
   *     milliseconds
   */
  public void setPublishTimeoutExpressionString(String publishTimeoutExpression) {
    Assert.notNull(publishTimeoutExpression, "Publish timeout expression can't be null.");
    setPublishTimeoutExpression(EXPRESSION_PARSER.parseExpression(publishTimeoutExpression));
  }

  /**
   * Set the timeout in milliseconds for a synchronous publish call to Google Cloud Pub/Sub.
   *
   * @param timeoutMillis timeout in milliseconds
   */
  public void setPublishTimeout(long timeoutMillis) {
    setPublishTimeoutExpression(new ValueExpression<>(timeoutMillis));
  }

  protected BiConsumer<String, Throwable> getPublishCallback() {
    return this.publishCallback;
  }

  /**
   * Set callback (can be a lambda) for processing the published message ID and the original {@code
   * Message} after the message was successfully published.
   *
   * @param successCallback callback accepting a {@code String} message ID and the original {@code
   *     Message}.
   */
  public void setSuccessCallback(SuccessCallback successCallback) {
    this.successCallback = successCallback;
  }

  /**
   * Set callback (can be a lambda) for processing the root cause exception and the original {@code
   * Message} in case of failure.
   *
   * @param failureCallback callback accepting a {@code Throwable} and a {@code Message}.
   */
  public void setFailureCallback(FailureCallback failureCallback) {
    this.failureCallback = failureCallback;
  }

  public Expression getTopicExpression() {
    return this.topicExpression;
  }

  /**
   * Set the SpEL expression for the topic this adapter sends messages to.
   *
   * @param topicExpression the SpEL expression representing the topic name
   */
  public void setTopicExpression(Expression topicExpression) {
    this.topicExpression = topicExpression;
  }

  /**
   * Set the topic where this adapter sends messages to.
   *
   * @param topic topic name
   */
  public void setTopic(String topic) {
    Assert.hasText(topic, "The topic can't be null or empty");
    this.topicExpression = new LiteralExpression(topic);
  }

  /**
   * Set the topic expression string that is evaluated into an actual expression.
   *
   * @param topicExpressionString topic expression string
   */
  public void setTopicExpressionString(String topicExpressionString) {
    this.topicExpression = EXPRESSION_PARSER.parseExpression(topicExpressionString);
  }

  /**
   * Set the header mapper to map headers from {@link Message} into outbound {@link
   * com.google.pubsub.v1.PubsubMessage}.
   *
   * @param headerMapper the header mapper
   */
  public void setHeaderMapper(HeaderMapper<Map<String, String>> headerMapper) {
    Assert.notNull(headerMapper, "The header mapper can't be null.");
    this.headerMapper = headerMapper;
  }

  @Override
  protected void handleMessageInternal(Message<?> message) {
    Object payload = message.getPayload();
    String topic = calculateTopic(message);

    Map<String, String> headers = new HashMap<>();
    this.headerMapper.fromHeaders(message.getHeaders(), headers);

    CompletableFuture<String> pubsubFuture =
        this.pubSubPublisherOperations.publish(topic, payload, headers);

    if (this.publishCallback != null) {
      pubsubFuture.whenComplete(this.publishCallback);
    }

    if (this.successCallback != null || this.failureCallback != null) {
      pubsubFuture.whenComplete(new PubSubPublishCallback(message));
    }

    if (this.sync) {
      Long timeout =
          this.publishTimeoutExpression.getValue(this.evaluationContext, message, Long.class);
      blockOnPublishFuture(pubsubFuture, message, timeout);
    }
  }

  /**
   * Returns Pub/Sub destination topic.
   *
   * <p>Order of precedence is:
   *
   * <ul>
   *   <li>Message header {@code GcpPubSubHeaders.TOPIC}
   *   <li>Handler-global topic name or evaluated expression.
   * </ul>
   *
   * @param message message to extract headers from
   * @return Pub/Sub topic destination for given message
   */
  private String calculateTopic(Message<?> message) {
    if (message.getHeaders().containsKey(GcpPubSubHeaders.TOPIC)) {
      return message.getHeaders().get(GcpPubSubHeaders.TOPIC, String.class);
    }
    return this.topicExpression.getValue(this.evaluationContext, message, String.class);
  }

  private void blockOnPublishFuture(
      CompletableFuture<String> pubsubFuture, Message<?> message, Long timeout) {
    try {
      if (timeout == null || timeout < 0) {
        pubsubFuture.get();
      } else {
        pubsubFuture.get(timeout, TimeUnit.MILLISECONDS);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new MessageHandlingException(message, ie);
    } catch (ExecutionException ee) {
      throw new MessageHandlingException(message, ee.getCause());
    } catch (TimeoutException te) {
      throw new MessageTimeoutException(
          message, "Timeout waiting for response from Pub/Sub publisher", te);
    }
  }

  @Override
  protected void onInit() {
    super.onInit();
    this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(getBeanFactory());
  }

  /** Implement this callback to post-process a successfully published message. */
  @FunctionalInterface
  public interface SuccessCallback {

    void onSuccess(String ackId, Message<?> message);
  }

  /** Implement this callback to post-process a message that failed to publish to Cloud Pub/Sub. */
  @FunctionalInterface
  public interface FailureCallback {

    void onFailure(Throwable cause, Message<?> message);
  }

  /**
   * Publish callback that invokes the parent {@code PubSubMessageHandler}'s success or failure
   * callback, if available.
   */
  private class PubSubPublishCallback implements BiConsumer<String, Throwable> {
    private final Message<?> message;

    PubSubPublishCallback(Message<?> message) {
      this.message = message;
    }

    private void handleSuccess(String messageId) {
      if (PubSubMessageHandler.this.successCallback != null) {
        PubSubMessageHandler.this.successCallback.onSuccess(messageId, message);
      }
    }

    private void handleFailure(Throwable throwable) {
      if (PubSubMessageHandler.this.failureCallback != null) {
        PubSubMessageHandler.this.failureCallback.onFailure(throwable, message);
      }
    }

    @Override
    public void accept(String messageId, Throwable throwable) {
      if (throwable == null) {
        handleSuccess(messageId);
      } else {
        handleFailure(throwable);
      }
    }
  }
}
