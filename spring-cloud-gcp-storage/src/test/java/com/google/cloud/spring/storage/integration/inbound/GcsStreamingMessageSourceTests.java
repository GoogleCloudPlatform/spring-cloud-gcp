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

package com.google.cloud.spring.storage.integration.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.PageImpl;
import com.google.cloud.ReadChannel;
import com.google.cloud.spring.storage.integration.GcsSessionFactory;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.InputStream;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests for the streaming message source.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class GcsStreamingMessageSourceTests {

  @Autowired
  private PollableChannel unsortedChannel;

  @Autowired
  private PollableChannel sortedChannel;

  private static Blob alphaBlob = mock(Blob.class);

  private static Blob betaBlob = mock(Blob.class);

  private static Blob gammaBlob = mock(Blob.class);

  @Test
  void testInboundStreamingChannelAdapter() throws InterruptedException {
    Message<?> message = this.unsortedChannel.receive(5000);
    assertThat(message).isNotNull();
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("gamma");

    message = this.unsortedChannel.receive(5000);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("beta");
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);

    message = this.unsortedChannel.receive(5000);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("alpha/alpha");
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);

    while (true) {
      Message<?> msg = this.unsortedChannel.receive(5000);
      if (msg == null) {
        break;
      }
      System.out.println(msg);
    }
    Thread.sleep(1000);
    message = this.unsortedChannel.receive(10);
    assertThat(message).isNull();
  }

  @Test
  void testSortedInboundChannelAdapter() throws InterruptedException {
    // This uses the channel adapter with a custom comparator.
    // Files will be processed in ascending order by name: alpha/alpha, beta, gamma
    Message<?> message = this.sortedChannel.receive(5000);
    assertThat(message).isNotNull();
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("alpha/alpha");

    message = this.sortedChannel.receive(5000);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("beta");
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);

    message = this.sortedChannel.receive(5000);
    assertThat(message.getHeaders().get(FileHeaders.REMOTE_FILE)).isEqualTo("gamma");
    assertThat(message.getPayload()).isInstanceOf(InputStream.class);

    Thread.sleep(1000);
    while (true) {
      Message<?> msg = this.sortedChannel.receive(5000);
      if (msg == null) {
        break;
      }
      System.out.println(msg);
    }
    message = this.sortedChannel.receive(10);
    assertThat(message).isNull();
  }

  private static Blob createBlob(Blob blob, String name) {
    when(blob.getBucket()).thenReturn("gcsbucket");
    when(blob.getName()).thenReturn(name);
    when(blob.isDirectory()).thenReturn(false);
    return blob;
  }

  /**
   * Spring config for the tests.
   */
  @Configuration
  @EnableIntegration
  public static class Config {
    private Storage gcsClient() {
      Storage gcs = mock(Storage.class);

      willAnswer(
          invocationOnMock ->
              new PageImpl<>(
                  null,
                  null,
                  Stream.of(
                          createBlob(alphaBlob, "gamma"),
                          createBlob(betaBlob, "beta"),
                          createBlob(gammaBlob, "alpha/alpha"))
                      .collect(Collectors.toList())))
          .given(gcs)
          .list(eq("gcsbucket"));

      willAnswer(invocationOnMock -> mock(ReadChannel.class))
          .given(gcs)
          .reader(eq("gcsbucket"), eq("alpha/alpha"));
      willAnswer(invocationOnMock -> mock(ReadChannel.class))
          .given(gcs)
          .reader(eq("gcsbucket"), eq("beta"));
      willAnswer(invocationOnMock -> mock(ReadChannel.class))
          .given(gcs)
          .reader(eq("gcsbucket"), eq("gamma"));

      return gcs;
    }

    @Bean
    @InboundChannelAdapter(value = "unsortedChannel", poller = @Poller(fixedDelay = "100"))
    public MessageSource<InputStream> unsortedChannelAdapter() {
      GcsStreamingMessageSource adapter =
          new GcsStreamingMessageSource(
              new RemoteFileTemplate<>(new GcsSessionFactory(gcsClient())));
      adapter.setRemoteDirectory("gcsbucket");
      adapter.setFilter(new AcceptOnceFileListFilter<>());

      return adapter;
    }

    @Bean
    @InboundChannelAdapter(value = "sortedChannel", poller = @Poller(fixedDelay = "100"))
    public MessageSource<InputStream> sortedChannelAdapter() {
      GcsStreamingMessageSource adapter =
          new GcsStreamingMessageSource(
              new RemoteFileTemplate<>(new GcsSessionFactory(gcsClient())),
              Comparator.comparing(BlobInfo::getName));

      adapter.setRemoteDirectory("gcsbucket");
      adapter.setFilter(new AcceptOnceFileListFilter<>());

      return adapter;
    }

    @Bean
    public PollableChannel unsortedChannel() {
      return new QueueChannel();
    }

    @Bean
    public PollableChannel sortedChannel() {
      return new QueueChannel();
    }
  }
}
