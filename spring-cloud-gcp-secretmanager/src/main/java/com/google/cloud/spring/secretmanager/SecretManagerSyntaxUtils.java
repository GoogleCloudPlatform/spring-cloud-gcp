/*
 * Copyright 2025 the original author or authors.
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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

public class SecretManagerSyntaxUtils {
  private static final String DEPRECATED_PREFIX = "sm://";

  private static final String PREFERRED_PREFIX = "sm@";

  /**
   * Prefixes for Google Cloud Secret Manager resources.
   */
  public static final List<String> PREFIXES = ImmutableList.of(PREFERRED_PREFIX, DEPRECATED_PREFIX);

  public static Optional<String> getMatchedPrefixes(PrefixMatcher matcher) {
    return PREFIXES.stream().filter(matcher::matches).findFirst();
  }

  public static void warnIfUsingDeprecatedSyntax(Logger logger, String value) {
    if (!logger.isWarnEnabled() || !value.startsWith(DEPRECATED_PREFIX)) {
      return;
    }
    logger.warn(String.format("Detected usage of deprecated prefix %s. This may be removed in a "
            + "future version of Spring Cloud GCP. Please use the new prefix %s instead.",
        DEPRECATED_PREFIX, PREFERRED_PREFIX));
  }

  public interface PrefixMatcher {
    boolean matches(String input);
  }

}
