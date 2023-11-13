package com.google.cloud.spring.bigquery;

import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.test.context.BootstrapUtils;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.support.DefaultBootstrapContext;
import org.springframework.test.context.web.WebAppConfiguration;

public class TestRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints
        .reflection()
        .registerTypes(
            Arrays.asList(TypeReference.of(WebAppConfiguration.class),
                TypeReference.of(DefaultCacheAwareContextLoaderDelegate.class),
                TypeReference.of(DefaultBootstrapContext.class),
                TypeReference.of(BootstrapUtils.class)),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS));
  }
}
