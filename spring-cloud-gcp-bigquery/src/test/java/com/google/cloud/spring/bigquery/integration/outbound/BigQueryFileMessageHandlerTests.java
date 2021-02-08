package com.google.cloud.spring.bigquery.integration.outbound;

import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.io.InputStream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BigQueryFileMessageHandlerTests {

	private BigQueryTemplate bigQueryTemplate;

	private BigQueryFileMessageHandler messageHandler;

	@Before
	public void setup() {
		bigQueryTemplate = mock(BigQueryTemplate.class);
		SettableListenableFuture<Job> result = new SettableListenableFuture<>();
		result.set(mock(Job.class));
		when(bigQueryTemplate.writeDataToTable(any(), any(), any(), any()))
				.thenReturn(result);

		messageHandler = new BigQueryFileMessageHandler(bigQueryTemplate);
	}

	@Test
	public void testHandleMessage_async() {
		messageHandler.setTableName("testTable");
		messageHandler.setFormatOptions(FormatOptions.csv());
		messageHandler.setSync(false);
		messageHandler.setTableSchema(Schema.of());

		InputStream payload = mock(InputStream.class);
		Message<?> message = MessageBuilder.createMessage(
				payload, new MessageHeaders(Collections.emptyMap()));

		Object result = messageHandler.handleRequestMessage(message);

		verify(bigQueryTemplate).writeDataToTable("testTable", payload, FormatOptions.csv(), Schema.of());
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(ListenableFuture.class);
	}

	@Test
	public void testHandleMessage_sync() {
		messageHandler.setTableName("testTable");
		messageHandler.setFormatOptions(FormatOptions.csv());
		messageHandler.setSync(true);
		messageHandler.setTableSchema(Schema.of());

		InputStream payload = mock(InputStream.class);
		Message<?> message = MessageBuilder.createMessage(
				payload, new MessageHeaders(Collections.emptyMap()));

		Object result = messageHandler.handleRequestMessage(message);

		verify(bigQueryTemplate).writeDataToTable("testTable", payload, FormatOptions.csv(), Schema.of());
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Job.class);
	}
}
