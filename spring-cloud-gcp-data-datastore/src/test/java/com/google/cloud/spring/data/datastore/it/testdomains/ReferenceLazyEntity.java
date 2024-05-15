/*
 * Copyright 2017-2019 the original author or authors.
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

import com.google.cloud.spring.data.datastore.core.mapping.LazyReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

/** A test class that holds references and lazy references as relationships. */
public class ReferenceLazyEntity {
  @Id public Long id;

  public String name;

  @Reference public ReferenceLazyEntity sibling;

  @LazyReference public List<ReferenceLazyEntity> children;

  public ReferenceLazyEntity(String name, ReferenceLazyEntity sibling, List<ReferenceLazyEntity> children) {
    this.name = name;
    this.sibling = sibling;
    this.children = children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenceLazyEntity that = (ReferenceLazyEntity) o;
    return Objects.equals(this.id, that.id)
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.sibling, that.sibling)
        && new HashSet<>((this.children != null) ? this.children : Collections.emptyList())
            .equals(
                new HashSet<>((that.children != null) ? that.children : Collections.emptyList()));
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.id, this.name, this.sibling, this.children);
  }

  @Override
  public String toString() {
    return "ReferenceEntry{"
        + "id="
        + this.id
        + ", name='"
        + this.name
        + '\''
        + ", sibling="
        + this.sibling
        + ", children="
        + this.children
        + '}';
  }
}
