/*
 * Copyright 2021-2021 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata.it.entities;

import com.google.common.base.Objects;

/**
 * Example custom class entity field.
 */
public class Address {
  String homeAddress;
  String workAddress;
  Integer homeZipCode;
  Integer workZipCode;

  public Address() {}

  /**
   * Constructor.
   *
   * @param homeAddress home address
   * @param workAddress work address
   * @param homeZipCode home zip code
   * @param workZipCode work zip code
   */
  public Address(String homeAddress, String workAddress, Integer homeZipCode, Integer workZipCode) {
    this.homeAddress = homeAddress;
    this.workAddress = workAddress;
    this.homeZipCode = homeZipCode;
    this.workZipCode = workZipCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Address address = (Address) o;
    return Objects.equal(this.homeAddress, address.homeAddress)
        && Objects.equal(this.workAddress, address.workAddress)
        && Objects.equal(this.homeZipCode, address.homeZipCode)
        && Objects.equal(this.workZipCode, address.workZipCode);
  }
}
