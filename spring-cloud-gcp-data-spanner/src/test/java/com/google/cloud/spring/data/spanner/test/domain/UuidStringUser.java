/*
 * Copyright 2026 the original author or authors.
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

package com.google.cloud.spring.data.spanner.test.domain;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.util.UUID;

@Table(name = "uuid_string_users")
public class UuidStringUser {

  @PrimaryKey
  @Column(name = "user_id", spannerType = com.google.spanner.v1.TypeCode.STRING, spannerTypeMaxLength = 36)
  private UUID userId;

  private String name;

  public UuidStringUser() {}

  public UuidStringUser(UUID userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  public UUID getUserId() {
    return this.userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UuidStringUser that = (UuidStringUser) o;

    if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    int result = userId != null ? userId.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "UuidStringUser{" + "userId=" + userId + ", name='" + name + '\'' + '}';
  }
}
