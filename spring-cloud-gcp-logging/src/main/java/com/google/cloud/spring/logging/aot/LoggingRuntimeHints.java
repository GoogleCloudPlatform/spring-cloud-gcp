package com.google.cloud.spring.logging.aot;

import com.google.cloud.spring.logging.StackdriverJsonLayout;
import com.google.cloud.spring.logging.TraceIdLoggingEnhancer;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class LoggingRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.resources().registerPattern("com/google/cloud/spring/logging/.*.xml");

    hints
        .reflection()
        .registerTypes(
            Arrays.asList(
                TypeReference.of(TraceIdLoggingEnhancer.class),
                TypeReference.of(StackdriverJsonLayout.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS));
  }
}
