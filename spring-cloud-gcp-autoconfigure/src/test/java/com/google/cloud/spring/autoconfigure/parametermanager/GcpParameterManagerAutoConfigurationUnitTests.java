package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.spring.autoconfigure.TestUtils;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class GcpParameterManagerAutoConfigurationUnitTests {
  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    GcpParameterManagerAutoConfiguration.class, GcpContextAutoConfiguration.class))
            .withPropertyValues("spring.cloud.gcp.project-id=globalProject")
            .withUserConfiguration(TestConfig.class);
  }

  @Test
  void testProjectIdWithParameterManagerProperties() {
    contextRunner
        .withPropertyValues("spring.cloud.gcp.parametermanager.project-id=parameterManagerProject")
        .run(
            ctx ->
                assertThat(ctx.getBean(ParameterManagerTemplate.class).getProjectId())
                    .isEqualTo("parameterManagerProject"));
  }

  @Test
  void testProjectIdWithGcpProperties() {
    contextRunner.run(
        ctx ->
            assertThat(ctx.getBean(ParameterManagerTemplate.class).getProjectId())
                .isEqualTo("globalProject"));
  }

  @Test
  void testParameterManagerClientExists() {
    contextRunner.run(ctx -> assertThat(ctx.getBean(ParameterManagerClient.class)).isNotNull());
  }

  @Test
  void testParameterManagerTemplateExists() {
    contextRunner.run(ctx -> assertThat(ctx.getBean(ParameterManagerTemplate.class)).isNotNull());
  }

  static class TestConfig {
    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> TestUtils.MOCK_CREDENTIALS;
    }
  }
}
