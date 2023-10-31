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

import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

/** Test class. */
@Entity
public class ParentEntity {
  @Id public Long id;

  @Reference public List<SubEntity> subEntities;

  @Reference public SubEntity singularSubEntity;

  @Descendants public List<SubEntity> descendants;

  public ParentEntity(
      List<SubEntity> subEntities, List<SubEntity> descendants, SubEntity singularSubEntity) {
    this.subEntities = subEntities;
    this.singularSubEntity = singularSubEntity;
    this.descendants = descendants;
  }
}
