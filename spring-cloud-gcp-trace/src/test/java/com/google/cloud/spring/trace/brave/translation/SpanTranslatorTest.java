/*
 * Copyright 2016-2022 the original author or authors.
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

package com.google.cloud.spring.trace.brave.translation;

import static com.google.cloud.spring.trace.brave.translation.AttributesExtractor.toAttributeValue;
import static com.google.cloud.spring.trace.brave.translation.SpanTranslator.createTimestamp;
import static com.google.cloud.spring.trace.brave.translation.SpanUtil.toTruncatableString;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;

public class SpanTranslatorTest {
  /** This test is intentionally sensitive, so changing other parts makes obvious impact here */
  @Test
  public void translate_clientSpan() {
    Span zipkinSpan =
        Span.newBuilder()
            .traceId("7180c278b62e8f6a216a2aea45d08fc9")
            .parentId("6b221d5bc9e6496c")
            .id("5b4185666d50f68b")
            .name("get")
            .kind(Span.Kind.CLIENT)
            .localEndpoint(Endpoint.newBuilder().serviceName("frontend").build())
            .remoteEndpoint(
                Endpoint.newBuilder()
                    .serviceName("backend")
                    .ip("192.168.99.101")
                    .port(9000)
                    .build())
            .timestamp(1_000_000L) // 1 second after epoch
            .duration(123_456L)
            .addAnnotation(1_123_000L, "foo")
            .putTag("http.path", "/api")
            .putTag("clnt/finagle.version", "6.45.0")
            .build();

    com.google.devtools.cloudtrace.v2.Span
        translated = SpanTranslator.translate(
            com.google.devtools.cloudtrace.v2.Span.newBuilder(), zipkinSpan).build();

    assertThat(translated)
        .isEqualTo(
            com.google.devtools.cloudtrace.v2.Span.newBuilder()
                .setSpanId(zipkinSpan.id())
                .setParentSpanId(zipkinSpan.parentId())
                .setDisplayName(toTruncatableString("get"))
                .setStartTime(Timestamp.newBuilder().setSeconds(1).build())
                .setEndTime(Timestamp.newBuilder().setSeconds(1).setNanos(123_456_000).build())
                .setAttributes(com.google.devtools.cloudtrace.v2.Span.Attributes.newBuilder()
                    .putAttributeMap("clnt/finagle.version", toAttributeValue("6.45.0"))
                    .putAttributeMap("http.path", toAttributeValue("/api"))
                    .putAttributeMap("/kind", toAttributeValue("client"))
                    .putAttributeMap("/component", toAttributeValue("frontend"))
                    .build())
                .setTimeEvents(com.google.devtools.cloudtrace.v2.Span.TimeEvents.newBuilder()
                    .addTimeEvent(com.google.devtools.cloudtrace.v2.Span.TimeEvent.newBuilder()
                        .setTime(createTimestamp(1_123_000L))
                        .setAnnotation(
                            com.google.devtools.cloudtrace.v2.Span.TimeEvent.Annotation.newBuilder()
                                .setDescription(toTruncatableString("foo"))
                                .build())
                        .build())
                    .build())
                .build());
  }

  @Test
  public void translate_missingName() {
    Span zipkinSpan = Span.newBuilder().traceId("3").id("2").build();
    com.google.devtools.cloudtrace.v2.Span translated = SpanTranslator.translate(
        com.google.devtools.cloudtrace.v2.Span.newBuilder(), zipkinSpan).build();

    assertThat(translated.getDisplayName().getValue()).isNotEmpty();
  }

  @Test
  public void testTranslateSpans() {
    Span span1 =
        Span.newBuilder().id("1").traceId("1").name("/a").timestamp(1L).duration(1L).build();
    Span span2 =
        Span.newBuilder().id("2").traceId("2").name("/b").timestamp(2L).duration(1L).build();
    Span span3 =
        Span.newBuilder().id("3").traceId("1").name("/c").timestamp(3L).duration(1L).build();

    List<Span> spans = asList(span1, span2, span3);
    List<com.google.devtools.cloudtrace.v2.Span> stackdriverSpans =
        new ArrayList<>(SpanTranslator.translate("test-project", spans));

    assertThat(stackdriverSpans).hasSize(3);
    assertThat(stackdriverSpans).extracting(com.google.devtools.cloudtrace.v2.Span::getName)
        .containsExactlyInAnyOrder(
            "projects/test-project/traces/00000000000000000000000000000001/spans/0000000000000001",
            "projects/test-project/traces/00000000000000000000000000000002/spans/0000000000000002",
            "projects/test-project/traces/00000000000000000000000000000001/spans/0000000000000003");
  }

  @Test
  public void testTranslateSpanEmptyName() {
    Span spanNullName =
        Span.newBuilder().id("1").traceId("1").timestamp(1L).duration(1L).build();
    Span spanEmptyName =
        Span.newBuilder().id("2").traceId("2").name("").timestamp(2L).duration(1L).build();
    Span spanNonEmptyName =
        Span.newBuilder().id("2").traceId("2").name("somename").timestamp(2L).duration(1L).build();

    List<Span> spans = asList(spanNullName, spanEmptyName, spanNonEmptyName);
    List<com.google.devtools.cloudtrace.v2.Span> stackdriverSpans =
            new ArrayList<>(SpanTranslator.translate("test-project", spans));

    assertThat(stackdriverSpans).hasSize(3);
    assertThat(stackdriverSpans.get(0).getDisplayName().getValue()).isEqualTo("unknown");
    assertThat(stackdriverSpans.get(1).getDisplayName().getValue()).isEqualTo("unknown");
    assertThat(stackdriverSpans.get(2).getDisplayName().getValue()).isEqualTo("somename");
  }
}
