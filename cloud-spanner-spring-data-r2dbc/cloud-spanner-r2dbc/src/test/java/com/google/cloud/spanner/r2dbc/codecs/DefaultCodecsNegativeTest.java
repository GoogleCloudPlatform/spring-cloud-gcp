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

package com.google.cloud.spanner.r2dbc.codecs;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link DefaultCodecs}.
 */
public class DefaultCodecsNegativeTest {

  private Codecs codecs = new DefaultCodecs();

  @Test
  public void encodeException() {

    assertThrows(IllegalArgumentException.class,
        () -> this.codecs.encode(BigDecimal.valueOf(100)),
        "Cannot encode parameter of type java.math.BigDecimal");
  }

  @Test
  public void decodeException() {

    assertThrows(IllegalArgumentException.class,
        () -> {
          Value value = this.codecs.encode("abc");
          this.codecs.decode(
              value, Type.newBuilder().setCode(TypeCode.STRING).build(), Integer.class);
        },
        "Cannot decode value of type code: STRING\n to java.lang.Integer"
    );

  }
}
