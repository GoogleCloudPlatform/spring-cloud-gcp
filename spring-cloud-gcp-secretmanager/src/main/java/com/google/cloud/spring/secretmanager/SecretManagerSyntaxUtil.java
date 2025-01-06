package com.google.cloud.spring.secretmanager;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

public class SecretManagerSyntaxUtil {
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
