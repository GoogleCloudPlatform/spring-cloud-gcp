package com.google.cloud.spring.autoconfigure.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.mock.env.MockEnvironment;

class TraceAutoConfigurationFilterTests {

  private TraceAutoConfigurationFilter filter;

  @BeforeEach
  void init() {
    filter = new TraceAutoConfigurationFilter();
  }

  @Test
  void testNullString() {
    assertThat(filter.match(new String[1], mock(AutoConfigurationMetadata.class)))
        .hasSize(1)
        .contains(true);
  }

  @Test
  void testZipkinAutoConfigurationShouldNotMatch() {
    assertThat(
            filter.match(
                new String[] {
                  "org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration"
                },
                mock(AutoConfigurationMetadata.class)))
        .hasSize(1)
        .contains(false);
  }

  @Test
  void testZipkinAutoConfigurationShouldMatchWhenGcpTracingIsDisabled() {
    MockEnvironment environment = new MockEnvironment();
    environment.setProperty("spring.cloud.gcp.trace.disable-spring-boot-autoconfig", "false");
    filter.setEnvironment(environment);

    assertThat(
            filter.match(
                new String[] {
                  "org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration"
                },
                mock(AutoConfigurationMetadata.class)))
        .hasSize(1)
        .contains(true);
  }
}
