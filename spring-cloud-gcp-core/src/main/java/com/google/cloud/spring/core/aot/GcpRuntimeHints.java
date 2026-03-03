/*
 * Copyright 2026 Google LLC
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

package com.google.cloud.spring.core.aot;

import java.util.stream.Stream;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

/**
 * Runtime hints for Spring Cloud GCP.
 */
public final class GcpRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(
      final RuntimeHints hints, final ClassLoader classLoader) {
    Stream.of(
        "org.springframework.data.domain.AbstractPageRequest",
        "org.springframework.data.domain.PageRequest",
        "org.springframework.data.domain.PageImpl",
        "org.springframework.data.domain.Page",
        "org.springframework.data.domain.Slice",
        "org.springframework.data.domain.Pageable",
        "org.springframework.data.domain.Sort",
        "org.springframework.data.domain.Sort$Order",
        "org.springframework.data.domain.Chunk"
    ).forEach(className -> {
      if (ClassUtils.isPresent(className, classLoader)) {
        hints.reflection().registerType(
            TypeReference.of(className),
            hint -> hint.withMembers(
                MemberCategory.INVOKE_PUBLIC_METHODS));
      }
    });
  }
}
