/*
 * Copyright 2024 the original author or authors.
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

package com.google.cloud.spring.data.spanner.core.mapping.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;

public class InstantTypeAdapter extends TypeAdapter<Instant> {
  @Override
  public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
    if (instant == null) {
      jsonWriter.nullValue();
    } else {
      jsonWriter.value(instant.toString());
    }
  }

  @Override
  public Instant read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      return null;
    }
    return Instant.parse(jsonReader.nextString());
  }
}
