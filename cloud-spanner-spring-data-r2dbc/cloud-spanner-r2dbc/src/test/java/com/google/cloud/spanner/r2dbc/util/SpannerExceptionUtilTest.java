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

package com.google.cloud.spanner.r2dbc.util;

import static com.google.cloud.spanner.r2dbc.util.SpannerExceptionUtil.createR2dbcException;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Duration;
import com.google.rpc.Code;
import com.google.rpc.RetryInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import io.r2dbc.spi.R2dbcPermissionDeniedException;
import io.r2dbc.spi.R2dbcTransientResourceException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class SpannerExceptionUtilTest {

  @Test
  void testCreateR2dbcException() {
    R2dbcException exception = SpannerExceptionUtil.createR2dbcException(
        Code.ALREADY_EXISTS_VALUE, "test");

    assertThat(exception).isInstanceOf(R2dbcDataIntegrityViolationException.class);
    assertThat(exception).hasMessage("test");
  }

  @Test
  void testNonRetryableException() {
    assertThat(createR2dbcException(new IllegalArgumentException()))
        .isInstanceOf(R2dbcNonTransientResourceException.class);
    assertThat(createR2dbcException((new IOException())))
        .isInstanceOf(R2dbcNonTransientResourceException.class);

    StatusRuntimeException nonRetryableException =
        new StatusRuntimeException(Status.PERMISSION_DENIED);
    assertThat(createR2dbcException(nonRetryableException))
        .isInstanceOf(R2dbcPermissionDeniedException.class);
  }

  @Test
  void testRetryableInternalException() {
    StatusRuntimeException retryableException =
        new StatusRuntimeException(
            Status.INTERNAL.withDescription("HTTP/2 error code: INTERNAL_ERROR"), null);

    assertThat(createR2dbcException(retryableException))
        .isInstanceOf(R2dbcTransientResourceException.class);
  }

  @Test
  void testRetryableExceptionWithDelay() {
    RetryInfo retryInfo =
        RetryInfo.newBuilder()
            .setRetryDelay(Duration.newBuilder().setSeconds(22L))
            .build();

    Metadata errorMetadata = new Metadata();
    errorMetadata.put(ProtoUtils.keyForProto(RetryInfo.getDefaultInstance()), retryInfo);

    StatusRuntimeException retryableException =
        new StatusRuntimeException(Status.RESOURCE_EXHAUSTED, errorMetadata);

    assertThat(createR2dbcException(retryableException))
        .isInstanceOf(R2dbcTransientResourceException.class);
  }
}
