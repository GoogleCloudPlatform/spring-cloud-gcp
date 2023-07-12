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
import com.google.api.generator.gapic.composer.utils.ClassNames;
import com.google.api.generator.gapic.model.Service;
import java.util.Arrays;
import java.util.List;

public class SpringAutoconfigCommentComposer {

  private static final String CLASS_HEADER_SUMMARY_PATTERN = "Auto-configuration for {@link %s}.";
  private static final String CLASS_HEADER_GENERAL_DESCRIPTION =
      "Provides auto-configuration for Spring Boot";
  private static final String CLASS_HEADER_DEFAULTS_DESCRIPTION =
      "The default instance has everything set to sensible defaults:";
  private static final String CLASS_HEADER_DEFAULTS_CREDENTIALS_DESCRIPTION =
      "Credentials are acquired automatically through Application Default Credentials.";
  private static final String CLASS_HEADER_DEFAULTS_TRANSPORT_DESCRIPTION =
      "The default transport provider is used.";
  private static final String CLASS_HEADER_DEFAULTS_RETRIES_DESCRIPTION =
      "Retries are configured for idempotent methods but not for non-idempotent methods.";

  public static final String CREDENTIALS_PROVIDER_GENERAL_DESCRIPTION =
      "Obtains the default credentials provider. The used key will be obtained from Spring Boot "
          + "configuration data files.";

  public static final String TRANSPORT_CHANNEL_PROVIDER_GENERAL_DESCRIPTION =
      "Provides a default transport channel provider bean, corresponding to the client library's default "
          + "transport channel provider. If the library supports both GRPC and REST transport, and "
          + "the useRest property is configured, the HTTP/JSON transport provider will be used instead of GRPC.";
  public static final String TRANSPORT_CHANNEL_PROVIDER_RETURN =
      "a default transport channel provider.";
  public static final String CLIENT_SETTINGS_BEAN_GENERAL_DESCRIPTION =
      "Provides a %s bean configured to use a DefaultCredentialsProvider "
          + "and the client library's default transport channel provider (%s()). "
          + "It also configures the quota project ID and executor thread count, if provided through properties.";

  public static final String CLIENT_SETTINGS_BEAN_RETRY_SETTINGS_DESCRIPTION =
      "Retry settings are also configured from service-level and method-level properties specified in %s. "
          + "Method-level properties will take precedence over service-level properties if available, "
          + "and client library defaults will be used if neither are specified.";
  public static final String CLIENT_SETTINGS_BEAN_RETURN_STATEMENT =
      "a {@link %s} bean configured with {@link TransportChannelProvider} bean.";

  public static final String CLIENT_BEAN_GENERAL_DESCRIPTION =
      "Provides a %s bean configured with %s.";
  public static final String CLIENT_BEAN_RETURN_STATEMENT =
      "a {@link %s} bean configured with {@link %s}";

  public SpringAutoconfigCommentComposer() {}

  public static List<CommentStatement> createClassHeaderComments(Service service) {

    JavaDocComment.Builder javaDocCommentBuilder =
        JavaDocComment.builder()
            .addUnescapedComment(
                String.format(
                    CLASS_HEADER_SUMMARY_PATTERN, ClassNames.getServiceClientClassName(service)))
            .addParagraph(CLASS_HEADER_GENERAL_DESCRIPTION)
            .addParagraph(CLASS_HEADER_DEFAULTS_DESCRIPTION)
            .addUnorderedList(
                Arrays.asList(
                    CLASS_HEADER_DEFAULTS_TRANSPORT_DESCRIPTION,
                    CLASS_HEADER_DEFAULTS_CREDENTIALS_DESCRIPTION,
                    CLASS_HEADER_DEFAULTS_RETRIES_DESCRIPTION));

    return Arrays.asList(
        CommentComposer.AUTO_GENERATED_CLASS_COMMENT,
        CommentStatement.withComment(javaDocCommentBuilder.build()));
  }

  public static CommentStatement createCredentialsProviderBeanComment() {
    return CommentStatement.withComment(
        JavaDocComment.builder().addParagraph(CREDENTIALS_PROVIDER_GENERAL_DESCRIPTION).build());
  }

  public static CommentStatement createTransportChannelProviderComment() {
    return CommentStatement.withComment(
        JavaDocComment.builder()
            .addParagraph(TRANSPORT_CHANNEL_PROVIDER_GENERAL_DESCRIPTION)
            .setReturn(TRANSPORT_CHANNEL_PROVIDER_RETURN)
            .build());
  }

  public static CommentStatement createSettingsBeanComment(
      Service service, String propertiesClazzName, String channelProviderName) {
    return CommentStatement.withComment(
        JavaDocComment.builder()
            .addParagraph(
                String.format(
                    CLIENT_SETTINGS_BEAN_GENERAL_DESCRIPTION,
                    ClassNames.getServiceSettingsClassName(service),
                    channelProviderName))
            .addParagraph(
                String.format(CLIENT_SETTINGS_BEAN_RETRY_SETTINGS_DESCRIPTION, propertiesClazzName))
            .addParam(
                "defaultTransportChannelProvider",
                "TransportChannelProvider to use in the settings.")
            .setReturn(
                String.format(
                    CLIENT_SETTINGS_BEAN_RETURN_STATEMENT,
                    ClassNames.getServiceSettingsClassName(service)))
            .build());
  }

  public static CommentStatement createClientBeanComment(
      Service service, String serviceSettingsMethodName) {
    return CommentStatement.withComment(
        JavaDocComment.builder()
            .addParagraph(
                String.format(
                    CLIENT_BEAN_GENERAL_DESCRIPTION,
                    ClassNames.getServiceClientClassName(service),
                    ClassNames.getServiceSettingsClassName(service)))
            .addParam(
                serviceSettingsMethodName, "settings to configure an instance of client bean.")
            .setReturn(
                String.format(
                    CLIENT_BEAN_RETURN_STATEMENT,
                    ClassNames.getServiceClientClassName(service),
                    ClassNames.getServiceSettingsClassName(service)))
            .build());
  }
}
