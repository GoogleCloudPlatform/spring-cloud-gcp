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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

class SimpleFirestoreReactiveRepositoryTests {

  @Test
  void deleteAllById() {
    FirestoreTemplate mockTemplate = mock(FirestoreTemplate.class);
    SimpleFirestoreReactiveRepository<String> repository =
        new SimpleFirestoreReactiveRepository<>(mockTemplate, String.class);
    Iterable<String> idList = Arrays.asList("1", "2");
    // only testing that the request is passed through to FirestoreTemplate as expected
    repository.deleteAllById(idList);

    ArgumentCaptor<Publisher> argumentCaptor = ArgumentCaptor.forClass(Publisher.class);
    verify(mockTemplate).deleteById(argumentCaptor.capture(), eq(String.class));
    List<String> arguments =
        (List<String>) ((Flux) argumentCaptor.getValue()).collectList().block();
    assertThat(arguments).containsExactlyInAnyOrder("1", "2");
  }
}
