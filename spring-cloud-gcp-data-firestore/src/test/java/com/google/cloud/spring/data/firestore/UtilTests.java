/*
 * Copyright 2019-2019 the original author or authors.
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

package com.google.cloud.spring.data.firestore;

import org.junit.Test;

import com.google.cloud.spring.data.firestore.util.Util;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dmitry Solomakha
 * @since 1.2
 */
public class UtilTests {

	@Test
	public void extractDatabasePathTest() {
		String actualDbPath = "projects/MY_PROJECT/databases/MY_DB_ID";
		String extractedDbPath = Util.extractDatabasePath(actualDbPath + "/abc/def");

		assertThat(extractedDbPath).isEqualTo(actualDbPath);
	}
}

