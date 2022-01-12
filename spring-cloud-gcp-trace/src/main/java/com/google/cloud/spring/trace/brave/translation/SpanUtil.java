package com.google.cloud.spring.trace.brave.translation;

import com.google.devtools.cloudtrace.v2.TruncatableString;

final class SpanUtil {

  static TruncatableString toTruncatableString(String string) {
    // NOTE: Java and Go implementations of opencensus seem to differ in their interpretation of
    // whether a "request" Span needs to have truncated strings. We'll assume the simpler
    // implementation of Java is correct and that the truncation semantics are for responses, not
    // requests.
    //
    // Reference:
    //   Java -
    // https://github.com/census-instrumentation/opencensus-java/blob/d5f7efe3ea6b808bad1b3c36db9e496d72e75238/exporters/trace/stackdriver/src/main/java/io/opencensus/exporter/trace/stackdriver/StackdriverV2ExporterHandler.java#L420
    //
    //   Go -
    // https://github.com/census-ecosystem/opencensus-go-exporter-stackdriver/blob/899e456273f5c46d23aef8f0c66e899d7d1e17f4/trace_proto.go#L247
    return TruncatableString.newBuilder().setValue(string).setTruncatedByteCount(0).build();
  }

  private SpanUtil() {}
}
