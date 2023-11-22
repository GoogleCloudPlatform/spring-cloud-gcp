package com.google.cloud.spring.autoconfigure.sql;

import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import org.springframework.aot.hint.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;

public class RuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(
      org.springframework.aot.hint.RuntimeHints hints, ClassLoader classLoader) {
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(
                TypeReference.of(ConfigurationProperties.class),
                TypeReference.of(GcpCloudSqlProperties.class),
                TypeReference.of(GcpProperties.class)),
            hint -> {
              TypeHint.Builder builder =
                  hint.withMethod(
                      "getAnnotation",
                      Arrays.asList(TypeReference.of(Class.class)),
                      ExecutableMode.INVOKE);
              builder.withMembers(
                  MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                  MemberCategory.INVOKE_DECLARED_METHODS,
                  MemberCategory.DECLARED_FIELDS);
            });
  }
}
