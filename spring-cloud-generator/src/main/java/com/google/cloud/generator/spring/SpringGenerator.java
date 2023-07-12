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

package com.google.cloud.generator.spring;

import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicPackageInfo;
import com.google.api.generator.gapic.protoparser.Parser;
import com.google.cloud.generator.spring.composer.SpringComposer;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import java.util.List;

public class SpringGenerator {
  public static CodeGeneratorResponse generateSpring(CodeGeneratorRequest request) {
    GapicContext context = Parser.parse(request);
    List<GapicClass> clazzes = SpringComposer.composeServiceAutoConfigClasses(context);
    GapicPackageInfo packageInfo = SpringComposer.composePackageInfo(context);
    String outputFilename = "temp-codegen-spring.srcjar";
    return SpringWriter.write(context, clazzes, packageInfo, outputFilename);
  }
}
