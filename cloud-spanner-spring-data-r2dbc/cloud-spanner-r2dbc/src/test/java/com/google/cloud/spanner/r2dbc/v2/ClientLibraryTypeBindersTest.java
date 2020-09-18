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

package com.google.cloud.spanner.r2dbc.v2;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement.Builder;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class ClientLibraryTypeBindersTest {
  static ValueBinder valueBinder = Mockito.mock(ValueBinder.class);

  static Builder builder = Mockito.mock(Builder.class);

  @BeforeAll
  static void setUp() {
    when(builder.bind(anyString())).thenReturn(valueBinder);
  }

  /** Prepare parameters for parametrized test. */
  static Stream<Arguments> data() {
    return Stream.of(
        arguments(
            Long.class,
            1L,
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((Long) val)),
        arguments(
            Double.class,
            2.0d,
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((Double) val)),
        arguments(
            Boolean.class,
            true,
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((Boolean) val)),
        arguments(
            ByteArray.class,
            ByteArray.copyFrom("abc"),
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((ByteArray) val)),
        arguments(
            Date.class,
            Date.fromYearMonthDay(1992, 12, 31),
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((Date) val)),
        arguments(
            String.class,
            "abc",
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((String) val)),
        arguments(
            Timestamp.class,
            Timestamp.ofTimeMicroseconds(123456),
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((Timestamp) val)),
        arguments(
            BigDecimal.class,
            BigDecimal.TEN,
            (BiConsumer<ValueBinder, Object>) (binder, val) -> binder.to((BigDecimal) val))
    );
  }

  /** Validates that every supported type binds successfully. */
  @ParameterizedTest
  @MethodSource("data")
  <T> void binderTest(Class<T> type, Object value, BiConsumer<ValueBinder, Object> verifyer) {
    ClientLibraryBinder.bind(builder, "a", value);
    ClientLibraryBinder.bind(builder, "b", new TypedNull(type));

    ValueBinder instrumentedBinder = Mockito.verify(valueBinder, times(1));
    verifyer.accept(instrumentedBinder, value);

    instrumentedBinder = Mockito.verify(valueBinder, times(1));
    verifyer.accept(instrumentedBinder, null);

    Mockito.verifyNoMoreInteractions(valueBinder);
  }
}
