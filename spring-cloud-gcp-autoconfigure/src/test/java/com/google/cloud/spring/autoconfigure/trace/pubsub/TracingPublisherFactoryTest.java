package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static brave.Span.Kind.PRODUCER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import brave.handler.MutableSpan;
import brave.propagation.CurrentTraceContext.Scope;
import com.google.api.core.ApiFunction;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.pubsub.v1.PubsubMessage;
import java.util.function.Consumer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TracingPublisherFactoryTest extends PubSubTestBase {

  PublisherFactory mockDelegateFactory;

  @BeforeEach
  public void setUp() {
    mockDelegateFactory = mock(PublisherFactory.class);
  }

  @Test
  public void should_add_b3_headers_to_messages() {
    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);

    PubsubMessage instrumentedMessage = transform.apply(producerMessage.build());

    assertThat(instrumentedMessage.getAttributesOrThrow("b3")).isNotNull();
    assertThat(instrumentedMessage.getAttributesCount()).isEqualTo(1);
  }

  @Test
  void should_add_b3_headers_when_other_headers_exist() {
    PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");

    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);
    PubsubMessage instrumentedMessage = transform.apply(message.build());

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(instrumentedMessage.getAttributesMap())
        .containsEntry("tx-id", "1")
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void should_inject_child_context() {
    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);

    PubsubMessage instrumentedMessage;
    try (Scope scope = currentTraceContext.newScope(parent)) {
      instrumentedMessage = transform.apply(producerMessage.build());
    }

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertChildOf(producerSpan, parent);
    assertThat(instrumentedMessage.getAttributesMap())
        .isNotNull()
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void should_add_parent_trace_when_context_injected_on_headers() {
    PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");
    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);

    pubSubTracing.producerInjector.inject(parent, new PubSubProducerRequest(message, "myTopic"));
    PubsubMessage instrumentedMessage = transform.apply(message.build());

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertChildOf(producerSpan, parent);
    assertThat(instrumentedMessage.getAttributesMap())
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void send_should_set_name() {
    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);

    PubsubMessage instrumentedMessage = transform.apply(producerMessage.build());

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.name()).isEqualTo("publish");
  }

  @Test
  void send_should_tag_topic() {
    ApiFunction<PubsubMessage, PubsubMessage> transform = getMessageTransformer(TEST_TOPIC);

    PubsubMessage instrumentedMessage = transform.apply(producerMessage.build());

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.tags()).containsOnly(entry("pubsub.topic", TEST_TOPIC));
  }

  @Test
  void send_shouldnt_tag_null_topic() {

    TracingPublisherFactory tracingFactory = new TracingPublisherFactory(pubSubTracing, mockDelegateFactory);
    PubsubMessage instrumentedMessage =
        tracingFactory.instrumentMessage(producerMessage.build(), null);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.tags()).isEmpty();
  }

  /**
   * Returns the message transformer {@code ApiFunction} that would be applied to every published
   * message.
   * Along the way validates that the transformer got created/propagated.
   */
  private ApiFunction<PubsubMessage, PubsubMessage> getMessageTransformer(String topic) {
    TracingPublisherFactory tracingFactory = new TracingPublisherFactory(pubSubTracing, mockDelegateFactory);
    Publisher publisher = tracingFactory.createPublisher(topic);

    ArgumentCaptor<Consumer<Publisher.Builder>> customizerCaptor = ArgumentCaptor.forClass(Consumer.class);
    verify(mockDelegateFactory).createPublisher(eq(topic), customizerCaptor.capture());
    Consumer<Publisher.Builder> customizer = customizerCaptor.getValue();
    assertThat(customizer).isNotNull();

    // Send a fresh builder through the customizer to get the actual Pub/Sub Message transform
    Publisher.Builder builder = Publisher.newBuilder(topic);
    customizer.accept(builder);

    ApiFunction<PubsubMessage, PubsubMessage> transform = null;
    try {
      transform =
          (ApiFunction<PubsubMessage, PubsubMessage>) FieldUtils.readField(builder,
              "messageTransform", true);
    } catch (IllegalAccessException e) {
      fail("Could not read messageTransform field");
    }

    assertThat(transform).isNotNull();
    return transform;
  }
}
