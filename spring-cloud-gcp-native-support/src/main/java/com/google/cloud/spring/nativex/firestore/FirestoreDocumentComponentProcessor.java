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

import java.util.List;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.NativeContext;
import org.springframework.nativex.type.Type;
import org.springframework.nativex.type.TypeProcessor;

/**
 * Native Component processor adding reflection support for classes annotated with {@link
 * com.google.cloud.spring.data.firestore.Document}. These classes will be found by spring aot if
 * <code>spring-context-indexer</code> was used to index such classes.
 *
 * @see <a
 *     href="https://github.com/spring-projects-experimental/spring-native/blob/832991d57686627b06792d55555cb9497475b3c5/spring-native-configuration/src/main/java/org/springframework/data/JpaComponentProcessor.java#L34">JpaComponentProcessor</a>
 */
public class FirestoreDocumentComponentProcessor implements ComponentProcessor {

  private static final String FIRESTORE_DOCUMENT_FQN =
      "com.google.cloud.spring.data.firestore.Document";

  private final TypeProcessor typeProcessor =
      TypeProcessor.namedProcessor("FirestoreDocumentComponentProcessor");

  @Override
  public boolean handle(
      NativeContext imageContext, String componentType, List<String> classifiers) {
    if (classifiers.contains(FIRESTORE_DOCUMENT_FQN)) {
      return true;
    }
    Type type = imageContext.getTypeSystem().resolveName(componentType);
    return type.getAnnotations().stream()
        .anyMatch(tag -> tag.getDottedName().equals(FIRESTORE_DOCUMENT_FQN));
  }

  @Override
  public void process(NativeContext imageContext, String componentType, List<String> classifiers) {
    Type domainType = imageContext.getTypeSystem().resolveName(componentType);
    typeProcessor.use(imageContext).toProcessType(domainType);
  }
}
