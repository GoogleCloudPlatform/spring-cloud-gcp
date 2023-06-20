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

class LongIntegerConverter implements SpannerClientLibrariesConverter<Integer> {

  @Override
  public boolean canConvert(Class<?> inputClass, Class<?> resultClass) {
    return inputClass == Long.class && resultClass == Integer.class;
  }

  @Override
  public Integer convert(Object input) {
    if (!canConvert(input.getClass(), Integer.class)) {
      throw new ConversionFailureException(
          String.format("Unable to convert %s to %s", ((Object) input.getClass()).getClass(),
              Integer.class));
    }
    long val = (Long) input;
    if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
      throw new ConversionFailureException(String.format("%d is out of range for Integer", val));
    }
    return (int) val;
  }
}
