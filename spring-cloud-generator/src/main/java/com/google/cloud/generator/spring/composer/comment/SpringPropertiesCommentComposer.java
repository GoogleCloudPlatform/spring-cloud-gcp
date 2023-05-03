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

package com.google.cloud.generator.spring.composer.comment;

import com.google.api.generator.engine.ast.CommentStatement;
import com.google.api.generator.engine.ast.JavaDocComment;
import com.google.api.generator.gapic.composer.comment.CommentComposer;
import java.util.Arrays;
import java.util.List;

public class SpringPropertiesCommentComposer {
  private static final String CLASS_HEADER_GENERAL_DESCRIPTION =
      "Provides default property values for %s client bean";
  private static final String CREDENTIALS_DESCRIPTION =
      "OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries.";
  private static final String QUOTA_PROJECT_ID_DESCRIPTION = "Quota project to use for billing.";
  private static final String EXECUTOR_THREAD_COUNT_DESCRIPTION =
      "Number of threads used for executors.";
  private static final String USE_REST_DESCRIPTION =
      "Allow override of default transport channel provider to use REST instead of gRPC.";
  private static final String SERVICE_RETRY_SETTINGS_DESCRIPTION =
      "Allow override of retry settings at service level, applying to all of its RPC methods.";
  private static final String METHOD_RETRY_SETTINGS_DESCRIPTION =
      "Allow override of retry settings at method-level for %s. "
          + "If defined, this takes precedence over service-level retry configurations for that RPC method.";

  public static List<CommentStatement> createClassHeaderComments(
      String configuredClassName, String serviceName) {

    JavaDocComment.Builder javaDocCommentBuilder =
        JavaDocComment.builder()
            .addParagraph(String.format(CLASS_HEADER_GENERAL_DESCRIPTION, serviceName));
    return Arrays.asList(
        CommentComposer.AUTO_GENERATED_CLASS_COMMENT,
        CommentStatement.withComment(javaDocCommentBuilder.build()));
  }

  public static CommentStatement createCredentialsPropertyComment() {
    return toSimpleJavaDocComment(CREDENTIALS_DESCRIPTION);
  }

  public static CommentStatement createQuotaProjectIdPropertyComment() {
    return toSimpleJavaDocComment(QUOTA_PROJECT_ID_DESCRIPTION);
  }

  public static CommentStatement createExecutorThreadCountPropertyComment() {
    return toSimpleJavaDocComment(EXECUTOR_THREAD_COUNT_DESCRIPTION);
  }

  public static CommentStatement createUseRestPropertyComment() {
    return toSimpleJavaDocComment(USE_REST_DESCRIPTION);
  }

  public static CommentStatement createServiceRetryPropertyComment() {
    return toSimpleJavaDocComment(SERVICE_RETRY_SETTINGS_DESCRIPTION);
  }

  public static CommentStatement createMethodRetryPropertyComment(String methodName) {
    String comment = String.format(METHOD_RETRY_SETTINGS_DESCRIPTION, methodName);
    return toSimpleJavaDocComment(comment);
  }

  private static CommentStatement toSimpleJavaDocComment(String comment) {
    return CommentStatement.withComment(JavaDocComment.withComment(comment));
  }
}
