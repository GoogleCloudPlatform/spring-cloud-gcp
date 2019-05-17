/*
 * Copyright 2019 Google LLC
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

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.math.BigDecimal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for {@link DefaultCodecs}.
 */
public class DefaultCodecsNegativeTest {

  private Codecs codecs = new DefaultCodecs();

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void encodeException() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("Cannot encode parameter of type java.math.BigDecimal");

    codecs.encode(BigDecimal.valueOf(100));
  }

  @Test
  public void decodeException() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("Cannot decode value of type code: STRING\n"
        + " to java.lang.Integer");

    Value value = codecs.encode("abc");
    codecs.decode(value, Type.newBuilder().setCode(TypeCode.STRING).build(), Integer.class);
  }
}
