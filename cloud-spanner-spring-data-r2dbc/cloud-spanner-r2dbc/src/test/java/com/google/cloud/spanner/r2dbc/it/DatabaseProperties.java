/*
 * Copyright 2019-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.it;

import com.google.cloud.ServiceOptions;

/**
 * Instance/database properties with defaults.
 */
interface DatabaseProperties {

  String INSTANCE = System.getProperty("spanner.instance", "reactivetest");

  String DATABASE = System.getProperty("spanner.database", "testdb");

  String URL = String.format(
      "r2dbc:cloudspanner://spanner.googleapis.com:443/projects/%s/instances/%s/databases/%s",
      ServiceOptions.getDefaultProjectId(), INSTANCE, DATABASE);
}
