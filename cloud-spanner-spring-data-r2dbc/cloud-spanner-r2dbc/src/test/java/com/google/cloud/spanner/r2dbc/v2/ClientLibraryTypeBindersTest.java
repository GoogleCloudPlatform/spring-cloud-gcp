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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement.Builder;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.BindingFailureException;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import java.math.BigDecimal;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ClientLibraryTypeBindersTest {
  ValueBinder valueBinder;

  Builder statementBuilder;

  @BeforeEach
  public void setUp() {
    this.valueBinder = Mockito.mock(ValueBinder.class);
    this.statementBuilder = Mockito.mock(Builder.class);
    when(this.statementBuilder.bind(anyString())).thenReturn(this.valueBinder);
  }

  @Test
  void unsupportedTypeThrowsException() {
    Random rand = new Random();
    assertThatThrownBy(() -> ClientLibraryBinder.bind(this.statementBuilder, "a", rand))
        .isInstanceOf(BindingFailureException.class)
        .hasMessageContaining("Can't find a binder for type: class java.util.Random");

    TypedNull randNull = new TypedNull(Random.class);
    assertThatThrownBy(() -> ClientLibraryBinder.bind(this.statementBuilder, "a", randNull))
        .isInstanceOf(BindingFailureException.class)
        .hasMessageContaining("Can't find a binder for type: class java.util.Random");
  }

  @Test
  void longBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", 1L);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Long.class));
    verify(this.valueBinder).to((Long) 1L);
    verify(this.valueBinder).to((Long) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void doubleBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", 2.0);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Double.class));
    verify(this.valueBinder).to((Double) 2.0);
    verify(this.valueBinder).to((Double) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void booleanBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", true);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Boolean.class));
    verify(this.valueBinder).to((Boolean) true);
    verify(this.valueBinder).to((Boolean) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void byteArrayBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", ByteArray.copyFrom("abc"));
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(ByteArray.class));
    verify(this.valueBinder).to(ByteArray.copyFrom("abc"));
    verify(this.valueBinder).to((ByteArray) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void dateBinderTest() {
    Date date = Date.fromYearMonthDay(1992, 12, 31);
    ClientLibraryBinder.bind(this.statementBuilder, "a", date);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Date.class));
    verify(this.valueBinder).to(date);
    verify(this.valueBinder).to((Date) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void stringBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", "abc");
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(String.class));
    verify(this.valueBinder).to("abc");
    verify(this.valueBinder).to((String) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void timestampBinderTest() {
    Timestamp ts = Timestamp.ofTimeMicroseconds(123456);
    ClientLibraryBinder.bind(this.statementBuilder, "a", ts);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Timestamp.class));
    verify(this.valueBinder).to(ts);
    verify(this.valueBinder).to((Timestamp) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  void bigDecimalBinderTest() {
    ClientLibraryBinder.bind(this.statementBuilder, "a", BigDecimal.TEN);
    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(BigDecimal.class));
    verify(this.valueBinder).to(BigDecimal.TEN);
    verify(this.valueBinder).to((BigDecimal) null);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  public void integerBindsAsLong() {

    ClientLibraryBinder.bind(this.statementBuilder, "a", 123);
    verify(this.valueBinder).to((Long) 123L);
    verifyNoMoreInteractions(this.valueBinder);
  }

  @Test
  public void integerNullBindsAsLong() {

    ClientLibraryBinder.bind(this.statementBuilder, "b", new TypedNull(Integer.class));
    verify(this.valueBinder).to((Long) null);
    verifyNoMoreInteractions(this.valueBinder);

  }

}
