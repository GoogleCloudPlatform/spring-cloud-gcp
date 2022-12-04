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

package com.google.cloud.spring.bigquery.integration.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

class BigQueryFileMessageHandlerTests {

  private BigQueryTemplate bigQueryTemplate;

  private BigQueryFileMessageHandler messageHandler;

  private final CompletableFuture<Job> completableFuture = new CompletableFuture<>();

  @BeforeEach
  void setup() {
    bigQueryTemplate = mock(BigQueryTemplate.class);
    completableFuture.complete(mock(Job.class));
    when(bigQueryTemplate.writeDataToTable(any(), any(), any(), any())).thenReturn(
        completableFuture);
    messageHandler = new BigQueryFileMessageHandler(bigQueryTemplate);
    messageHandler.setTableName("testTable");
    messageHandler.setFormatOptions(FormatOptions.csv());
    messageHandler.setSync(false);
    messageHandler.setTableSchema(Schema.of());
  }

  @Test
  void testHandleResourceMessage() throws IOException {
    Resource payload = mock(Resource.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    Object result = messageHandler.handleRequestMessage(message);

    verify(bigQueryTemplate)
        .writeDataToTable("testTable", payload.getInputStream(), FormatOptions.csv(), Schema.of());
    assertThat(result).isNotNull().isInstanceOf(CompletableFuture.class);
  }

  @Test
  void testHandleResourceMessageThrowsException() throws IOException {
    Resource payload = mock(Resource.class);
    when(payload.getInputStream()).thenThrow(IOException.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    assertThatCode(() -> messageHandler.handleRequestMessage(message))
        .isInstanceOf(MessageHandlingException.class)
        .hasStackTraceContaining("Failed to write data to BigQuery tables in message handler");
  }

  @Test
  void testHandleInputStreamMessage_async() {
    InputStream payload = mock(InputStream.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    Object result = messageHandler.handleRequestMessage(message);

    verify(bigQueryTemplate)
        .writeDataToTable("testTable", payload, FormatOptions.csv(), Schema.of());
    assertThat(result).isNotNull().isInstanceOf(CompletableFuture.class);
  }

  @Test
  void testHandleMessage_sync() {
    messageHandler.setSync(true);
    messageHandler.setTableSchemaExpression(new ValueExpression<>(Schema.of()));

    InputStream payload = mock(InputStream.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    Object result = messageHandler.handleRequestMessage(message);

    verify(bigQueryTemplate)
        .writeDataToTable("testTable", payload, FormatOptions.csv(), Schema.of());
    assertThat(result).isNotNull().isInstanceOf(Job.class);
  }

  @Test
  void testHandleMessage_ThrowsExecutionExceptionTest()
      throws ExecutionException, InterruptedException, TimeoutException {
    CompletableFuture<Job> mockCompletableFuture = mock(CompletableFuture.class);
    when(bigQueryTemplate.writeDataToTable(any(), any(), any(), any())).thenReturn(
        mockCompletableFuture);
    when(mockCompletableFuture.get(1L, TimeUnit.SECONDS))
        .thenThrow(ExecutionException.class);
    messageHandler.setSync(true);
    messageHandler.setTableSchemaExpression(new ValueExpression<>(Schema.of()));
    messageHandler.setTimeout(Duration.ofSeconds(1));
    InputStream payload = mock(InputStream.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    assertThatCode(() -> messageHandler.handleRequestMessage(message))
        .isInstanceOf(MessageHandlingException.class)
        .hasStackTraceContaining("Failed to wait for BigQuery Job to complete in message handler");
  }

  @Test
  void testHandleMessage_ThrowsInterruptedExceptionTest()
      throws ExecutionException, InterruptedException, TimeoutException {
    CompletableFuture<Job> mockCompletableFuture = mock(CompletableFuture.class);
    when(bigQueryTemplate.writeDataToTable(any(), any(), any(), any())).thenReturn(
        mockCompletableFuture);
    when(mockCompletableFuture.get(1L, TimeUnit.SECONDS))
        .thenThrow(InterruptedException.class);
    messageHandler.setSync(true);
    messageHandler.setTableSchemaExpression(new ValueExpression<>(Schema.of()));
    messageHandler.setTimeout(Duration.ofSeconds(1));
    InputStream payload = mock(InputStream.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    assertThatCode(() -> messageHandler.handleRequestMessage(message))
        .isInstanceOf(MessageHandlingException.class)
        .hasStackTraceContaining("Failed to wait for BigQuery Job (interrupted) in message handler");
  }

  @Test
  void testHandleMessageWithIllegalArgumentTest() {
    messageHandler.setSync(true);
    messageHandler.setTableSchemaExpression(new ValueExpression<>(Schema.of()));

    BigInteger payload = mock(BigInteger.class);
    Message<?> message =
        MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap()));

    assertThatCode(() -> messageHandler.handleRequestMessage(message))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(String.format(
            "Unsupported message payload type: %s. The supported payload types "
                + "are: java.io.File, byte[], org.springframework.core.io.Resource, "
                + "and java.io.InputStream.",
            payload.getClass().getName()));
  }

  @Test
  void setTableNameExpressionThrowsExceptionTest() {
    assertThatCode(() -> messageHandler.setTableNameExpression(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Table name expression must not be null.");
  }

  @Test
  void setTableNameExpressionTest() {
    assertThatCode(() -> messageHandler.setTableNameExpression(mock(Expression.class)))
        .doesNotThrowAnyException();
  }

  @Test
  void setFormatOptionsExpressionThrowsExceptionTest() {
    assertThatCode(() -> messageHandler.setFormatOptionsExpression(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Format options expression cannot be null.");
  }

  @Test
  void setFormatOptionsExpressionTest() {
    assertThatCode(() -> messageHandler.setFormatOptionsExpression(mock(Expression.class)))
        .doesNotThrowAnyException();
  }

  @Test
  void setTimeOutThrowsExceptionTest() {
    assertThatCode(() -> messageHandler.setTimeout(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Timeout duration must not be null.");
  }

  @Test
  void setTimeOutTest() {
    assertThatCode(() -> messageHandler.setTimeout(Duration.ZERO))
        .doesNotThrowAnyException();
  }
}
