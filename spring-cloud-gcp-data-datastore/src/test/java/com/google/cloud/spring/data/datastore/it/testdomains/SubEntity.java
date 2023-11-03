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

package com.google.cloud.spring.data.datastore.it.testdomains;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

/** Test class. */
@Entity
public class SubEntity {
  @Id public Key key;

  @Reference public ParentEntity parent;

  @Reference public SubEntity sibling;

  @Unindexed public List<String> stringList;

  public String stringProperty;

  @Unindexed public List<SubEntity> embeddedSubEntities;
}
