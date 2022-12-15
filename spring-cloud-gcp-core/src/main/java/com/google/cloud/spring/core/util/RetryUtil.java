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

import com.google.api.core.InternalApi;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.spring.core.Retry;

/** Utility methods for retry settings. */
@InternalApi
public class RetryUtil {

  private RetryUtil() {}

  /**
   * Updates a {@link RetrySettings} based on configuration properties.
   *
   * @param oldRetrySettings the existing {@link RetrySettings} object to update
   * @param newRetry the {@link Retry} object containing configured property override values
   * @return the updated {@link RetrySettings} object
   */
  public static RetrySettings updateRetrySettings(
      RetrySettings oldRetrySettings, Retry newRetry) {
    RetrySettings.Builder builder = oldRetrySettings.toBuilder();
    if (newRetry.getTotalTimeout() != null) {
      builder.setTotalTimeout(newRetry.getTotalTimeout());
    }
    if (newRetry.getInitialRetryDelay() != null) {
      builder.setInitialRetryDelay(newRetry.getInitialRetryDelay());
    }
    if (newRetry.getRetryDelayMultiplier() != null) {
      builder.setRetryDelayMultiplier(newRetry.getRetryDelayMultiplier());
    }
    if (newRetry.getMaxRetryDelay() != null) {
      builder.setMaxRetryDelay(newRetry.getMaxRetryDelay());
    }
    if (newRetry.getMaxAttempts() != null) {
      builder.setMaxAttempts(newRetry.getMaxAttempts());
    }
    if (newRetry.getInitialRpcTimeout() != null) {
      builder.setInitialRpcTimeout(newRetry.getInitialRpcTimeout());
    }
    if (newRetry.getRpcTimeoutMultiplier() != null) {
      builder.setRpcTimeoutMultiplier(newRetry.getRpcTimeoutMultiplier());
    }
    if (newRetry.getMaxRpcTimeout() != null) {
      builder.setMaxRpcTimeout(newRetry.getMaxRpcTimeout());
    }
    return builder.build();
  }
}
