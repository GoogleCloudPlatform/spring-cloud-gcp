/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.spring.core.Retry;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Tests for {@link RetryUtil}. */
class RetryUtilTests {

  private static final Duration TEST_DURATION = Duration.ofSeconds(5);
  private static final org.threeten.bp.Duration TEST_DURATION_BP =
      org.threeten.bp.Duration.ofSeconds(5);
  private static final double TEST_MULTIPLIER_FIVE = 5.0;
  private static final double TEST_MULTIPLIER_THREE = 3.0;
  private static final double TEST_MULTIPLIER_ONE = 1.0;
  private static final int TEST_MAX_ATTEMPTS = 2;

  @Test
  void testAllSettingsUpdatedAsExpected() {
    RetrySettings oldRetrySettings = RetrySettings.newBuilder().build();
    Retry newRetry = new Retry();
    newRetry.setInitialRetryDelay(TEST_DURATION);
    newRetry.setMaxRetryDelay(TEST_DURATION);
    newRetry.setRetryDelayMultiplier(TEST_MULTIPLIER_FIVE);
    newRetry.setInitialRpcTimeout(TEST_DURATION);
    newRetry.setMaxRpcTimeout(TEST_DURATION);
    newRetry.setRpcTimeoutMultiplier(TEST_MULTIPLIER_FIVE);
    newRetry.setTotalTimeout(TEST_DURATION);
    newRetry.setMaxAttempts(TEST_MAX_ATTEMPTS);

    RetrySettings updated = RetryUtil.updateRetrySettings(oldRetrySettings, newRetry);

    // Old RetrySettings should differ from updated object returned
    assertThat(updated).isNotEqualTo(oldRetrySettings);
    assertThat(updated.getInitialRetryDelay()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getMaxRetryDelay()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getRetryDelayMultiplier()).isEqualTo(TEST_MULTIPLIER_FIVE);
    assertThat(updated.getInitialRpcTimeout()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getMaxRpcTimeout()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getRpcTimeoutMultiplier()).isEqualTo(TEST_MULTIPLIER_FIVE);
    assertThat(updated.getTotalTimeout()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getMaxAttempts()).isEqualTo(TEST_MAX_ATTEMPTS);
  }

  @Test
  void testNotConfiguredSettingsNotUpdated() {
    RetrySettings oldRetrySettings =
        RetrySettings.newBuilder()
            .setRetryDelayMultiplier(TEST_MULTIPLIER_THREE)
            .setRpcTimeoutMultiplier(TEST_MULTIPLIER_THREE)
            .build();
    Retry newRetry = new Retry();
    newRetry.setInitialRetryDelay(TEST_DURATION);
    newRetry.setMaxRetryDelay(TEST_DURATION);
    newRetry.setRetryDelayMultiplier(TEST_MULTIPLIER_ONE);

    RetrySettings updated = RetryUtil.updateRetrySettings(oldRetrySettings, newRetry);

    // Old RetrySettings should differ from updated object returned
    assertThat(updated).isNotEqualTo(oldRetrySettings);

    // Set fields should take on new values
    assertThat(updated.getInitialRetryDelay()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getMaxRetryDelay()).isEqualTo(TEST_DURATION_BP);
    assertThat(updated.getRetryDelayMultiplier()).isEqualTo(TEST_MULTIPLIER_ONE);

    // Unset fields should take on values from old RetrySettings object
    assertThat(updated.getInitialRpcTimeout()).isEqualTo(oldRetrySettings.getInitialRpcTimeout());
    assertThat(updated.getMaxRpcTimeout()).isEqualTo(oldRetrySettings.getMaxRpcTimeout());
    assertThat(updated.getRpcTimeoutMultiplier())
        .isEqualTo(oldRetrySettings.getRpcTimeoutMultiplier());
    assertThat(updated.getTotalTimeout()).isEqualTo(oldRetrySettings.getTotalTimeout());
    assertThat(updated.getMaxAttempts()).isEqualTo(oldRetrySettings.getMaxAttempts());
  }
}
