package com.example;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TestResourcesRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    Resource csvFile = new ClassPathResource("test.csv");
    Resource jsonFile = new ClassPathResource("test.json");
    hints.resources().registerResource(csvFile);
    hints.resources().registerResource(jsonFile);
  }
}
