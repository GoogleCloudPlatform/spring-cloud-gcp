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
import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;

@Entity
public class Employee {
  @Id public Key id;

  @Descendants public List<Employee> subordinates;

  public Employee(List<Employee> subordinates) {
    this.subordinates = subordinates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Employee that = (Employee) o;
    return Objects.equals(this.id, that.id) && Objects.equals(this.subordinates, that.subordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.subordinates);
  }

  @Override
  public String toString() {
    return "Employee{"
        + "id="
        + this.id.getNameOrId()
        + ", subordinates="
        + (this.subordinates != null
            ? this.subordinates.stream()
                .map(employee -> employee.id.getNameOrId())
                .collect(Collectors.toList())
            : null)
        + '}';
  }
}
