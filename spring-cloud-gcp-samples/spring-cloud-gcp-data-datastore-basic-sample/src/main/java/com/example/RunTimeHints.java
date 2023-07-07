package com.example;

import com.google.cloud.spring.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class RunTimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

    hints.reflection().registerType(EnableDatastoreRepositories.class, MemberCategory.DECLARED_CLASSES);
  }
}
