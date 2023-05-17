/*
 * Copyright 2022-2023 Google LLC
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

package com.google.cloud.spring.vision.nativeimage;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TestRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    Resource dummyPdf = new ClassPathResource("documents/single-page-dummy.pdf");
    Resource multiPagePdf = new ClassPathResource("documents/multi-page-dummy.pdf");
    hints.resources().registerResource(dummyPdf);
    hints.resources().registerResource(multiPagePdf);
  }
}
