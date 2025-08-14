package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLocation;

/** Unit tests for {@link ParameterManagerConfigDataResource}. */
class ParameterManagerConfigDataResourceUnitTests {

  private final ConfigDataLocation location = ConfigDataLocation.of("test");
  private final ParameterManagerConfigDataResource resource =
      new ParameterManagerConfigDataResource(location);

  @Test
  void resourceShouldBeEqualToItself() {
    assertThat(resource.equals(resource)).isTrue();
  }

  @Test
  void resourceShouldNotBeEqualToOtherObject() {
    assertThat(new ParameterManagerConfigDataResource(location).equals(location)).isFalse();
  }

  @Test
  void twoResourcesWithSameLocationShouldBeEqual() {
    assertThat(resource.equals(new ParameterManagerConfigDataResource(location))).isTrue();
  }

  @Test
  void twoResourcesWithSameLocationShouldHaveSameHashcode() {
    assertThat(resource).hasSameHashCodeAs(new ParameterManagerConfigDataResource(location));
  }

  @Test
  void toStringTest() {
    String expectedString = "ParameterManagerConfigDataResource{" + "location=" + location + "}";
    assertThat(resource).hasToString(expectedString);
  }
}
