package com.google.cloud.spring.secretmanager.aot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.aot.hint.predicate.RuntimeHintsPredicates.reflection;

import com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

public class SecretManagerRuntimeHintsTest {
  @Test
  void shouldRegisterHints() {
    RuntimeHints hints = new RuntimeHints();
    new SecretManagerRuntimeHints().registerHints(hints, getClass().getClassLoader());

    assertThat(hints)
        .matches(reflection().onType(SecretManagerSyntaxUtils.class));
  }
}

