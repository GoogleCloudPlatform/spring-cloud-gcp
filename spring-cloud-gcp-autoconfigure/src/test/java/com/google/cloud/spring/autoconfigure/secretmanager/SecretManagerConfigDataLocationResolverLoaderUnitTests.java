package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.properties.bind.Binder;

/** Unit tests for {@link SecretManagerConfigDataLocationResolverLoader}. */
public class SecretManagerConfigDataLocationResolverLoaderUnitTests {
  private ConfigDataLocationResolverContext resolverContext;
  private SecretManagerConfigDataLocationResolverLoader resolverLoader;

  @BeforeEach
  void setup() {
    resolverContext = mock(ConfigDataLocationResolverContext.class);

    // Setup mocks
    Binder mockBinder = mock(Binder.class);
    when(resolverContext.getBinder()).thenReturn(mockBinder);
    resolverLoader = new SecretManagerConfigDataLocationResolverLoader();
  }

  @ParameterizedTest
  @CsvSource({"sm@test-location, true", "invalid-location, false"})
  void isResolvableTest(String locationString, boolean expected) {
    ConfigDataLocation location = ConfigDataLocation.of(locationString);
    boolean result = resolverLoader.isResolvable(resolverContext, location);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testIsClassPresent_classExists() throws Exception {
    Method isClassPresentMethod =
        SecretManagerConfigDataLocationResolverLoader.class.getDeclaredMethod(
            "isClassPresent", String.class);
    isClassPresentMethod.setAccessible(true);

    boolean result = (boolean) isClassPresentMethod.invoke(null, "java.lang.String");
    assertThat(result).isTrue();
  }

  @Test
  void testIsClassPresent_classDoesNotExist() throws Exception {
    Method isClassPresentMethod =
        SecretManagerConfigDataLocationResolverLoader.class.getDeclaredMethod(
            "isClassPresent", String.class);
    isClassPresentMethod.setAccessible(true);

    boolean result = (boolean) isClassPresentMethod.invoke(null, "non.existent.ClassName");
    assertThat(result).isFalse();
  }

  @Test
  void testIsResolvable_delegateNotNullAndResolvable() {
    // Assuming SecretManagerSyntaxUtils is present for this test
    if (isClassPresent("com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils")) {
      ConfigDataLocation testLocation = ConfigDataLocation.of("sm@test-location");
      boolean result = resolverLoader.isResolvable(resolverContext, testLocation);
      assertThat(result).isTrue();
    }
  }

  @Test
  void testIsResolvable_delegateNull() {
    // Assuming SecretManagerSyntaxUtils is not present for this test
    if (!isClassPresent("com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils")) {
      ConfigDataLocation testLocation = ConfigDataLocation.of("sm@test-location");
      boolean result = resolverLoader.isResolvable(resolverContext, testLocation);
      assertThat(result).isFalse();
    }
  }

  // Helper method to check class presence using reflection
  private boolean isClassPresent(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
