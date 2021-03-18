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

package com.google.cloud.spanner.r2dbc.statement;

import com.google.cloud.spanner.r2dbc.codecs.Codec;
import com.google.cloud.spanner.r2dbc.codecs.Codecs;
import com.google.cloud.spanner.r2dbc.codecs.DefaultCodecs;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.protobuf.Struct;
import com.google.spanner.v1.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of bindings for a Spanner SQL statement.
 */
public class StatementBindings {

  private static final Codecs codecs = new DefaultCodecs();

  private Struct.Builder currentStruct;

  private final List<Struct> structList;

  private final Map<String, Codec> resolvedCodecs;

  private final Map<String, Type> typesMap;

  /**
   * Constructs a {@link StatementBindings} object representing a list of bindings for a Spanner
   * statement.
   */
  public StatementBindings() {
    this.currentStruct = Struct.newBuilder();
    this.structList = new ArrayList<>();
    this.resolvedCodecs = new HashMap<>();
    this.typesMap = new HashMap<>();
  }

  /**
   * Adds the current binding to the list of bindings for the statement and starts a new parameter
   * binding.
   */
  public void completeBinding() {
    if (this.currentStruct.getFieldsCount() > 0) {
      this.structList.add(this.currentStruct.build());
      this.currentStruct = Struct.newBuilder();
    }
  }

  /**
   * Add an additional param-to-value binding pair to the current parameter binding.
   *
   * @param identifier the String name of the bind variable.
   * @param value the value you wish to bind the parameter to.
   */
  public void createBind(String identifier, Object value) {
    Assert.requireNonNull(identifier, "Identifier must not be null.");
    Assert.requireNonNull(value, "Value bound must not be null.");

    Object valToStore;
    Class classToStore;

    if (value.getClass().equals(TypedNull.class)) {
      valToStore = null;
      classToStore = ((TypedNull) value).getType();
    } else {
      valToStore = value;
      classToStore = value.getClass();
    }

    Codec codec = this.resolvedCodecs
        .computeIfAbsent(identifier, n -> codecs.getCodec(classToStore));

    this.currentStruct.putFields(identifier, codec.encode(valToStore));

    if (this.structList.isEmpty()) {
      // first binding, fill types map
      Type.Builder typeBuilder = Type.newBuilder().setCode(codec.getTypeCode());
      if (codec.getArrayElementTypeCode() != null) {
        typeBuilder.setArrayElementType(
            Type.newBuilder().setCode(codec.getArrayElementTypeCode()).build());
      }
      this.typesMap.put(identifier, typeBuilder.build());
    }
  }

  /**
   * Returns the built binding for the statement.
   *
   * @return list of {@link Struct} representing all the parameter bindings for the statement.
   */
  public List<Struct> getBindings() {
    completeBinding();

    if (this.structList.isEmpty()) {
      return Collections.singletonList(Struct.getDefaultInstance());
    } else {
      return this.structList;
    }
  }

  public Map<String, Type> getTypes() {
    return this.typesMap;
  }
}
