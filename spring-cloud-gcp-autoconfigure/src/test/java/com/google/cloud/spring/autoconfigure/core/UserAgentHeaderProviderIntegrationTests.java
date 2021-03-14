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

package com.google.cloud.spring.autoconfigure.core;

/**
 * This needs to be an integration test because the JAR MANIFEST has to be available for
 * this.getClass().getPackage().getImplementationVersion() to work properly.
 *
 * @author João André Martins
 * @author Mike Eltsufin
 * @author Chengyuan Zhao
 */
public class UserAgentHeaderProviderIntegrationTests extends UserAgentHeaderProviderTests {

	public UserAgentHeaderProviderIntegrationTests() {
		super("\\d+\\.\\d+\\.\\d+(\\-RC\\d+)?(\\-SNAPSHOT)?");
	}
}
