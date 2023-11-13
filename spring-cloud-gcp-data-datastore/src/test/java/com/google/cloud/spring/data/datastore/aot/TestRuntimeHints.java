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

import com.google.cloud.spring.data.datastore.entities.CustomMap;
import com.google.cloud.spring.data.datastore.it.testdomains.AncestorEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.AncestorEntity.DescendantEntry;
import com.google.cloud.spring.data.datastore.it.testdomains.EmbeddableTreeNode;
import com.google.cloud.spring.data.datastore.it.testdomains.Event;
import com.google.cloud.spring.data.datastore.it.testdomains.ParentEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.ServiceConfiguration;
import com.google.cloud.spring.data.datastore.it.testdomains.SubEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TreeCollection;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class TestRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(
                TypeReference.of(AncestorEntity.class),
                TypeReference.of(DescendantEntry.class),
                TypeReference.of(EmbeddableTreeNode.class),
                TypeReference.of(ServiceConfiguration.class),
                TypeReference.of(CustomMap.class),
                TypeReference.of(TreeCollection.class),
                TypeReference.of(SubEntity.class),
                TypeReference.of(Event.class),
                TypeReference.of(ParentEntity.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS));
  }
}
