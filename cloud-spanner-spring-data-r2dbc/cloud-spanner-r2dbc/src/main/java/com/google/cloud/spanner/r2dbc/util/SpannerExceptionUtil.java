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

import com.google.rpc.Code;
import com.google.rpc.RetryInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.r2dbc.spi.R2dbcBadGrammarException;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import io.r2dbc.spi.R2dbcPermissionDeniedException;
import io.r2dbc.spi.R2dbcTransientResourceException;
import java.time.Duration;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Extracts metadata from exceptions thrown during Spanner RPCs.
 */
public class SpannerExceptionUtil {
  private static final Metadata.Key<RetryInfo> KEY_RETRY_INFO =
      ProtoUtils.keyForProto(RetryInfo.getDefaultInstance());

  private static final Set<String> RETRYABLE_ERROR_MESSAGES =
      CollectionsBuilder.setOf(
          "HTTP/2 error code: INTERNAL_ERROR",
          "Connection closed with unknown cause",
          "Received unexpected EOS on DATA frame from server");

  /**
   * Manually creates an {@link R2dbcException} from a provided error code and message.
   *
   * @param errorCode the Spanner error code of the error.
   * @param message the error message.
   *
   * @return the resulting {@link R2dbcException} that is propagated to the user.
   */
  public static R2dbcException createR2dbcException(int errorCode, String message) {
    return createR2dbcException(errorCode, message, null);
  }

  /**
   * Extracts metadata of a thrown exception and creates a {@link R2dbcException} from it.
   *
   * @param baseException the base exception that is thrown
   *
   * @return the resulting {@link R2dbcException} that is propagated to the user.
   */
  public static R2dbcException createR2dbcException(Throwable baseException) {
    if (baseException == null) {
      return new R2dbcNonTransientResourceException();
    } else if (!(baseException instanceof StatusRuntimeException)) {
      return new R2dbcNonTransientResourceException(baseException.getMessage(), baseException);
    }

    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) baseException;
    int errorCode = statusRuntimeException.getStatus().getCode().value();

    if (isRetryable(statusRuntimeException)) {
      return new R2dbcTransientResourceException(
          baseException.getMessage(), null, errorCode, baseException);
    } else {
      return createR2dbcException(
          errorCode, baseException.getMessage(), baseException);
    }
  }

  /**
   * Private helper to manually create an {@link R2dbcException} given the error code,
   * error message and base exception.
   */
  private static R2dbcException createR2dbcException(
      int errorCode, String message, @Nullable Throwable baseException) {

    switch (errorCode) {
      case Code.ALREADY_EXISTS_VALUE:
        return new R2dbcDataIntegrityViolationException(message, null, errorCode, baseException);
      case Code.INVALID_ARGUMENT_VALUE:
        return new R2dbcBadGrammarException(message, null, errorCode, baseException);
      case Code.PERMISSION_DENIED_VALUE:
        return new R2dbcPermissionDeniedException(message, null, errorCode, baseException);
      default:
        return new R2dbcNonTransientResourceException(message, null, errorCode, baseException);
    }
  }

  /**
   * Returns whether an exception thrown should be retried.
   *
   * <p>Derived from google-cloud-java/SpannerExceptionFactory.java:
   * https://github.com/googleapis/java-spanner/blob/master/google-cloud-spanner/src/main/java/com/google/cloud/spanner/SpannerExceptionFactory.java
   */
  private static boolean isRetryable(StatusRuntimeException statusRuntimeException) {
    if (statusRuntimeException.getStatus().getCode() == Status.Code.INTERNAL
        && RETRYABLE_ERROR_MESSAGES.stream().anyMatch(
            errorFragment -> statusRuntimeException.getMessage().contains(errorFragment))) {
      return true;
    }

    return statusRuntimeException.getStatus().getCode() == Status.Code.RESOURCE_EXHAUSTED
        && extractRetryDelay(statusRuntimeException) != null;

  }

  /**
   * Extracts the retry delay from the Spanner exception if it exists; else returns null.
   */
  private static Duration extractRetryDelay(Throwable cause) {
    Metadata trailers = Status.trailersFromThrowable(cause);
    if (trailers != null && trailers.containsKey(KEY_RETRY_INFO)) {
      RetryInfo retryInfo = trailers.get(KEY_RETRY_INFO);
      if (retryInfo.hasRetryDelay()) {
        com.google.protobuf.Duration protobufDuration = retryInfo.getRetryDelay();
        return Duration.ofSeconds(protobufDuration.getSeconds())
            .withNanos(protobufDuration.getNanos());
      }
    }

    return null;
  }
}
