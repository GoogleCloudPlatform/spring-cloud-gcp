/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;

/**
 * This class represents a single computer stored in Datastore. To demonstrate template usage
 * without repository.
 */
@Entity(name = "computer")
public class Computer {
  @Id Long id;

  private final String brand;

  private final String model;

  private final int year;

  public Computer(String brand, String model, int year) {
    this.brand = brand;
    this.model = model;
    this.year = year;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Computer computer)) {
      return false;
    }
    return year == computer.year
        && Objects.equal(id, computer.id)
        && Objects.equal(brand, computer.brand)
        && Objects.equal(model, computer.model);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, brand, model, year);
  }

  @Override
  public String toString() {
    return "Computer{"
        + "id="
        + this.id
        + ", brand='"
        + this.brand
        + '\''
        + ", model='"
        + this.model
        + '\''
        + ", year="
        + this.year
        + '}';
  }
}
