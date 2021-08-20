/*
 * Copyright 2021-2021 the original author or authors.
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

import java.util.Arrays;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SimpleFirestoreReactiveRepositoryTests {

	@Test
	public void deleteAllById() {
		FirestoreTemplate mockTemplate = mock(FirestoreTemplate.class);
		SimpleFirestoreReactiveRepository<String> repository = new SimpleFirestoreReactiveRepository<>(mockTemplate,
				String.class);
		Publisher<String> ids = Flux.fromIterable(Arrays.asList("1", "2"));
		repository.deleteAllById(ids);
		verify(mockTemplate).deleteById(same(ids), eq(String.class));
	}
}
