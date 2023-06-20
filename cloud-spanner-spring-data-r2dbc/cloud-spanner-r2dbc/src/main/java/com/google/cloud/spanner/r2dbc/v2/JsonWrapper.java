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

package com.google.cloud.spanner.r2dbc.v2;

import com.google.cloud.spanner.Value;
import com.google.common.base.Objects;

/** Wrapper class to hold Json value. */
public class JsonWrapper {
  private String jsonString;

  public JsonWrapper(String jsonString) {
    this.jsonString = jsonString;
  }

  protected Value getJsonVal() {
    return Value.json(this.jsonString);
  }

  public static JsonWrapper of(String jsonString) {
    return new JsonWrapper(jsonString);
  }

  @Override
  public String toString() {
    return this.jsonString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonWrapper that = (JsonWrapper) o;
    return Objects.equal(this.jsonString, that.jsonString);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.jsonString);
  }
}
