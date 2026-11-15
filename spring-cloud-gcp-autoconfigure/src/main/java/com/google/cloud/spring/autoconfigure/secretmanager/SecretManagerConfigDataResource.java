/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.secretmanager;

import java.util.Objects;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResource;

public class SecretManagerConfigDataResource extends ConfigDataResource {

  private final ConfigDataLocation location;

  private final boolean enabled;

  public SecretManagerConfigDataResource(ConfigDataLocation location) {
    this(location, true);
  }

  public SecretManagerConfigDataResource(ConfigDataLocation location, boolean enabled) {
    this.location = location;
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SecretManagerConfigDataResource)) {
      return false;
    }
    SecretManagerConfigDataResource that = (SecretManagerConfigDataResource) o;
    // The flag is part of the identity so that an import resolved again with a different value
    // is loaded again instead of being treated as already loaded.
    return enabled == that.enabled && location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, enabled);
  }

  @Override
  public String toString() {
    return "SecretManagerConfigDataResource{"
        + "location="
        + location
        + ", enabled="
        + enabled
        + '}';
  }
}
