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

import static com.google.cloud.spring.trace.brave.translation.SpanUtil.toTruncatableString;
import static java.util.logging.Level.FINE;

import com.google.devtools.cloudtrace.v2.Span.TimeEvent;
import com.google.devtools.cloudtrace.v2.Span.TimeEvents;
import com.google.protobuf.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import zipkin2.Annotation;
import zipkin2.Span;

/** SpanTranslator converts a Zipkin Span to a Stackdriver Trace Span. */
public final class SpanTranslator {
  private static final Logger LOG = Logger.getLogger(SpanTranslator.class.getName());

  static final AttributesExtractor ATTRIBUTES_EXTRACTOR;

  static {
    Map<String, String> renamedLabels = new LinkedHashMap<>();
    renamedLabels.put("http.host", "/http/host");
    renamedLabels.put("http.method", "/http/method");
    renamedLabels.put("http.status_code", "/http/status_code");
    renamedLabels.put("http.request.size", "/request/size");
    renamedLabels.put("http.response.size", "/response/size");
    renamedLabels.put("http.url", "/http/url");
    ATTRIBUTES_EXTRACTOR = new AttributesExtractor(renamedLabels);
  }

  /**
   * Convert a Collection of Zipkin Spans into a Collection of Stackdriver Trace Spans.
   *
   * @param projectId The Google Cloud Platform projectId that should be used for Stackdriver Trace
   *     Traces.
   * @param zipkinSpans The Collection of Zipkin Spans.
   * @return A Collection of Stackdriver Trace Spans.
   */
  public static List<com.google.devtools.cloudtrace.v2.Span> translate(
      String projectId, List<Span> zipkinSpans) {
    List<com.google.devtools.cloudtrace.v2.Span> result = new ArrayList<>(zipkinSpans.size());
    for (int i = 0, len = zipkinSpans.size(); i < len; i++) {
      Span zipkinSpan = zipkinSpans.get(i);
      com.google.devtools.cloudtrace.v2.Span.Builder spanBuilder =
          translate(com.google.devtools.cloudtrace.v2.Span.newBuilder(), zipkinSpan);
      spanBuilder.setName(
          "projects/"
              + projectId
              + "/traces/"
              + paddedTraceId(zipkinSpan.traceId())
              + "/spans/"
              + zipkinSpan.id());
      result.add(spanBuilder.build());
    }
    return result;
  }

  /**
   * Converts a Zipkin Span into a Stackdriver Trace Span.
   *
   * <p>Ex.
   *
   * <pre>{@code
   * traceSpan = SpanTranslator.translate(TraceSpan.newBuilder(), zipkinSpan).build();
   * }</pre>
   *
   * <p>Note: the result does not set {@link
   * com.google.devtools.cloudtrace.v2.Span.Builder#setName(String)} and it is up to callers to make
   * sure to fill it using the project ID and trace ID.
   *
   * @param spanBuilder the builder (to facilitate re-use)
   * @param zipkinSpan The Zipkin Span.
   * @return A Stackdriver Trace Span.
   */
  public static com.google.devtools.cloudtrace.v2.Span.Builder translate(
      com.google.devtools.cloudtrace.v2.Span.Builder spanBuilder, Span zipkinSpan) {
    boolean logTranslation = LOG.isLoggable(FINE);
    if (logTranslation) {
      LOG.log(FINE, ">> translating zipkin span: {0}", zipkinSpan);
    }

    spanBuilder.setSpanId(zipkinSpan.id());
    if (zipkinSpan.parentId() != null) {
      spanBuilder.setParentSpanId(zipkinSpan.parentId());
    }

    // NOTE: opencensus prefixes Send. and Recv. based on Kind. For now we reproduce our V1 behavior
    // of using the span name as the display name as is.
    spanBuilder.setDisplayName(
        toTruncatableString(
            (zipkinSpan.name() != null && !zipkinSpan.name().isEmpty())
                ? zipkinSpan.name()
                : "unknown"));

    if (zipkinSpan.timestampAsLong() != 0L) {
      spanBuilder.setStartTime(createTimestamp(zipkinSpan.timestampAsLong()));
      if (zipkinSpan.durationAsLong() != 0L) {
        Timestamp endTime =
            createTimestamp(zipkinSpan.timestampAsLong() + zipkinSpan.durationAsLong());
        spanBuilder.setEndTime(endTime);
      }
    }
    spanBuilder.setAttributes(ATTRIBUTES_EXTRACTOR.extract(zipkinSpan));

    if (!zipkinSpan.annotations().isEmpty()) {
      TimeEvents.Builder events = TimeEvents.newBuilder();
      for (Annotation annotation : zipkinSpan.annotations()) {
        events.addTimeEvent(
            TimeEvent.newBuilder()
                .setTime(createTimestamp(annotation.timestamp()))
                .setAnnotation(
                    TimeEvent.Annotation.newBuilder()
                        .setDescription(toTruncatableString(annotation.value()))));
      }
      spanBuilder.setTimeEvents(events);
    }

    if (logTranslation) {
      LOG.log(FINE, "<< translated to stackdriver span: {0}", spanBuilder);
    }
    return spanBuilder;
  }

  static Timestamp createTimestamp(long microseconds) {
    long seconds = (microseconds / 1000000);
    int remainderMicros = (int) (microseconds % 1000000);
    int remainderNanos = remainderMicros * 1000;

    return Timestamp.newBuilder().setSeconds(seconds).setNanos(remainderNanos).build();
  }

  static String paddedTraceId(String traceId) {
    if (traceId.length() == 32) {
      return traceId;
    }
    return "0000000000000000" + traceId;
  }
}
