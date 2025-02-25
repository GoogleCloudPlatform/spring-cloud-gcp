/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.secretmanager;

import static com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils.getMatchedPrefixes;
import static com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils.warnIfUsingDeprecatedSyntax;

import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/** Utilities for parsing Secret Manager properties. */
final class SecretManagerPropertyUtils {

  private static final Logger logger = LoggerFactory.getLogger(SecretManagerPropertyUtils.class);

  private SecretManagerPropertyUtils() {}

  static SecretVersionName getSecretVersionName(
      final String input, GcpProjectIdProvider projectIdProvider, String location) {
    Optional<String> usedPrefix = getMatchedPrefixes(input::startsWith);

    // Since spring-core 6.2.2, the property resolution mechanism will try a full match that
    // may include a default string if provided. For example, a @Value("${sm@secret:default}") will
    // cause two attempts: one with sm@secret:default as a whole string (we don't want this),
    // and one with sm@secret (that's the one we want to process). The colon is also an invalid
    // character in secret IDs.
    // See https://github.com/spring-projects/spring-framework/issues/34124.
    final boolean isAttemptingFullStringMatch = input.replace("sm://", "").contains(":");
    if (usedPrefix.isEmpty() || isAttemptingFullStringMatch) {
      return null;
    }
    warnIfUsingDeprecatedSyntax(logger, usedPrefix.orElse(""));

    String resourcePath = input.substring(usedPrefix.get().length());
    String[] tokens = resourcePath.split("/");

    String projectId = projectIdProvider.getProjectId();
    String secretId = null;
    String version = "latest";

    if (tokens.length == 1) {
      // property is of the form "sm@<secret-id>"
      secretId = tokens[0];
    } else if (tokens.length == 2) {
      // property is of the form "sm@<secret-id>/<version>"
      secretId = tokens[0];
      version = tokens[1];
    } else if (tokens.length == 3) {
      // property is of the form "sm@<project-id>/<secret-id>/<version-id>"
      projectId = tokens[0];
      secretId = tokens[1];
      version = tokens[2];
    } else if (tokens.length == 4 && tokens[0].equals("projects") && tokens[2].equals("secrets")) {
      // property is of the form "sm@projects/<project-id>/secrets/<secret-id>"
      projectId = tokens[1];
      secretId = tokens[3];
    } else if (tokens.length == 6
        && tokens[0].equals("projects")
        && tokens[2].equals("secrets")
        && tokens[4].equals("versions")) {
      // property is of the form "sm@projects/<project-id>/secrets/<secret-id>/versions/<version>"
      projectId = tokens[1];
      secretId = tokens[3];
      version = tokens[5];
    } else {
      throw new IllegalArgumentException(
          "Unrecognized format for specifying a GCP Secret Manager secret: " + input);
    }

    Assert.hasText(secretId, "The GCP Secret Manager secret id must not be empty: " + input);

    Assert.hasText(projectId, "The GCP Secret Manager project id must not be empty: " + input);

    Assert.hasText(version, "The GCP Secret Manager secret version must not be empty: " + input);

    return getSecretVersionName(projectId, secretId, version, location);
  }

  static SecretVersionName getSecretVersionName(
      String projectId, String secretId, String version, String location) {
    if (ObjectUtils.isEmpty(location)) {
      return SecretVersionName.newBuilder()
          .setProject(projectId)
          .setSecret(secretId)
          .setSecretVersion(version)
          .build();
    } else {
      return SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
          .setLocation(location)
          .setProject(projectId)
          .setSecret(secretId)
          .setSecretVersion(version)
          .build();
    }
  }
}
