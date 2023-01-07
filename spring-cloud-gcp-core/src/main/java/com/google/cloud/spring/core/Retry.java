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
public class Retry {
  /**
   * TotalTimeout has ultimate control over how long the logic should keep trying the remote call
   * until it gives up completely. The higher the total timeout, the more retries can be attempted.
   */
  private Duration totalTimeout;
  /**
   * InitialRetryDelay controls the delay before the first retry. Subsequent retries will use this
   * value adjusted according to the RetryDelayMultiplier.
   */
  private Duration initialRetryDelay;
  /**
   * RetryDelayMultiplier controls the change in retry delay. The retry delay of the previous call
   * is multiplied by the RetryDelayMultiplier to calculate the retry delay for the next call.
   */
  private Double retryDelayMultiplier;
  /**
   * MaxRetryDelay puts a limit on the value of the retry delay, so that the RetryDelayMultiplier
   * can't increase the retry delay higher than this amount.
   */
  private Duration maxRetryDelay;
  /**
   * MaxAttempts defines the maximum number of attempts to perform. If this value is greater than 0,
   * and the number of attempts reaches this limit, the logic will give up retrying even if the
   * total retry time is still lower than TotalTimeout.
   */
  private Integer maxAttempts;
  /**
   * InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use this
   * value adjusted according to the RpcTimeoutMultiplier.
   */
  private Duration initialRpcTimeout;
  /**
   * RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous call is
   * multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next call.
   */
  private Double rpcTimeoutMultiplier;
  /**
   * MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the RpcTimeoutMultiplier
   * can't increase the RPC timeout higher than this amount.
   */
  private Duration maxRpcTimeout;

  public Duration getTotalTimeout() {
    return totalTimeout;
  }

  public void setTotalTimeout(java.time.Duration totalTimeout) {
    this.totalTimeout = Duration.parse(totalTimeout.toString());
  }

  public Duration getInitialRetryDelay() {
    return initialRetryDelay;
  }

  public void setInitialRetryDelay(java.time.Duration initialRetryDelay) {
    this.initialRetryDelay = Duration.parse(initialRetryDelay.toString());
  }

  public Double getRetryDelayMultiplier() {
    return retryDelayMultiplier;
  }

  public void setRetryDelayMultiplier(Double retryDelayMultiplier) {
    this.retryDelayMultiplier = retryDelayMultiplier;
  }

  public Duration getMaxRetryDelay() {
    return maxRetryDelay;
  }

  public void setMaxRetryDelay(java.time.Duration maxRetryDelay) {
    this.maxRetryDelay = Duration.parse(maxRetryDelay.toString());
  }

  public Integer getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public Duration getInitialRpcTimeout() {
    return initialRpcTimeout;
  }

  public void setInitialRpcTimeout(java.time.Duration initialRpcTimeout) {
    this.initialRpcTimeout = Duration.parse(initialRpcTimeout.toString());
  }

  public Double getRpcTimeoutMultiplier() {
    return rpcTimeoutMultiplier;
  }

  public void setRpcTimeoutMultiplier(Double rpcTimeoutMultiplier) {
    this.rpcTimeoutMultiplier = rpcTimeoutMultiplier;
  }

  public Duration getMaxRpcTimeout() {
    return maxRpcTimeout;
  }

  public void setMaxRpcTimeout(java.time.Duration maxRpcTimeout) {
    this.maxRpcTimeout = Duration.parse(maxRpcTimeout.toString());
  }
}
