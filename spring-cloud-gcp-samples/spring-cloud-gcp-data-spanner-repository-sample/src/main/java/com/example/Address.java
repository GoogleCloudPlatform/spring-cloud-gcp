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

package com.example;

import java.util.Objects;

public class Address {
  private String streetName;

  private Long streetNumber;

  private Boolean active;

  public Address(Long streetNumber, String streetName, Boolean active) {
    this.streetName = streetName;
    this.streetNumber = streetNumber;
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Address)) {
      return false;
    }
    Address that = (Address) o;
    return Objects.equals(active, that.active)
        && Objects.equals(streetName, that.streetName)
        && Objects.equals(streetNumber, that.streetNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(streetName, streetNumber, active);
  }

  @Override
  public String toString() {
    return "Address{"
        + "number ="
        + streetNumber
        + ", street Name='"
        + streetName
        + '\''
        + ", active="
        + active
        + '}';
  }
}
