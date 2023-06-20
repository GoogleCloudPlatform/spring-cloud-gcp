/*
 * Copyright 2019-2020 Google LLC
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

import com.google.protobuf.ByteString;
import reactor.util.annotation.Nullable;

/**
 * Lightweight assertion support.
 */
public class Assert {

  private Assert() {
    // Prevent instantiation.
  }

  /**
   * Checks that a specified object reference is not {@code null} and throws a customized
   * {@link IllegalArgumentException} if it is.
   *
   * @param t the object reference to check for nullity
   * @param message informative message to be used in the event that an
   *     {@link IllegalArgumentException} is thrown
   * @param <T> the type of object reference
   *
   * @return the original object {@code t}
   *
   * @throws IllegalArgumentException if {@code o} is {@code null}
   */
  public static <T> T requireNonNull(@Nullable T t, String message) {
    if (t == null) {
      throw new IllegalArgumentException(message);
    }

    return t;
  }

  /**
   * Checks that a specified object reference is not {@code null} or an empty string, and throws a
   * customized {@link IllegalArgumentException} if it is.
   *
   * @param s string to check
   * @param message informative message to be used in the event that an
   *     {@link IllegalArgumentException} is thrown
   *
   * @return the original string {@code s}
   *
   * @throws IllegalArgumentException if {@code o} is {@code null}
   */
  public static ByteString requireNonEmpty(@Nullable ByteString s, String message) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException(message);
    }

    return s;
  }

  /**
   * Checks that a specified object reference is not {@code null} or an empty string, and throws a
   * customized {@link IllegalArgumentException} if it is.
   *
   * @param s string to check
   * @param message informative message to be used in the event that an
   *     {@link IllegalArgumentException} is thrown
   *
   * @return the original string {@code s}
   *
   * @throws IllegalArgumentException if {@code o} is {@code null}
   */
  public static String requireNonEmpty(@Nullable String s, String message) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException(message);
    }

    return s;
  }

}
