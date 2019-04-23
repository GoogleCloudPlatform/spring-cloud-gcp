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

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.reactivestreams.Publisher;

/**
 * An implementation of {@link ConnectionFactory} for creating connections to Cloud Spanner
 * database.
 *
 * @author Mike Eltsufin
 */
public class SpannerConnectionFactory implements ConnectionFactory {

  @Override
  public Publisher<? extends Connection> create() {
    return null;
  }

  @Override
  public ConnectionFactoryMetadata getMetadata() {
    return null;
  }
}
