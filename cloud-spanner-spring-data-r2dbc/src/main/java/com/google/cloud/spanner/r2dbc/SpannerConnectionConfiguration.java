/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc;

import com.google.cloud.spanner.r2dbc.util.Assert;

/**
 * Configurable properties for Cloud Spanner.
 */
public class SpannerConnectionConfiguration {

  private String instanceName;

  private String databaseName;

  /**
   * Basic property initializing constructor.
   *
   * @param instanceName instance to connect to
   * @param databaseName database to connect to.
   */
  public SpannerConnectionConfiguration(String instanceName, String databaseName) {
    Assert.requireNonNull(instanceName, "instanceName must not be null");
    Assert.requireNonNull(databaseName, "databaseName must not be null");

    this.instanceName = instanceName;
    this.databaseName = databaseName;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

}
