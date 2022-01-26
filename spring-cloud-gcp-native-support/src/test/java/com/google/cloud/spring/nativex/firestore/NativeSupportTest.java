/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.nativex.firestore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.data.firestore.Document;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.ServiceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.Repository;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.nativex.type.NativeContext;
import org.springframework.nativex.type.TypeSystem;

class NativeSupportTest {

  private NativeContext nativeContext;
  private TypeSystem typeSystem;

  private final FirestoreDocumentComponentProcessor documentsComponentProcessor =
      new FirestoreDocumentComponentProcessor();
  private final FirestoreRepositoryComponentProcessor repositoryComponentProcessor =
      new FirestoreRepositoryComponentProcessor();

  @BeforeEach
  void setup() {
    nativeContext = mock(NativeContext.class);
    typeSystem =
        new TypeSystem(
            Arrays.asList(
                new File("./target/classes").toString(),
                new File("./target/test-classes").toString()));

    when(nativeContext.getTypeSystem()).thenReturn(typeSystem);
  }

  @Test
  void shouldConfigureComponentProcessorJavaSpi() {
    assertThat(ServiceLoader.load(ComponentProcessor.class))
        .anyMatch(FirestoreDocumentComponentProcessor.class::isInstance)
        .anyMatch(FirestoreRepositoryComponentProcessor.class::isInstance);
  }

  @Test
  void shouldConfigureNativeConfigurationJavaSpi() {
    assertThat(ServiceLoader.load(NativeConfiguration.class))
        .anyMatch(FirestoreNativeConfig.class::isInstance);
  }

  @Test
  void shouldHandleComponentIndexedFirestoreDocuments() {
    assertThat(
            documentsComponentProcessor.handle(
                nativeContext,
                typeSystem.resolve(TestDocument.class).getDottedName(),
                Collections.singletonList(typeSystem.resolve(Document.class).getDottedName())))
        .isTrue();
  }

  @Test
  void shouldHandleNoneIndexedFirestoreDocuments() {
    assertThat(
            documentsComponentProcessor.handle(
                nativeContext,
                typeSystem.resolve(TestDocument.class).getDottedName(),
                Collections.emptyList()))
        .isTrue();
  }

  @Test
  void shouldHandleFirestoreRepositories() {
    assertThat(
            repositoryComponentProcessor.handle(
                nativeContext,
                typeSystem.resolve(TestDocumentRepository.class).getDottedName(),
                Collections.singletonList(typeSystem.resolve(Repository.class).getDottedName())))
        .isTrue();
  }
}
