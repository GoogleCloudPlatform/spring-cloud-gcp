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

package com.example;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.client.util.DateTime;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.devtools.cloudtrace.v1.GetTraceRequest;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc.TraceServiceBlockingStub;
import com.google.devtools.cloudtrace.v1.TraceSpan;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Verifies that the logged Traces on the sample application appear in StackDriver.
 */
@EnabledIfSystemProperty(named = "it.trace", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Application.class })
class TraceSampleApplicationIntegrationTests {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@LocalServerPort
	private int port;

	@Autowired
	private GcpProjectIdProvider projectIdProvider;

	@Autowired
	private CredentialsProvider credentialsProvider;

	private String url;

	private TestRestTemplate testRestTemplate;

	private Logging logClient;

	private TraceServiceBlockingStub traceServiceStub;

	private static final String SAMPLE_TOPIC = "traceTopic";

	private static final String SAMPLE_SUBSCRIPTION = "traceSubscription";

	final static String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();

	private static TopicAdminClient topicAdminClient;

	private static SubscriptionAdminClient subscriptionAdminClient;


	@BeforeAll
	static void setup() throws IOException {


		topicAdminClient = TopicAdminClient.create();
		subscriptionAdminClient = SubscriptionAdminClient.create();

		TopicName topic = TopicName.ofProjectTopicName(projectName, SAMPLE_TOPIC);
		Topic response = topicAdminClient.createTopic(topic);
		SubscriptionName subName = SubscriptionName.of(projectName, SAMPLE_SUBSCRIPTION);
		PushConfig pushConfig = PushConfig.newBuilder().build();
		int ackDeadlineSeconds = 599;
		Subscription subResponse =
				subscriptionAdminClient.createSubscription(subName, topic, pushConfig, ackDeadlineSeconds);

	}

	@AfterAll
	static void cleanupPubsubClients() {

		if (subscriptionAdminClient != null) {
			deleteSubscriptions(SAMPLE_SUBSCRIPTION);

			subscriptionAdminClient.close();
		}

		if (topicAdminClient != null) {
			deleteTopics(SAMPLE_TOPIC);
			topicAdminClient.close();
		}

	}

	@BeforeEach
	void setupTraceClient() throws IOException {
		this.url = String.format("http://localhost:%d/", this.port);

		// Create a new RestTemplate here because the auto-wired instance has built-in instrumentation
		// which interferes with us setting the 'x-cloud-trace-context' header.
		this.testRestTemplate = new TestRestTemplate();

		this.logClient = LoggingOptions.newBuilder()
				.setProjectId(this.projectIdProvider.getProjectId())
				.setCredentials(this.credentialsProvider.getCredentials())
				.build()
				.getService();

		ManagedChannel channel = ManagedChannelBuilder
				.forTarget("dns:///cloudtrace.googleapis.com")
				.build();

		this.traceServiceStub = TraceServiceGrpc.newBlockingStub(channel)
				.withCallCredentials(MoreCallCredentials.from(this.credentialsProvider.getCredentials()));
	}

	@Test
	void testTracesAreLoggedCorrectly() {
		DateTime startDateTime = new DateTime(System.currentTimeMillis() - 60000); // Time is hard.

		HttpHeaders headers = new HttpHeaders();

		String uuidString = UUID.randomUUID().toString().replaceAll("-", "");

		headers.add("x-cloud-trace-context", uuidString);
		this.testRestTemplate.exchange(this.url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

		GetTraceRequest getTraceRequest = GetTraceRequest.newBuilder()
				.setProjectId(this.projectIdProvider.getProjectId())
				.setTraceId(uuidString)
				.build();

		String logFilter = String.format(
				"trace=projects/%s/traces/%s AND logName=projects/%s/logs/spring.log AND timestamp>=\"%s\"",
				this.projectIdProvider.getProjectId(), uuidString,
				this.projectIdProvider.getProjectId(), startDateTime.toStringRfc3339());

		await().atMost(4, TimeUnit.MINUTES)
				.pollInterval(Duration.ofSeconds(2))
				.ignoreExceptionsMatching(e -> e.getMessage().contains("Requested entity was not found"))
				.untilAsserted(() -> {

			log.debug("Getting trace...");
			Trace trace = this.traceServiceStub.getTrace(getTraceRequest);
			log.info("Found trace! " + trace.getTraceId()
					+ " with " + trace.getSpansCount() + " spans ("
					+ trace.getSpansList().stream().map(TraceSpan::getName).collect(Collectors.toList())
					+ ").");

			assertThat(trace.getTraceId()).isEqualTo(uuidString);
			// The 16 expected spans are:
			// get /, visit-meet-endpoint, get, get /meet, get, get /meet, get, get /meet,
			// send-message-spring-integration, publish, send-message-pub-sub-template, publish,
			// next-message, on-message, next-message, on-message
			assertThat(trace.getSpansCount()).isGreaterThanOrEqualTo(16);
			log.debug("Trace spans match.");

			// verify custom tags
			assertThat(trace.getSpans(0).getLabelsMap().get("environment")).isEqualTo("QA");
			assertThat(trace.getSpans(0).getLabelsMap().get("session-id")).isNotNull();
			log.debug("Trace labels match.");

			assertThat(trace.getSpansList().stream().map(TraceSpan::getName).distinct())
					.containsExactly(
							"get /",
							"visit-meet-endpoint",
							"get",
							"get /meet",
							"send-message-spring-integration",
							"publish",
							"send-message-pub-sub-template",
							"next-message",
							"on-message");
			log.debug("Trace span names match.");

			List<LogEntry> logEntries = new ArrayList<>();
			log.debug("Finding logs with filter: " + logFilter);
			this.logClient.listLogEntries(Logging.EntryListOption.filter(logFilter)).iterateAll()
					.forEach(le -> {
						logEntries.add(le);
						log.debug("Found log entry: " + le.toString());

						String wantTraceRegex = "projects/" + this.projectIdProvider.getProjectId() + "/traces/([a-z0-9]){32}";
						log.debug("Want trace " + wantTraceRegex + " and got " + le.getTrace());
						assertThat(le.getTrace()).matches(wantTraceRegex);

						String wantSpanRegex = "([a-z0-9]){16}";
						log.debug("Want span " + wantSpanRegex + " and got " + le.getSpanId());
						assertThat(le.getSpanId()).matches(wantSpanRegex);

						log.debug("Log trace and span match.");
					});


			List<String> logContents = logEntries.stream()
					.map(logEntry -> (String) ((JsonPayload) logEntry.getPayload())
							.getDataAsMap().get("message"))
					.collect(Collectors.toList());

			log.info("\n========================= [START OF LOG CONTENTS] =========================\n"
					+ logContents.toString()
					+ "\n========================= [END OF LOG CONTENTS]   =========================\n");
			assertThat(logContents).contains("starting busy work");
			log.debug("Found 'starting' line");
			assertThat(logContents).contains("finished busy work");
			log.debug("Found 'finishing' line");
			assertThat(logContents).contains("Message arrived! Payload: All work is done via PubSubTemplate.");
			log.debug("Found 'PubSubTemplate' line");
			assertThat(logContents).contains("Message arrived! Payload: All work is done via SI.");
			log.debug("Found 'SI' line");
		});
	}

	private static void deleteSubscriptions(String testSubscription) {

		String testSubscriptionName = ProjectSubscriptionName.format(projectName, testSubscription);
		subscriptionAdminClient.deleteSubscription(testSubscriptionName);

		}


	private static void deleteTopics(String testTopic) {

		String testTopicName = ProjectTopicName.format(projectName, testTopic);
		topicAdminClient.deleteTopic(testTopicName);

		}
}
