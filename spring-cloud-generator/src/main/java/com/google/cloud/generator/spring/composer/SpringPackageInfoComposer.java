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

package com.google.cloud.generator.spring.composer;

import com.google.api.generator.engine.ast.CommentStatement;
import com.google.api.generator.engine.ast.JavaDocComment;
import com.google.api.generator.engine.ast.PackageInfoDefinition;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicPackageInfo;
import com.google.cloud.generator.spring.utils.Utils;
import com.google.common.base.Preconditions;

public class SpringPackageInfoComposer {
  private static final String PACKAGE_INFO_TITLE_PATTERN =
      "Spring Boot auto-configurations for %s.";

  public static GapicPackageInfo generatePackageInfo(GapicContext context) {
    Preconditions.checkState(!context.services().isEmpty(), "No services found to generate");
    PackageInfoDefinition packageInfo =
        PackageInfoDefinition.builder()
            .setPakkage(Utils.getSpringPackageName(Utils.getPackageName(context)))
            .setHeaderCommentStatements(createPackageInfoJavadoc(context))
            .build();
    return GapicPackageInfo.with(packageInfo);
  }

  private static CommentStatement createPackageInfoJavadoc(GapicContext context) {
    JavaDocComment.Builder javaDocCommentBuilder = JavaDocComment.builder();
    javaDocCommentBuilder =
        javaDocCommentBuilder.addComment(
            String.format(PACKAGE_INFO_TITLE_PATTERN, Utils.getLibName(context)));
    return CommentStatement.withComment(javaDocCommentBuilder.build());
  }
}
