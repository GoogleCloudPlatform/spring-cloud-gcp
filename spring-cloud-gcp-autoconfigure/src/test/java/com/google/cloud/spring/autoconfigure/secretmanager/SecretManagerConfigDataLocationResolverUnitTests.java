package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Unit tests for {@link SecretManagerConfigDataLocationResolver}.
 */
public class SecretManagerConfigDataLocationResolverUnitTests {

  private final SecretManagerConfigDataLocationResolver resolver = new SecretManagerConfigDataLocationResolver();
  private final ConfigDataLocationResolverContext context = mock(
      ConfigDataLocationResolverContext.class);

  @Test
  public void isResolvableReturnsFalseWithIncorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("test://"))).isFalse();
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm:"))).isFalse();
  }

  @Test
  public void isResolvableReturnsFalseWithCorrectPrefix() {
    assertThat(resolver.isResolvable(context, ConfigDataLocation.of("sm://"))).isTrue();
  }

  @Test
  public void resolveReturnsConfigDataLocation() {
    SpringApplicationBuilder applicationBuilder =
        new SpringApplicationBuilder(
            TestSecretManagerConfiguration.class)
            .properties("spring.cloud.gcp.sql.enabled=false")
            .web(WebApplicationType.NONE);
    try (ConfigurableApplicationContext c = applicationBuilder.run()) {
      when(context.getBinder()).thenReturn(new Binder());
      when(context.getBootstrapContext()).thenReturn(new DefaultBootstrapContext());
      List<SecretManagerConfigDataResource> locations = resolver.resolve(context,
          ConfigDataLocation.of("sm://my-secret"));
      assertThat(locations.size()).isEqualTo(1);
      assertThat(locations).first().extracting("location")
          .isEqualTo(ConfigDataLocation.of("sm://my-secret"));
    }
  }

  @Configuration
  static class TestSecretManagerConfiguration {

    @Bean
    public static CredentialsProvider googleCredentials() {
      return () -> mock(Credentials.class);
    }
  }
}
