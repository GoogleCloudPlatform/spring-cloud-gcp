package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLocation;

class SecretManagerConfigDataResourceUnitTests {

  private ConfigDataLocation location = ConfigDataLocation.of("test");
  private final SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(
      location);

  @Test
  void resourceShouldBeEqualToItself() {
    assertThat(resource.equals(resource)).isTrue();
  }

  @Test
  void resourceShouldNotBeEqualToOtherObject() {
    assertThat(new SecretManagerConfigDataResource(location).equals(location)).isFalse();
  }

  @Test
  void twoResourcesWithSameLocationShouldBeEqual() {
    assertThat(resource.equals(new SecretManagerConfigDataResource(location))).isTrue();
  }

  @Test
  void twoResourcesWithSameLocationShouldHaveSameHashcode() {
    assertThat(resource).hasSameHashCodeAs(
        new SecretManagerConfigDataResource(location));
  }

  @Test
  void toStringTest() {

    String expectedString = "SecretManagerConfigDataResource{"
        + "location="
        + location
        + "}";
    assertThat(resource)
        .hasToString(expectedString);
  }
}
