package com.google.cloud.spring.trace.brave.sender;

import java.util.Calendar;
import java.util.TimeZone;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.Span.Kind;

public class TestConstants {

  public static final Endpoint FRONTEND =
      Endpoint.newBuilder().serviceName("frontend").ip("127.0.0.1").build();
  public static final Endpoint BACKEND =
      Endpoint.newBuilder().serviceName("backend").ip("192.168.99.101").port(9000).build();
  public static final long TODAY = midnightUTC(System.currentTimeMillis());

  public static final Span CLIENT_SPAN = Span.newBuilder()
      .traceId("7180c278b62e8f6a216a2aea45d08fc9")
      .parentId("1")
      .id("2")
      .name("get")
      .kind(Kind.CLIENT)
      .localEndpoint(FRONTEND)
      .remoteEndpoint(BACKEND)
      .timestamp((TODAY + 50L) * 1000L)
      .duration(200 * 1000L)
      .addAnnotation((TODAY + 100) * 1000L, "foo")
      .putTag("http.path", "/api")
      .putTag("clnt/finagle.version", "6.45.0")
      .build();

  public static long midnightUTC(long epochMillis) {
    Calendar day = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    day.setTimeInMillis(epochMillis);
    day.set(Calendar.MILLISECOND, 0);
    day.set(Calendar.SECOND, 0);
    day.set(Calendar.MINUTE, 0);
    day.set(Calendar.HOUR_OF_DAY, 0);
    return day.getTimeInMillis();
  }

}