package com.google.cloud.spring.autoconfigure.trace;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class TraceAutoConfigurationFilterTests {

  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner = new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                TraceAutoConfigurationFilter.class));
  }

  @Test
  void test() {
    this.contextRunner
        .run(
            context ->
                assertThatThrownBy(() -> context.getBean(ZipkinAutoConfiguration.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class)
                    .hasMessageContaining("ZipkinAutoConfiguration"));
  }
}
