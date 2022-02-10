/*
 * Copyright 2022-2022 Google LLC
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArrayToIterableBinderTest {

  @Test
  void canBindSameArrayClassOnly() {

    ArrayToIterableBinder binder =
        new ArrayToIterableBinder(CharSequence[].class, (builder, value) -> {});


    assertTrue(binder.canBind(CharSequence[].class, null));
    assertFalse(binder.canBind(CharSequence.class, null));
    assertFalse(binder.canBind(String[].class, null));
    assertFalse(binder.canBind(Object[].class, null));
  }

  @Test
  void bindWithNullValue() {
    Statement.Builder mockBuilder = mock(Statement.Builder.class);

    ArrayToIterableBinder binder =
        new ArrayToIterableBinder(String[].class, (builder, value) -> {
          assertThat(value).isNull();
        });

    binder.bind(mockBuilder, "nullColumn", null, null);
  }

  @Test
  void bindWithRealValueTurnsArrayIntoList() {
    Statement.Builder mockBuilder = mock(Statement.Builder.class);

    ArrayToIterableBinder binder =
        new ArrayToIterableBinder(String[].class, (builder, value) -> {
          assertThat(value).isInstanceOf(List.class);
          assertThat((List) value).containsExactly("apples", "oranges");
        });

    binder.bind(mockBuilder, "nullColumn", new String[] { "apples", "oranges" }, null);
  }


}
