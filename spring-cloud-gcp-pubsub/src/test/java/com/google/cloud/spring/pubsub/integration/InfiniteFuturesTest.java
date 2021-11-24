package com.google.cloud.spring.pubsub.integration;

import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InfiniteFuturesTest {
	static String PROJECT_ID = "elfel-spring";
	static String SUBSCRIPTION_NAME = "exampleSubscription";

	String subscriptionName = ProjectSubscriptionName.format(PROJECT_ID, SUBSCRIPTION_NAME);
	PullRequest pullRequest = PullRequest.newBuilder()
			.setMaxMessages(10)
			.setSubscription(subscriptionName)
			.build();

	SubscriberStubSettings.Builder settingsBuilder = SubscriberStubSettings
			.newBuilder()
			.setEndpoint("this.will.fail:443");

	@Test
	@Timeout(value = 10, unit = TimeUnit.MINUTES)
	void futuresBehaveWellWhenAllowedToFail() throws IOException {

		RetrySettings retryAllowedToFail = RetrySettings.newBuilder()
				.setInitialRpcTimeout(Duration.ofSeconds(5))
				.setMaxRpcTimeout(Duration.ofSeconds(5))
				.setMaxRetryDelay(Duration.ofSeconds(5))
				.setTotalTimeout(Duration.ofSeconds(5))
				.build();
		settingsBuilder.pullSettings().setRetrySettings(retryAllowedToFail);
		SubscriberStubSettings settings = settingsBuilder.build();

		try (SubscriberStub subscriber = GrpcSubscriberStub.create(settings)) {
			PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
			System.out.println("*** This should never happen, but okay: received "
					+ pullResponse.getReceivedMessagesCount() + " messages");
		} catch (Exception e) {
			System.out.println("*** Failed properly");
		}
	}

	@Test
	@Timeout(value = 10, unit = TimeUnit.MINUTES)
	void futuresNeverResolveWhenNotAllowedToFail() throws IOException {

		RetrySettings retryAllowedToFail = RetrySettings.newBuilder()
				.setInitialRpcTimeout(Duration.ofSeconds(3600))
				.setMaxRpcTimeout(Duration.ofSeconds(3600))
				.setMaxRetryDelay(Duration.ofSeconds(3600))
				.setTotalTimeout(Duration.ofSeconds(3600))
				.build();
		settingsBuilder.pullSettings().setRetrySettings(retryAllowedToFail);
		SubscriberStubSettings settings = settingsBuilder.build();

		try (SubscriberStub subscriber = GrpcSubscriberStub.create(settings)) {
			PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
			System.out.println("*** This should never happen, but okay: received "
					+ pullResponse.getReceivedMessagesCount() + " messages");
		} catch (Exception e) {
			System.out.println("*** Failed properly");
		}
	}
}
