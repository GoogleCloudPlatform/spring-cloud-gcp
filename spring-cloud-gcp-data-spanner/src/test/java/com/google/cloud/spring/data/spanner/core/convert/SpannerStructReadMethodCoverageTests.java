/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.data.spanner.core.convert;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spanner.Struct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests to check for new mapping methods that appear in the Spanner client lib. */
class SpannerStructReadMethodCoverageTests {

  private static final Set<String> DISREGARDED_METHOD_NAMES =
      Collections.unmodifiableSet(
          new HashSet<String>(
              Arrays.asList("getColumnIndex", "getStructList", "getColumnType", "getValue")));

  // Checks that the converter is aware of all Spanner struct getter types
  @Test
  void allKnownMappingTypesTest() throws NoSuchFieldException {
    for (Method method : Struct.class.getMethods()) {
      String methodName = method.getName();
      // ignoring private methods, ones not named like a getter. Getters must also
      // only take the column index or name
      if (!Modifier.isPublic(method.getModifiers())
          || !methodName.startsWith("get")
          || method.getParameterCount() != 1
          || DISREGARDED_METHOD_NAMES.contains(methodName)) {
        continue;
      }
      Class returnType = ConversionUtils.boxIfNeeded(method.getReturnType());
      if (ConversionUtils.isIterableNonByteArrayType(returnType)) {
        Class innerReturnType =
            (Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        assertThat(StructAccessor.readIterableMapping).containsKey(innerReturnType);
      } else {
        assertThat(StructAccessor.singleItemReadMethodMapping).containsKey(returnType);
      }
    }
  }
}
