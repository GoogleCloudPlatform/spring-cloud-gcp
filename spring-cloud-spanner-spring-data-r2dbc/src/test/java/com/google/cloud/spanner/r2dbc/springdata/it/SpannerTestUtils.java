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

package com.google.cloud.spanner.r2dbc.springdata.it;

import io.r2dbc.spi.Connection;
import reactor.core.publisher.Mono;

/**
 * Helper functions for Spanner integration testing.
 */
public class SpannerTestUtils {

  /**
   * Returns true if the Spanner table exists; false if not.
   */
  public static boolean tableExists(Connection connection, String tableName) {
    return Mono.from(connection.createStatement(
        "SELECT table_name FROM information_schema.tables WHERE table_name = @name")
        .bind("name", tableName)
        .execute())
        .flatMapMany(result -> result.map((r, m) -> r))
        .hasElements()
        .block();
  }
}
