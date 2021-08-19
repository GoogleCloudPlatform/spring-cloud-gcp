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

import java.util.Arrays;
import java.util.List;

import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.repository.support.SimpleSpannerRepository;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleSpannerRepositoryTests {

	@Test
	public void deleteAllByIdUnimplemented() {
		SpannerTemplate mockTemplate = mock(SpannerTemplate.class);
		SpannerMappingContext mockSpannerMappingContext = mock(SpannerMappingContext.class);
		SimpleSpannerRepository<Book, String> repository = new SimpleSpannerRepository<>(mockTemplate, Book.class);

		ConverterAwareMappingSpannerEntityProcessor processor = new ConverterAwareMappingSpannerEntityProcessor(
				mockSpannerMappingContext);
		when(mockTemplate.getSpannerEntityProcessor()).thenReturn(processor);

		List<String> keys = Arrays.asList("1", "2");

		KeySet.Builder builder = KeySet.newBuilder();
		builder.addKey(Key.of("1"));
		builder.addKey(Key.of("2"));
		KeySet keySet = builder.build();

		repository.deleteAllById(keys);
		verify(mockTemplate).delete(Book.class, keySet);
	}

	static class Book {
		String id;
	}
}
