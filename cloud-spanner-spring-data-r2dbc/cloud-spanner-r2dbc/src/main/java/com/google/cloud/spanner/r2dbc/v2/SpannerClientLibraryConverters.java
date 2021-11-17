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

import com.google.cloud.spanner.r2dbc.ConversionFailureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class SpannerClientLibraryConverters {

  private static final List<SpannerClientLibrariesConverter<?>> converters = createConverters();

  private static List<SpannerClientLibrariesConverter<?>> createConverters() {
    ArrayList<SpannerClientLibrariesConverter<?>> converters = new ArrayList<>();
    converters.add(new LongIntegerConverter());
    converters.add(new StringToJsonConverter());
    return converters;
  }

  static <T> T convert(Object value, Class<T> type) {
    Optional<SpannerClientLibrariesConverter<?>> converter = converters.stream()
        .filter(candidate -> candidate.canConvert(value.getClass(), type))
        .findFirst();
    if (!converter.isPresent()) {
      throw new ConversionFailureException(
          String.format("Unable to convert %s to %s", value.getClass(), type));
    }
    return (T) converter.get().convert(value);
  }
}
