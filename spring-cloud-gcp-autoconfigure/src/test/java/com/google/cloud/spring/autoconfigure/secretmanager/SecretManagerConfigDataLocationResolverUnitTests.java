package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.properties.bind.Binder;

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
    when(context.getBinder()).thenReturn(new Binder());
    when(context.getBootstrapContext()).thenReturn(new DefaultBootstrapContext());
    List<SecretManagerConfigDataResource> locations = resolver.resolve(context,
        ConfigDataLocation.of("sm://my-secret"));
    assertThat(locations.size()).isEqualTo(1);
    assertThat(locations).first().extracting("location")
        .isEqualTo(ConfigDataLocation.of("sm://my-secret"));
  }
}
