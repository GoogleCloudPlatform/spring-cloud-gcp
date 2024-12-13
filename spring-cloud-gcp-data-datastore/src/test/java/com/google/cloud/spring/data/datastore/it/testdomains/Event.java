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

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.annotation.Id;

@Entity
public class Event {
  @Id
  private String eventName;

  private Map<CommunicationChannels, String> preferences;

  public Event(String eventName, Map<CommunicationChannels, String> preferences) {
    this.eventName = eventName;
    this.preferences = preferences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Event event = (Event) o;
    return Objects.equals(this.eventName, event.eventName)
        && Objects.equals(this.preferences, event.preferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.eventName, this.preferences);
  }
}
