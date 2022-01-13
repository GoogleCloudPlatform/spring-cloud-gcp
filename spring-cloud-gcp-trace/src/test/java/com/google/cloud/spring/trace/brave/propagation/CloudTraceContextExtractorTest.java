/*
 * Copyright 2017-2022 the original author or authors.
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

package com.google.cloud.spring.trace.brave.propagation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import brave.propagation.B3Propagation;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Arrays;
import org.junit.Test;

public class CloudTraceContextExtractorTest {

  @Test
  public void testExtractCloudTraceContext_traceTrue() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/4981115762139876185;o=1";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.context().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.context().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.context().spanId()).isEqualTo(4981115762139876185L);
    assertThat(context.context().sampled()).isTrue();
  }

  @Test
  public void testExtractCloudTraceContext_spanIdZero() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/0;o=1";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.traceIdContext().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.traceIdContext().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.traceIdContext().sampled()).isTrue();
  }

  @Test
  public void extractFailsOnNullRequest() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/0;o=1";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    assertThatThrownBy(() -> extractor.extract(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("request == null");
  }

  @Test
  public void testExtractCloudTraceContext_traceFalse() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/4981115762139876185;o=0";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.context().sampled()).isFalse();
  }

  @Test
  public void testExtractCloudTraceContext_missingTraceTrueValue() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/4981115762139876185;o=";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.context().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.context().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.context().spanId()).isEqualTo(4981115762139876185L);
    assertThat(context.context().sampled()).isNull();
  }

  @Test
  public void testExtractCloudTraceContext_noTraceTrue() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/4981115762139876185";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.context().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.context().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.context().spanId()).isEqualTo(4981115762139876185L);
    assertThat(context.context().sampled()).isNull();
  }

  @Test
  public void testExtractCloudTraceContext_noSpanId() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.traceIdContext().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.traceIdContext().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.traceIdContext().sampled()).isNull();
  }

  @Test
  public void testExtractCloudTraceContext_unsignedLong() {
    String cloudTraceContext = "8fd836bcfe241ee19a057679a77ba317/13804021222261907717";
    CloudTraceContextExtractor extractor =
        new CloudTraceContextExtractor<>(
            B3Propagation.FACTORY.get(), (request, key) -> cloudTraceContext);

    TraceContextOrSamplingFlags context = extractor.extract(new Object());
    assertThat(context.context().traceId()).isEqualTo(-7348336952112078057L);
    assertThat(context.context().traceIdHigh()).isEqualTo(-8081649345970823455L);
    assertThat(context.context().spanId()).isEqualTo(-4642722851447643899L);
    assertThat(context.context().sampled()).isNull();
  }

  @Test
  public void parseUnsignedLong() {
    // max int64
    assertThat(CloudTraceContextExtractor.parseUnsignedLong("9223372036854775807"))
        .isEqualTo(Long.parseUnsignedLong("9223372036854775807"));

    // max int64 + 1
    assertThat(CloudTraceContextExtractor.parseUnsignedLong("9223372036854775808"))
        .isEqualTo(Long.parseUnsignedLong("9223372036854775808"));

    // max uint64
    assertThat(CloudTraceContextExtractor.parseUnsignedLong("18446744073709551615"))
        .isEqualTo(Long.parseUnsignedLong("18446744073709551615"));

    // max uint64 + 1
    try {
      CloudTraceContextExtractor.parseUnsignedLong("18446744073709551616");
      failBecauseExceptionWasNotThrown(NumberFormatException.class);
    } catch (NumberFormatException e) {
      // ignore exception
    }
  }

  @Test
  public void traceIdReturnsNullIfLengthIncorrect() {
    long[] longParts = CloudTraceContextExtractor.convertHexTraceIdToLong("ABCDEF");
    assertThat(longParts).isNull();
  }

  @Test
  public void traceIdReturnsNullOnInvalidInputInFirstHalf() {
    long[] longParts = CloudTraceContextExtractor.convertHexTraceIdToLong("aaXaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    assertThat(longParts).isNull();
  }

  @Test
  public void traceIdReturnsNullOnInvalidInputInSecondHalf() {
    long[] longParts = CloudTraceContextExtractor.convertHexTraceIdToLong("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaXaa");
    assertThat(longParts).isNull();
  }

}
