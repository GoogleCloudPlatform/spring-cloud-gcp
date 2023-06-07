/*
 * Copyright 2023 Google LLC
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

package com.example;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/** Runtime Hints for GraalVM Native Images * */
public class TestRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(
      org.springframework.aot.hint.RuntimeHints hints, ClassLoader classLoader) {
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(
                TypeReference.of(ConsoleAppender.class),
                TypeReference.of(PatternLayoutEncoder.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS));
    hints.resources().registerPattern("logback-test.xml");
    hints.resources().registerPattern("com/google/cloud/spring/logging/logback-appender.xml");
  }
}
