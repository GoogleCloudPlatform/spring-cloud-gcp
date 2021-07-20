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

import java.util.Collections;
import java.util.ServiceLoader;

import com.google.cloud.spring.data.firestore.Document;
import org.junit.jupiter.api.Test;

import org.springframework.data.repository.Repository;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.nativex.type.TypeSystem;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.nativex.hint.AccessBits.DECLARED_CONSTRUCTORS;
import static org.springframework.nativex.hint.AccessBits.DECLARED_FIELDS;
import static org.springframework.nativex.hint.AccessBits.DECLARED_METHODS;

class NativeSupportTest {

	private final NativeTestContext nativeContext = new NativeTestContext();
	private final TypeSystem typeSystem = nativeContext.getTypeSystem();

	private final FirestoreDocumentComponentProcessor documentsComponentProcessor =
			new FirestoreDocumentComponentProcessor();
	private final FirestoreRepositoryComponentProcessor repositoryComponentProcessor =
			new FirestoreRepositoryComponentProcessor();

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
						Collections.singletonList(typeSystem.resolve(Document.class).getDottedName())
				)
		)
				.isTrue();
	}

	@Test
	void shouldHandleNoneIndexedFirestoreDocuments() {
		assertThat(
				documentsComponentProcessor.handle(
						nativeContext,
						typeSystem.resolve(TestDocument.class).getDottedName(),
						Collections.emptyList()
				)
		).isTrue();
	}

	@Test
	void shouldAddFullReflectionForEntityTypes() {
		processDocument();

		assertThat(nativeContext.getReflectionEntry(TestDocument.class))
				.satisfies(config -> {
					assertThat(config.getAccessBits()).isEqualTo(AccessBits.FULL_REFLECTION);
				});
	}

	@Test
	void shouldIncludeReachableTypes() {
		processDocument();

		assertThat(nativeContext.hasReflectionEntry(TestDocument.Address.class)).isTrue();
	}

	@Test
	void shouldHandleFirestoreRepositories() {
		assertThat(
				repositoryComponentProcessor.handle(
						nativeContext,
						typeSystem.resolve(TestDocumentRepository.class).getDottedName(),
						Collections.singletonList(typeSystem.resolve(Repository.class).getDottedName())
				)
		)
				.isTrue();
	}

	@Test
	void shouldProcessFirestoreRepositories() {
		processRepository();

		assertThat(nativeContext.getReflectionEntries(TestDocument.class))
				.anyMatch(config -> config.getAccessBits().equals(DECLARED_CONSTRUCTORS | DECLARED_METHODS | DECLARED_FIELDS));
		String repoFqn = typeSystem.resolve(TestDocumentRepository.class).getDottedName();
		assertThat(nativeContext.getProxyEntries())
				.containsKey(repoFqn);
		// verifying org.springframework.data.SpringDataComponentProcessor.registerRepositoryInterface
		assertThat(nativeContext.getProxyEntries().get(repoFqn))
				.contains(asList(
						repoFqn,
						"org.springframework.aop.SpringProxy",
						"org.springframework.aop.framework.Advised",
						"org.springframework.core.DecoratingProxy"
				))
				.contains(asList(
						repoFqn,
						typeSystem.resolve(Repository.class).getDottedName(),
						"org.springframework.transaction.interceptor.TransactionalProxy",
						"org.springframework.aop.framework.Advised",
						"org.springframework.core.DecoratingProxy"
				))
				.contains(asList(
						repoFqn,
						typeSystem.resolve(Repository.class).getDottedName(),
						"org.springframework.transaction.interceptor.TransactionalProxy",
						"org.springframework.aop.framework.Advised",
						"org.springframework.core.DecoratingProxy",
						"java.io.Serializable"
				));
	}

	private void processDocument() {
		documentsComponentProcessor.process(nativeContext, typeSystem.resolve(TestDocument.class).getDottedName(), Collections.emptyList());
	}

	private void processRepository() {
		repositoryComponentProcessor.process(nativeContext, typeSystem.resolve(TestDocumentRepository.class).getDottedName(), Collections.emptyList());
	}
}
