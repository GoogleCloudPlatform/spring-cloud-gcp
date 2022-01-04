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

package com.google.cloud.spring.logging;

import com.google.api.gax.core.FixedExecutorProvider;
import com.google.auth.Credentials;
import com.google.cloud.grpc.GrpcTransportOptions;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A Google Cloud Java Logback {@link com.google.cloud.logging.logback.LoggingAppender}
 * wrapper that configures it for Spring Cloud GCP.
 *
 * @author Mike Eltsufin
 * @since 1.2
 */
public class LoggingAppender extends com.google.cloud.logging.logback.LoggingAppender {
	private LoggingOptions loggingOptions;

	private Integer maxThreads;

	private ScheduledExecutorService executorService;

	/**
	 * Wraps {@link com.google.cloud.logging.logback.LoggingAppender#getLoggingOptions()} to
	 * add {@link UserAgentHeaderProvider} configuration, so that usage can be properly
	 * attributed to Spring Cloud GCP.
	 */
	@Override
	protected LoggingOptions getLoggingOptions() {

		if (loggingOptions == null) {
			LoggingOptions.Builder loggingOptionsBuilder = LoggingOptions.newBuilder();

			// only credentials are set in the options of the parent class
			Credentials credentials = super.getLoggingOptions().getCredentials();
			if (credentials != null) {
				loggingOptionsBuilder.setCredentials(credentials);
			}

			// set User-Agent
			loggingOptionsBuilder.setHeaderProvider(new UserAgentHeaderProvider(this.getClass()));

			if (this.getMaxThreads() != null) {
				System.out.println("**** HELLOO, will create custom thread pool");

				loggingOptionsBuilder.setTransportOptions(
						LoggingOptions.getDefaultGrpcTransportOptions().toBuilder()
								.setExecutorFactory(new FixedThreadExecutorFactory(this.maxThreads))
								.build());

			}

			//loggingOptionsBuilder.setRetrySettings()
/*			loggingOptionsBuilder.setTransportOptions(
					GrpcTransportOptions.newBuilder()
							.setExecutorFactory(FixedExecutorProvider.create())
							.build());*/

			this.loggingOptions = loggingOptionsBuilder.build();
		}

		return this.loggingOptions;
	}

	static class FixedThreadExecutorFactory implements GrpcTransportOptions.ExecutorFactory<ScheduledExecutorService> {
		private int numThreads;
		volatile int threadSuffix = 0;

		FixedThreadExecutorFactory(int numThreads) {
			this.numThreads = numThreads;
		}

		@Override
		public ScheduledExecutorService get() {
			ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
			scheduler.setPoolSize(this.numThreads);

			System.out.println("***** creating thread pool  " + "gcp-logging-" + threadSuffix);
			scheduler.setThreadNamePrefix("gcp-logging-" + threadSuffix);
			threadSuffix++;

			scheduler.setDaemon(true);
			scheduler.initialize();
			return scheduler.getScheduledExecutor();
		}

		@Override
		public void release(ScheduledExecutorService scheduledExecutorService) {
			System.out.println("***** shutting down thread pool " );
			scheduledExecutorService.shutdown();
		}
	}

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(Integer maxThreads) {
		//System.out.println("****** HELLO, setting max threads to " + maxThreads);

		this.maxThreads = maxThreads;
	}

	@Override
	public synchronized void stop() {
		super.stop();

		System.out.println("****** HELLO, cleaning up thread pool");
		// clean up thread pool
		if (this.executorService != null) {
			this.executorService.shutdown();
		}
	}
}
