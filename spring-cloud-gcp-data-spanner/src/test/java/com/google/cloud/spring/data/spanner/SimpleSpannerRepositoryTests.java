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

package com.google.cloud.spring.data.spanner;

import java.util.ArrayList;

import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.repository.support.SimpleSpannerRepository;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class SimpleSpannerRepositoryTests {

	@Test
	public void deleteAllByIdUnimplemented() {
		SpannerTemplate mockTemplate = mock(SpannerTemplate.class);
		SimpleSpannerRepository<Book, String> repository = new SimpleSpannerRepository<>(mockTemplate, Book.class);

		assertThatThrownBy(() -> repository.deleteAllById(new ArrayList<>()))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	static class Book {
		String id;
	}
}
