/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.util.Loader;
import com.google.cloud.logging.Severity;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.util.MapBuilder;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StringUtils;

/**
 * This class provides a JSON layout for a Logback appender compatible to the Stackdriver log
 * format.
 *
 * <p>Reference: https://cloud.google.com/logging/docs/agent/configuration#process-payload
 */
public class StackdriverJsonLayout extends JsonLayout {

  private static final Map<Level, String> logbackToSeverityMap =
      new MapBuilder<Level, String>()
          .put(Level.TRACE, Severity.DEBUG.name())
          .put(Level.DEBUG, Severity.DEBUG.name())
          .put(Level.INFO, Severity.INFO.name())
          .put(Level.WARN, Severity.WARNING.name())
          .put(Level.ERROR, Severity.ERROR.name())
          .build();

  private String projectId;

  private String traceIdMdcField;

  private String spanIdMdcField;

  private boolean includeTraceId;

  private boolean includeSpanId;

  private boolean includeExceptionInMessage;

  private StackdriverErrorReportingServiceContext serviceContext;

  private Map<String, Object> customJson;

  private Set<String> filteredMdcFields;

  private final List<JsonLoggingEventEnhancer> loggingEventEnhancers = new ArrayList<>();

  /** creates a layout for a Logback appender compatible to the Stackdriver log format. */
  public StackdriverJsonLayout() {
    this.traceIdMdcField = StackdriverTraceConstants.MDC_FIELD_TRACE_ID;
    this.spanIdMdcField = StackdriverTraceConstants.MDC_FIELD_SPAN_ID;
    this.appendLineSeparator = true;
    this.includeExceptionInMessage = true;
    this.includeException = false;
    this.includeTraceId = true;
    this.includeSpanId = true;
    Gson formatter = new Gson();
    setJsonFormatter(formatter::toJson);
  }

  /**
   * Get the project id.
   *
   * @return the Google Cloud project id relevant for logging the traceId
   */
  public String getProjectId() {
    return this.projectId;
  }

  /**
   * set the project id.
   *
   * @param projectId the Google Cloud project id relevant for logging the traceId
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * Get the MDC filed name for trace id.
   *
   * @return the MDC field name for retrieving a trace id
   * @since 2.0.5
   */
  public String getTraceIdMdcField() {
    return traceIdMdcField;
  }

  /**
   * Set the MDC filed name for trace id.
   *
   * @param traceIdMdcField the MDC field name for retrieving a trace id
   * @since 2.0.5
   */
  public void setTraceIdMdcField(String traceIdMdcField) {
    this.traceIdMdcField = traceIdMdcField;
  }

  /**
   * Get the MDC field name for span id.
   *
   * @return the MDC field name for retrieving a span id
   * @since 2.0.5
   */
  public String getSpanIdMdcField() {
    return spanIdMdcField;
  }

  /**
   * Set the MDC field name for span id.
   *
   * @param spanIdMdcField the MDC field name for retrieving a span id
   * @since 2.0.5
   */
  public void setSpanIdMdcField(String spanIdMdcField) {
    this.spanIdMdcField = spanIdMdcField;
  }

  /**
   * check if the trace id is included.
   *
   * @return true if the traceId should be included into the JSON
   */
  public boolean isIncludeTraceId() {
    return this.includeTraceId;
  }

  /**
   * set whether the trace id is included.
   *
   * @param includeTraceId true if the traceId should be included into the JSON
   */
  public void setIncludeTraceId(boolean includeTraceId) {
    this.includeTraceId = includeTraceId;
  }

  /**
   * check if the span id is included.
   *
   * @return true if the spanId should be included into the JSON
   */
  public boolean isIncludeSpanId() {
    return this.includeSpanId;
  }

  /**
   * set whether the span id is included.
   *
   * @param includeSpanId true if the spanId should be included into the JSON
   */
  public void setIncludeSpanId(boolean includeSpanId) {
    this.includeSpanId = includeSpanId;
  }

  /**
   * check if there is an included exception in the message.
   *
   * @return true if the exception should be added to the message
   */
  public boolean isIncludeExceptionInMessage() {
    return this.includeExceptionInMessage;
  }

  /**
   * set whether the exception is included in the message.
   *
   * @param includeExceptionInMessage true if the exception should be added to the message
   */
  public void setIncludeExceptionInMessage(boolean includeExceptionInMessage) {
    this.includeExceptionInMessage = includeExceptionInMessage;
  }

  /**
   * set the service context for stackdriver.
   *
   * @param serviceContext the service context
   * @since 1.2
   */
  public void setServiceContext(StackdriverErrorReportingServiceContext serviceContext) {
    this.serviceContext = serviceContext;
  }

  /**
   * set custom json data to include in log output.
   *
   * @param json json string
   * @since 1.2
   */
  public void setCustomJson(String json) {
    Gson gson = new Gson();
    this.customJson = gson.fromJson(json, Map.class);
  }

  /**
   * Add additional logging enhancers that implement {@link JsonLoggingEventEnhancer}.
   *
   * @param enhancerClassName class name of the layout enhancer
   */
  public void addLoggingEventEnhancer(String enhancerClassName) {
    try {
      Class<JsonLoggingEventEnhancer> clz =
          (Class<JsonLoggingEventEnhancer>) Loader.loadClass(enhancerClassName.trim());
      loggingEventEnhancers.add(clz.getDeclaredConstructor().newInstance());
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot create object of class " + enhancerClassName, ex);
    }
  }

  @Override
  public void start() {
    super.start();

    // If no Project ID set, then attempt to resolve it with the default project ID provider
    if (!StringUtils.hasText(this.projectId) || this.projectId.endsWith("_IS_UNDEFINED")) {
      GcpProjectIdProvider projectIdProvider = new DefaultGcpProjectIdProvider();
      this.projectId = projectIdProvider.getProjectId();
    }

    this.filteredMdcFields =
        new HashSet<>(
            Arrays.asList(
                traceIdMdcField, spanIdMdcField, StackdriverTraceConstants.MDC_FIELD_SPAN_EXPORT));
  }

  /**
   * Convert a logging event into a Map.
   *
   * @param event the logging event
   * @return the map which should get rendered as JSON
   */
  @Override
  protected Map<String, Object> toJsonMap(ILoggingEvent event) {

    Map<String, Object> map = new LinkedHashMap<>();

    if (this.includeMDC) {
      Map<String, String> shallowCopy = new HashMap<>(event.getMDCPropertyMap());
      shallowCopy.keySet().removeAll(filteredMdcFields);
      map.putAll(shallowCopy);
    }
    if (this.includeTimestamp) {
      map.put(
          StackdriverTraceConstants.TIMESTAMP_SECONDS_ATTRIBUTE,
          TimeUnit.MILLISECONDS.toSeconds(event.getTimeStamp()));
      map.put(
          StackdriverTraceConstants.TIMESTAMP_NANOS_ATTRIBUTE,
          TimeUnit.MILLISECONDS.toNanos(event.getTimeStamp() % 1_000));
    }

    add(
        StackdriverTraceConstants.SEVERITY_ATTRIBUTE,
        this.includeLevel,
        logbackToSeverityMap.getOrDefault(event.getLevel(), Severity.DEFAULT.name()),
        map);

    add(JsonLayout.THREAD_ATTR_NAME, this.includeThreadName, event.getThreadName(), map);
    add(JsonLayout.LOGGER_ATTR_NAME, this.includeLoggerName, event.getLoggerName(), map);

    if (this.includeFormattedMessage) {
      map.put(JsonLayout.FORMATTED_MESSAGE_ATTR_NAME, formatMessage(event));
    }
    add(JsonLayout.MESSAGE_ATTR_NAME, this.includeMessage, event.getMessage(), map);
    add(
        JsonLayout.CONTEXT_ATTR_NAME,
        this.includeContextName,
        event.getLoggerContextVO().getName(),
        map);
    addThrowableInfo(JsonLayout.EXCEPTION_ATTR_NAME, this.includeException, event, map);
    addTraceId(event, map);
    add(
        StackdriverTraceConstants.SPAN_ID_ATTRIBUTE,
        this.includeSpanId,
        event.getMDCPropertyMap().get(spanIdMdcField),
        map);
    if (this.serviceContext != null) {
      map.put(StackdriverTraceConstants.SERVICE_CONTEXT_ATTRIBUTE, this.serviceContext);
    }
    if (this.customJson != null && !this.customJson.isEmpty()) {
      for (Map.Entry<String, Object> entry : this.customJson.entrySet()) {
        map.putIfAbsent(entry.getKey(), entry.getValue());
      }
    }
    addCustomDataToJsonMap(map, event);

    for (JsonLoggingEventEnhancer enhancer : loggingEventEnhancers) {
      enhancer.enhanceJsonLogEntry(map, event);
    }

    return map;
  }

  private String formatMessage(ILoggingEvent event) {
    // the formatted message might be null, don't initialize StringBuilder with it, but append it
    // afterwards
    StringBuilder message = new StringBuilder();
    message.append(event.getFormattedMessage());
    if (!this.includeExceptionInMessage) {
      return message.toString();
    }
    IThrowableProxy throwableProxy = event.getThrowableProxy();
    if (throwableProxy != null) {
      message.append(formatThrowable(event));
    }
    return message.toString();
  }

  private String formatThrowable(ILoggingEvent event) {
    String stackTrace = getThrowableProxyConverter().convert(event);
    return StringUtils.hasText(stackTrace) ? "\n" + stackTrace : "";
  }

  protected String formatTraceId(final String traceId) {
    // Trace IDs are either 64-bit or 128-bit, which is 16-digit hex, or 32-digit hex.
    // If traceId is 64-bit (16-digit hex), then we need to prepend 0's to make a 32-digit hex.
    if (traceId != null && traceId.length() == 16) {
      return "0000000000000000" + traceId;
    }
    return traceId;
  }

  private void addTraceId(ILoggingEvent event, Map<String, Object> map) {
    if (!this.includeTraceId) {
      return;
    }

    String traceId = event.getMDCPropertyMap().get(traceIdMdcField);
    if (traceId == null) {
      traceId = TraceIdLoggingEnhancer.getCurrentTraceId();
    }
    if (StringUtils.hasText(traceId)
        && StringUtils.hasText(this.projectId)
        && !this.projectId.endsWith("_IS_UNDEFINED")) {
      traceId =
          StackdriverTraceConstants.composeFullTraceName(this.projectId, formatTraceId(traceId));
    }

    add(StackdriverTraceConstants.TRACE_ID_ATTRIBUTE, this.includeTraceId, traceId, map);
  }
}
