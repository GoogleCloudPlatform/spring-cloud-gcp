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

package com.google.cloud.spanner.r2dbc.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Duration;
import com.google.rpc.RetryInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import org.junit.Test;

public class SpannerExceptionUtilTest {

  @Test
  public void testNonRetryableException() {
    assertThat(SpannerExceptionUtil.isRetryable(new IllegalArgumentException())).isFalse();
    assertThat(SpannerExceptionUtil.isRetryable(new IOException())).isFalse();

    StatusRuntimeException nonRetryableException = new StatusRuntimeException(Status.ABORTED);
    assertThat(SpannerExceptionUtil.isRetryable(nonRetryableException)).isFalse();
  }

  @Test
  public void testRetryableInternalException() {
    StatusRuntimeException retryableException =
        new StatusRuntimeException(
            Status.INTERNAL.withDescription("HTTP/2 error code: INTERNAL_ERROR"), null);

    assertThat(SpannerExceptionUtil.isRetryable(retryableException)).isTrue();
  }

  @Test
  public void testRetryableExceptionWithDelay() {
    RetryInfo retryInfo =
        RetryInfo.newBuilder()
            .setRetryDelay(Duration.newBuilder().setSeconds(22L))
            .build();

    Metadata errorMetadata = new Metadata();
    errorMetadata.put(ProtoUtils.keyForProto(RetryInfo.getDefaultInstance()), retryInfo);

    StatusRuntimeException retryableException =
        new StatusRuntimeException(Status.RESOURCE_EXHAUSTED, errorMetadata);

    assertThat(SpannerExceptionUtil.isRetryable(retryableException)).isTrue();
  }
}
