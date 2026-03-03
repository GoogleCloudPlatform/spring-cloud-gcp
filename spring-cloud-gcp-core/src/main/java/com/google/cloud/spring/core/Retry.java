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

package com.google.cloud.spring.core;

import com.google.api.core.InternalApi;
import org.threeten.bp.Duration;

/** Retry settings configuration. */
@InternalApi
public final class Retry {
  /**
   * TotalTimeout has ultimate control over how long the logic should keep
   * trying the remote call until it gives up completely. The higher the
   * total timeout, the more retries can be attempted.
   */
  private Duration totalTimeout;

  /**
   * InitialRetryDelay controls the delay before the first retry. Subsequent
   * retries will use this value adjusted according to the
   * RetryDelayMultiplier.
   */
  private Duration initialRetryDelay;

  /**
   * RetryDelayMultiplier controls the change in retry delay. The retry delay
   * of the previous call is multiplied by the RetryDelayMultiplier to
   * calculate the retry delay for the next call.
   */
  private Double retryDelayMultiplier;

  /**
   * MaxRetryDelay puts a limit on the value of the retry delay, so that the
   * RetryDelayMultiplier can't increase the retry delay higher than this
   * amount.
   */
  private Duration maxRetryDelay;

  /**
   * MaxAttempts defines the maximum number of attempts to perform. If this
   * value is greater than 0, and the number of attempts reaches this limit,
   * the logic will give up retrying even if the total retry time is still
   * lower than TotalTimeout.
   */
  private Integer maxAttempts;

  /**
   * InitialRpcTimeout controls the timeout for the initial RPC. Subsequent
   * calls will use this value adjusted according to the
   * RpcTimeoutMultiplier.
   */
  private Duration initialRpcTimeout;

  /**
   * RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of
   * the previous call is multiplied by the RpcTimeoutMultiplier to
   * calculate the timeout for the next call.
   */
  private Double rpcTimeoutMultiplier;

  /**
   * MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the
   * RpcTimeoutMultiplier can't increase the RPC timeout higher than this
   * amount.
   */
  private Duration maxRpcTimeout;

  /**
   * Returns the total timeout.
   *
   * @return the total timeout
   */
  public Duration getTotalTimeout() {
    return totalTimeout;
  }

  /**
   * Sets the total timeout.
   *
   * @param totalTimeoutValue the total timeout
   */
  public void setTotalTimeout(final java.time.Duration totalTimeoutValue) {
    this.totalTimeout = Duration.parse(totalTimeoutValue.toString());
  }

  /**
   * Returns the initial retry delay.
   *
   * @return the initial retry delay
   */
  public Duration getInitialRetryDelay() {
    return initialRetryDelay;
  }

  /**
   * Sets the initial retry delay.
   *
   * @param initialRetryDelayValue the initial retry delay
   */
  public void setInitialRetryDelay(
      final java.time.Duration initialRetryDelayValue) {
    this.initialRetryDelay = Duration.parse(initialRetryDelayValue.toString());
  }

  /**
   * Returns the retry delay multiplier.
   *
   * @return the retry delay multiplier
   */
  public Double getRetryDelayMultiplier() {
    return retryDelayMultiplier;
  }

  /**
   * Sets the retry delay multiplier.
   *
   * @param retryDelayMultiplierValue the retry delay multiplier
   */
  public void setRetryDelayMultiplier(final Double retryDelayMultiplierValue) {
    this.retryDelayMultiplier = retryDelayMultiplierValue;
  }

  /**
   * Returns the maximum retry delay.
   *
   * @return the maximum retry delay
   */
  public Duration getMaxRetryDelay() {
    return maxRetryDelay;
  }

  /**
   * Sets the maximum retry delay.
   *
   * @param maxRetryDelayValue the maximum retry delay
   */
  public void setMaxRetryDelay(final java.time.Duration maxRetryDelayValue) {
    this.maxRetryDelay = Duration.parse(maxRetryDelayValue.toString());
  }

  /**
   * Returns the maximum number of attempts.
   *
   * @return the maximum number of attempts
   */
  public Integer getMaxAttempts() {
    return maxAttempts;
  }

  /**
   * Sets the maximum number of attempts.
   *
   * @param maxAttemptsValue the maximum number of attempts
   */
  public void setMaxAttempts(final Integer maxAttemptsValue) {
    this.maxAttempts = maxAttemptsValue;
  }

  /**
   * Returns the initial RPC timeout.
   *
   * @return the initial RPC timeout
   */
  public Duration getInitialRpcTimeout() {
    return initialRpcTimeout;
  }

  /**
   * Sets the initial RPC timeout.
   *
   * @param initialRpcTimeoutValue the initial RPC timeout
   */
  public void setInitialRpcTimeout(
      final java.time.Duration initialRpcTimeoutValue) {
    this.initialRpcTimeout = Duration.parse(initialRpcTimeoutValue.toString());
  }

  /**
   * Returns the RPC timeout multiplier.
   *
   * @return the RPC timeout multiplier
   */
  public Double getRpcTimeoutMultiplier() {
    return rpcTimeoutMultiplier;
  }

  /**
   * Sets the RPC timeout multiplier.
   *
   * @param rpcTimeoutMultiplierValue the RPC timeout multiplier
   */
  public void setRpcTimeoutMultiplier(final Double rpcTimeoutMultiplierValue) {
    this.rpcTimeoutMultiplier = rpcTimeoutMultiplierValue;
  }

  /**
   * Returns the maximum RPC timeout.
   *
   * @return the maximum RPC timeout
   */
  public Duration getMaxRpcTimeout() {
    return maxRpcTimeout;
  }

  /**
   * Sets the maximum RPC timeout.
   *
   * @param maxRpcTimeoutValue the maximum RPC timeout
   */
  public void setMaxRpcTimeout(final java.time.Duration maxRpcTimeoutValue) {
    this.maxRpcTimeout = Duration.parse(maxRpcTimeoutValue.toString());
  }
}
