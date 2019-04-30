/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.spanner.r2dbc.util;

import javax.annotation.Nullable;

/**
 * Lightweight assertion support.
 */
public class Assert {

  // static methods only; no instantiation.
  private Assert() {}

  /**
   * Checks that a specified object reference is not {@code null} and throws a customized
   * {@link IllegalArgumentException} if it is.
   *
   * @param o       the object reference to check for nullity
   * @param message informative message to be used in the event that an
   * {@link IllegalArgumentException} is thrown
   * @throws IllegalArgumentException if {@code o} is {@code null}
   */
  public static void requireNonNull(@Nullable Object o, String message) {
    if (o == null) {
      throw new IllegalArgumentException(message);
    }
  }

}
