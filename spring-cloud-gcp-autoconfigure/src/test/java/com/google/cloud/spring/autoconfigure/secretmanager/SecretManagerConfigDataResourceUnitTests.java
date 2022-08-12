package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLocation;

public class SecretManagerConfigDataResourceUnitTests {

  private ConfigDataLocation location = ConfigDataLocation.of("test");

  @Test
  void resourceShouldBeEqualToItself() {
    SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(location);
    assertThat(resource.equals(resource)).isTrue();
  }

  @Test
  void resourceShouldNotBeEqualToOtherObject() {
    assertThat(new SecretManagerConfigDataResource(location).equals(location)).isFalse();
  }

  @Test
  void twoResourcesWithSameLocationShouldBeEqual() {
    SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(location);
    assertThat(resource.equals(new SecretManagerConfigDataResource(location))).isTrue();
  }

  @Test
  void toStringTest() {

    String expectedString = "SecretManagerConfigDataResource{"
        + "location="
        + location
        + "}";
    assertThat(new SecretManagerConfigDataResource(location)
        .toString())
        .isEqualTo(expectedString);
  }
}
