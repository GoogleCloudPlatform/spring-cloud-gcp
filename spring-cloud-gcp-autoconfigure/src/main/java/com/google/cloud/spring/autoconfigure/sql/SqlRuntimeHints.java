package com.google.cloud.spring.autoconfigure.sql;

import java.util.Arrays;
import org.springframework.aot.hint.*;

public class SqlRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(
      org.springframework.aot.hint.RuntimeHints hints, ClassLoader classLoader) {
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(TypeReference.of(GcpCloudSqlProperties.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS));
  }
}
