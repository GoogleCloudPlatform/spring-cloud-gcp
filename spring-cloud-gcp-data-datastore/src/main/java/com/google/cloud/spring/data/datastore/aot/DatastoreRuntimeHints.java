/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.spring.data.datastore.aot;

import com.google.cloud.spring.data.datastore.core.convert.DatastoreNativeTypes;
import com.google.cloud.spring.data.datastore.core.mapping.event.AfterDeleteEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.AfterFindByKeyEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.AfterQueryEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.AfterSaveEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.BeforeDeleteEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.BeforeSaveEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.DeleteEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.ReadEvent;
import com.google.cloud.spring.data.datastore.core.mapping.event.SaveEvent;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class DatastoreRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    for (Class<?> simpleType : DatastoreNativeTypes.DATASTORE_NATIVE_TYPES) {
      hints.reflection().registerType(TypeReference.of(simpleType), MemberCategory.PUBLIC_CLASSES);
    }
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(
                TypeReference.of(AfterDeleteEvent.class),
                TypeReference.of(AfterFindByKeyEvent.class),
                TypeReference.of(AfterQueryEvent.class),
                TypeReference.of(AfterSaveEvent.class),
                TypeReference.of(BeforeDeleteEvent.class),
                TypeReference.of(BeforeSaveEvent.class),
                TypeReference.of(DeleteEvent.class),
                TypeReference.of(ReadEvent.class),
                TypeReference.of(SaveEvent.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS));
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(TypeReference.of(java.lang.String.class),
                TypeReference.of(java.util.HashMap.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS));

    hints.proxies().registerJdkProxy(TypeReference.of("com.google.cloud.spring.data.datastore.core.LazyUtil"));
  }
}
