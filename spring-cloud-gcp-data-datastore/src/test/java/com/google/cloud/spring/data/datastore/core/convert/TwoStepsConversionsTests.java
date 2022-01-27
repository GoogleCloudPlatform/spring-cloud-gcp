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

package com.google.cloud.spring.data.datastore.core.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

/**
 * Unit tests for {@link TwoStepsConversions}.
 *
 * <p>Higher level tests invoking {@link TwoStepsConversions} as part of read/write operations are
 * in {@link TestDatastoreItemCollections}.
 *
 * @since 1.1
 */
class TwoStepsConversionsTests {

  private final DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();

  private final TwoStepsConversions twoStepsConversions =
      new TwoStepsConversions(
          new DatastoreCustomConversions(Arrays.asList()), null, this.datastoreMappingContext);

  @Test
  void convertOnReadReturnsNullWhenConvertingNullSimpleValue() {

    assertThat(this.twoStepsConversions.<String>convertOnRead(null, null, String.class)).isNull();
  }

  @Test
  void convertOnReadConvertsCollectionAndElementTypesCorrectly() {

    List<String> okayList = new ArrayList<>();
    okayList.add("128");
    okayList.add("256");

    Set<Integer> result =
        this.twoStepsConversions.<Set<Integer>>convertOnRead(okayList, Set.class, Integer.class);
    assertThat(result).isNotNull().containsExactlyInAnyOrder(128, 256);
  }

  @Test
  void convertOnReadConvertsSimpleElementTypesCorrectly() {

    Integer result = this.twoStepsConversions.<Integer>convertOnRead("512", null, Integer.class);
    assertThat(result).isNotNull().isEqualTo(512);
  }

  @Test
  void convertOnReadFailsOnIncompatibleTypes() {
    assertThatThrownBy(
            () -> {
              this.twoStepsConversions.<String>convertOnRead(3, null, String.class);
            })
        .isInstanceOf(DatastoreDataException.class)
        .hasMessageContaining(
            "Unable to convert class java.lang.Integer to class java.lang.String");
  }

  @Test
  void convertOnReadUsesCustomConverter() {
    List<String> numberNames = Arrays.asList("zero", "one", "two", "three", "four", "five");
    Converter<Long, String> converter =
        new Converter<Long, String>() {
          @Override
          public String convert(Long num) {
            if (num < 0 || num > 5) {
              return null;
            }
            return numberNames.get(num.intValue());
          }
        };

    TwoStepsConversions twoStepsConversionsThatSpeaksEnglish =
        new TwoStepsConversions(
            new DatastoreCustomConversions(Arrays.asList(converter)),
            null,
            this.datastoreMappingContext);
    String result =
        twoStepsConversionsThatSpeaksEnglish.<String>convertOnRead(3L, null, String.class);
    assertThat(result).isEqualTo("three");
  }

  @Test
  void convertingArrayNotSupported() {
    String[] arr = new String[] {"a", "b", "c"};

    assertThatThrownBy(
            () -> {
              this.twoStepsConversions.<String>convertOnRead(arr, List.class, String.class);
            })
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Value passed to convertOnRead expected to be Iterable");
  }
}
