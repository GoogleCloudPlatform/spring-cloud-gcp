package com.google.cloud.spring.data.spanner.aot;

import java.util.Arrays;
import java.util.List;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class SpannerTestRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerTypes(
                    Arrays.asList(
                            TypeReference.of(com.google.cloud.spring.data.spanner.repository.support.SimpleSpannerRepository.class),
                            TypeReference.of(com.google.cloud.spring.data.spanner.test.domain.TradeRepository.class),
//                            TypeReference.of(org.springframework.data.repository.CrudRepository.class),
                            TypeReference.of(com.google.cloud.spring.data.spanner.test.domain.Trade.class),
                            TypeReference.of(java.lang.String.class)),
                    hint ->
                         hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                 MemberCategory.INVOKE_DECLARED_METHODS,
                                 MemberCategory.INVOKE_PUBLIC_METHODS));

  }
}
