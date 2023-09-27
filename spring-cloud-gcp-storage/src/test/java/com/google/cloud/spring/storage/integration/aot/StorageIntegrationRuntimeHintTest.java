package com.google.cloud.spring.storage.integration.aot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.aot.hint.predicate.RuntimeHintsPredicates.reflection;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;


class StorageIntegrationRuntimeHintTest {
  @Test
  void shouldRegisterHints() {
    RuntimeHints hints = new RuntimeHints();
    new StorageIntegrationRuntimeHint().registerHints(hints, getClass().getClassLoader());

    assertThat(hints)
        .matches(reflection().onType(TypeReference.of("com.google.cloud.storage.Blob[]")));
  }
}
