package com.example;

import com.google.cloud.MonitoredResource;
import com.google.cloud.grpc.GrpcTransportOptions;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.Synchronicity;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ClientLibraryBasedApp {

		/** Expects a new or existing Cloud log name as the first argument. */
		public static void main(String... args) throws Exception {


			LoggingOptions loggingOptions = LoggingOptions.getDefaultInstance()
					.toBuilder()
					.setHost("localhost:8999")
					.build();


			ExecutorService scheduler = Executors.newFixedThreadPool(100);

			try (Logging logging = loggingOptions.getService()) {
				logging.setWriteSynchronicity(Synchronicity.SYNC);

				for (int i = 0; i < 10; i++) {
					Thread.sleep(5000);    // space out logging calls by 5 seconds
					int counter = i;
					scheduler.submit(() -> logging.write(makeLogEntry(counter)));
				}

			}
	}

	private static Collection<LogEntry> makeLogEntry(int counter) {
		String logName = "my-log";
		String textPayload = "Hello, world!";

		System.out.println("About to log: " + textPayload + counter);
		LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(textPayload + counter))
				.setSeverity(Severity.ERROR)
				.setLogName(logName)
				.setResource(MonitoredResource.newBuilder("global").build())
				.build();

		return Collections.singleton(entry);
	}
}
