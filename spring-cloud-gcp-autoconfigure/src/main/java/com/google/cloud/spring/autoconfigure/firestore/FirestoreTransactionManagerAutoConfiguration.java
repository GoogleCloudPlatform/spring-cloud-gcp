/*
 * Copyright 2019-2021 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.firestore;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.data.firestore.mapping.FirestoreClassMapper;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import com.google.firestore.v1.FirestoreGrpc;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for {@link ReactiveFirestoreTransactionManager}.
 *
 * @author Biju Kunjummen
 * @since 2.0.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ReactiveFirestoreTransactionManager.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.firestore.enabled", matchIfMissing = true)
@AutoConfigureBefore(TransactionAutoConfiguration.class)
public class FirestoreTransactionManagerAutoConfiguration {

	private static final String ROOT_PATH_FORMAT = "projects/%s/databases/(default)/documents";

	private final String firestoreRootPath;

	FirestoreTransactionManagerAutoConfiguration(GcpFirestoreProperties gcpFirestoreProperties,
			GcpProjectIdProvider projectIdProvider) {
		String projectId = (gcpFirestoreProperties.getProjectId() != null)
				? gcpFirestoreProperties.getProjectId()
				: projectIdProvider.getProjectId();

		this.firestoreRootPath = String.format(ROOT_PATH_FORMAT, projectId);
	}

	@Bean
	@ConditionalOnMissingBean
	public ReactiveFirestoreTransactionManager firestoreTransactionManager(
			FirestoreGrpc.FirestoreStub firestoreStub, FirestoreClassMapper classMapper) {
		return new ReactiveFirestoreTransactionManager(firestoreStub, firestoreRootPath, classMapper);
	}
}
