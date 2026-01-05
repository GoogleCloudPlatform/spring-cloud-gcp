/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.spanner.core.mapping.event;

import java.util.Collections;
import java.util.Set;

/**
 * An event that is published just before a save operation.
 */
public final class BeforeSaveEvent extends SaveEvent {

  /**
   * Constructor.
   *
   * @param entities the target entities that need to be mutated.
   * @param includeProps the set of properties to include in the save operation.
   */
  public BeforeSaveEvent(final Iterable entities, final Set<String> includeProps) {
    super(Collections.emptyList(), entities, includeProps);
  }
}