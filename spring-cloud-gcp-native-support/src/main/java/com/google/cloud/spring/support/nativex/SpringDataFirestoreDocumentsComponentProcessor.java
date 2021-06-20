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

package com.google.cloud.spring.support.nativex;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.nativex.domain.reflect.FieldDescriptor;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.type.AccessDescriptor;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.Field;
import org.springframework.nativex.type.NativeContext;
import org.springframework.nativex.type.Type;
import org.springframework.nativex.type.TypeProcessor;

/**
 * Native Component processor adding reflection support for classes annotated with
 * {@link com.google.cloud.spring.data.firestore.Document}. These classes will be found by spring aot if
 * <code>spring-context-indexer</code> was used to index such classes.
 *
 * @see <a href="https://github.com/spring-projects-experimental/spring-native/blob/832991d57686627b06792d55555cb9497475b3c5/spring-native-configuration/src/main/java/org/springframework/data/JpaComponentProcessor.java#L34">JpaComponentProcessor</a>
 */
public class SpringDataFirestoreDocumentsComponentProcessor implements ComponentProcessor {
	private static final Log logger = LogFactory.getLog(SpringDataFirestoreDocumentsComponentProcessor.class);

	private final TypeProcessor typeProcessor = new TypeProcessor(
			(type, context) -> !type.isPartOfDomain("sun.") && !type.isPartOfDomain("jdk."),
			this::registerTypeInConfiguration,
			this::registerAnnotationInConfiguration
	).named("SpringDataFirestoreDocumentsComponentProcessor");

	@Override
	public boolean handle(NativeContext imageContext, String componentType, List<String> classifiers) {
		return classifiers.contains("com.google.cloud.spring.data.firestore.Document");
	}

	@Override
	public void process(NativeContext imageContext, String componentType, List<String> classifiers) {
		Type domainType = imageContext.getTypeSystem().resolveName(componentType);
		typeProcessor.use(imageContext).toProcessType(domainType);
	}

	private void registerAnnotationInConfiguration(Type annotation, NativeContext context) {
		logger.info(String.format(
				"SpringDataFirestoreDocumentsComponentProcessor: adding reflection configuration AccessBits.ANNOTATION for annotation %s.",
				annotation.getDottedName()));
		context.addReflectiveAccess(annotation.getDottedName(), AccessBits.ANNOTATION);
	}

	private void registerTypeInConfiguration(Type type, NativeContext context) {
		AccessDescriptor accessDescriptor = new AccessDescriptor(AccessBits.FULL_REFLECTION,
				Collections.emptyList(), fieldDescriptorsForType(type, context));

		logger.info(String.format("SpringDataFirestoreDocumentsComponentProcessor: adding reflection configuration type %s - %s",
				type.getDottedName(), accessDescriptor));

		context.addReflectiveAccess(type.getDottedName(), accessDescriptor);
	}

	private List<FieldDescriptor> fieldDescriptorsForType(Type type, NativeContext context) {

		if (type.isPartOfDomain("java.")) { // other well known domains ?
			logger.info(String.format("SpringDataFirestoreDocumentsComponentProcessor: skipping field inspection for type %s.",
					type.getDottedName()));
			return Collections.emptyList();
		}

		return type.getFields()
				.stream()
				.filter(Field::isFinal)
				.map(field -> {
					logger.info(String.format(
							"SpringDataFirestoreDocumentsComponentProcessor: detected final field %s for type %s. Setting allowWrite=true.",
							field.getName(), type.getDottedName()));
					return new FieldDescriptor(field.getName(), true, true);
				})
				.collect(Collectors.toList());
	}

}
