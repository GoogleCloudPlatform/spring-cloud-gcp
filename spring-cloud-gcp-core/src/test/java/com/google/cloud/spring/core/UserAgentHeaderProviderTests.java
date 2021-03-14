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

package com.google.cloud.spring.core;

import java.util.regex.Pattern;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A more complete integration test is available in {@link UserAgentHeaderProviderIntegrationTests}.
 *
 * @author João André Martins
 * @author Mike Eltsufin
 * @author Chengyuan Zhao
 */
public class UserAgentHeaderProviderTests {

	static final String USER_AGENT_HEADER_NAME = "user-agent";

	private String versionRegex;

	public UserAgentHeaderProviderTests() {
		this(".*"); // no version verification because we don't have JAR MANIFEST
	}

	UserAgentHeaderProviderTests(String versionRegex) {
		this.versionRegex = versionRegex;
	}
	/**
	 * This test is check if the generated user-agent header is in the right format.
	 */
	@Test
	public void testGetHeaders() {
		UserAgentHeaderProvider subject = new UserAgentHeaderProvider(this.getClass());

		assertThat(subject.getHeaders()).containsKey(USER_AGENT_HEADER_NAME);
		assertThat(subject.getHeaders().get(USER_AGENT_HEADER_NAME)).isEqualTo(subject.getUserAgent());
		assertThat(subject.getHeaders().get(USER_AGENT_HEADER_NAME)).matches(
				Pattern.compile("Spring/" + versionRegex + " spring-cloud-gcp-core/" + versionRegex));
		assertThat(subject.getHeaders()).hasSize(1);
	}
}
