## Cloud Logging

Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-logging</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-logging")
    }

[Cloud Logging](https://cloud.google.com/logging/) is the managed
logging service provided by Google Cloud.

This module provides support for associating a web request trace ID with
the corresponding log entries. It does so by retrieving the
`X-B3-TraceId` value from the [Mapped Diagnostic Context
(MDC)](https://logback.qos.ch/manual/mdc.html), which is set by Micrometer. If Spring Cloud Micrometer isn’t used, the configured
`TraceIdExtractor` extracts the desired header value and sets it as the
log entry’s trace ID. This allows grouping of log messages by request,
for example, in the [Google Cloud Console Logs
viewer](https://console.cloud.google.com/logs/viewer).

<div class="note">

Due to the way logging is set up, the Google Cloud project ID and credentials
defined in `application.properties` are ignored. Instead, you should set
the `GOOGLE_CLOUD_PROJECT` and `GOOGLE_APPLICATION_CREDENTIALS`
environment variables to the project ID and credentials private key
location, respectively. You can do this easily if you’re using the
[Google Cloud SDK](https://cloud.google.com/sdk), using the `gcloud
config set project [YOUR_PROJECT_ID]` and `gcloud auth
application-default login` commands, respectively.

</div>

### Web MVC Interceptor

For use in Web MVC-based applications, `TraceIdLoggingWebMvcInterceptor`
is provided that extracts the request trace ID from an HTTP request
using a `TraceIdExtractor` and stores it in a thread-local, which can
then be used in a logging appender to add the trace ID metadata to log
messages.

<div class="warning">

If Spring Framework on Google Cloud Trace is enabled, the logging module disables itself
and delegates log correlation to Micrometer.

</div>

`LoggingWebMvcConfigurer` configuration class is also provided to help
register the `TraceIdLoggingWebMvcInterceptor` in Spring MVC
applications.

Applications hosted on the Google Cloud include trace IDs under
the `x-cloud-trace-context` header, which will be included in log
entries. However, if Micrometer is used the trace ID will be picked up from
the MDC.

### Logback Support

Currently, only Logback is supported and there are 2 possibilities to
log to Cloud Logging via this library with Logback: via direct API calls
and through JSON-formatted console logs.

#### Log via API

A Cloud Logging appender is available using
`com/google/cloud/spring/logging/logback-appender.xml`. This appender
builds a Cloud Logging log entry from a JUL or Logback log entry, adds a
trace ID to it and sends it to Cloud Logging.

`STACKDRIVER_LOG_NAME` and `STACKDRIVER_LOG_FLUSH_LEVEL` environment
variables can be used to customize the `STACKDRIVER` appender.

Your configuration may then look like this:

``` xml
<configuration>
  <include resource="com/google/cloud/spring/logging/logback-appender.xml" />

  <root level="INFO">
    <appender-ref ref="STACKDRIVER" />
  </root>
</configuration>
```

If you want to have more control over the log output, you can further
configure the appender. The following properties are available  (see [java-logging-logback project](https://github.com/googleapis/java-logging-logback) for the full list):

| Property    | Default Value | Description                                                                                                                                                                                                                                 |
|-------------|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `log`       | `spring.log` | The Cloud Logging Log name. This can also be set via the `STACKDRIVER_LOG_NAME` environmental variable.                                                                                                                                     |
| `flushLevel` | `WARN`      | If a log entry with this level is encountered, trigger a flush of locally buffered log to Cloud Logging. This can also be set via the `STACKDRIVER_LOG_FLUSH_LEVEL` environmental variable.                                                 |
| `enhancer`  |             | Fully qualified class name for customizing a logging entry; must implement `com.google.cloud.logging.LoggingEnhancer`.                                                                                                                      |
| `loggingEventEnhancer`  |             | Fully qualified class name for customizing a logging entry given an [`ILoggingEvent`](https://logback.qos.ch/apidocs/ch/qos/logback/classic/spi/ILoggingEvent.html); must implement `com.google.cloud.logging.logback.LoggingEventEnhancer`. |

#### Asynchronous Logging

If you would like to send logs asynchronously to Cloud Logging, you can
use the `AsyncAppender`.

Your configuration may then look like this:

``` xml
<configuration>
  <include resource="com/google/cloud/spring/logging/logback-appender.xml" />

  <appender name="ASYNC_STACKDRIVER" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="STACKDRIVER" />
  </appender>

  <root level="INFO">
    <appender-ref ref="ASYNC_STACKDRIVER" />
  </root>
</configuration>
```

#### Log via Console

For Logback, a
`com/google/cloud/spring/logging/logback-json-appender.xml` file is made
available for import to make it easier to configure the JSON Logback
appender.

Your configuration may then look something like this:

``` xml
<configuration>
  <include resource="com/google/cloud/spring/logging/logback-json-appender.xml" />

  <root level="INFO">
    <appender-ref ref="CONSOLE_JSON" />
  </root>
</configuration>
```

If your application is running on Google Kubernetes Engine, Google
Compute Engine or Google App Engine Flexible, your console logging is
automatically saved to Google Cloud Logging. Therefore, you can just
include `com/google/cloud/spring/logging/logback-json-appender.xml` in
your logging configuration, which logs JSON entries to the console. The
trace id will be set correctly.

If you want to have more control over the log output, you can further
configure the appender. The following properties are available:

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 33%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr class="header">
<th>Property</th>
<th>Default Value</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p><code>projectId</code></p></td>
<td><p>If not set, default value is determined in the following order:</p>
<ol type="1">
<li><p><code>SPRING_CLOUD_GCP_LOGGING_PROJECT_ID</code> Environmental Variable.</p></li>
<li><p>Value of <code>DefaultGcpProjectIdProvider.getProjectId()</code></p></li>
</ol></td>
<td><p>This is used to generate fully qualified Cloud Trace ID format: <code>projects/[PROJECT-ID]/traces/[TRACE-ID]</code>.</p>
<p>This format is required to correlate trace between Cloud Trace and Cloud Logging.</p>
<p>If <code>projectId</code> is not set and cannot be determined, then it’ll log <code>traceId</code> without the fully qualified format.</p></td>
</tr>
<tr class="even">
<td><p><code>traceIdMdcField</code></p></td>
<td><p><code>traceId</code></p></td>
<td><p>The MDC field name for retrieving a trace id</p></td>
</tr>
<tr class="odd">
<td><p><code>spanIdMdcField</code></p></td>
<td><p><code>spanId</code></p></td>
<td><p>the MDC field name for retrieving a span id</p></td>
</tr>
<tr class="even">
<td><p><code>includeTraceId</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the trace id be included</p></td>
</tr>
<tr class="odd">
<td><p><code>includeSpanId</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the span id be included</p></td>
</tr>
<tr class="even">
<td><p><code>includeLevel</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the severity be included</p></td>
</tr>
<tr class="odd">
<td><p><code>includeThreadName</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the thread name be included</p></td>
</tr>
<tr class="even">
<td><p><code>includeMDC</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should all MDC properties be included. The MDC properties <code>X-B3-TraceId</code>, <code>X-B3-SpanId</code> and <code>X-Span-Export</code> provided by Micrometer will get excluded as they get handled separately</p></td>
</tr>
<tr class="odd">
<td><p><code>includeLoggerName</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the name of the logger be included</p></td>
</tr>
<tr class="even">
<td><p><code>includeFormattedMessage</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the formatted log message be included.</p></td>
</tr>
<tr class="odd">
<td><p><code>includeExceptionInMessage</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the stacktrace be appended to the formatted log message. This setting is only evaluated if <code>includeFormattedMessage</code> is <code>true</code></p></td>
</tr>
<tr class="even">
<td><p><code>includeContextName</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Should the logging context be included</p></td>
</tr>
<tr class="odd">
<td><p><code>includeMessage</code></p></td>
<td><p><code>false</code></p></td>
<td><p>Should the log message with blank placeholders be included</p></td>
</tr>
<tr class="even">
<td><p><code>includeException</code></p></td>
<td><p><code>false</code></p></td>
<td><p>Should the stacktrace be included as a own field</p></td>
</tr>
<tr class="odd">
<td><p><code>serviceContext</code></p></td>
<td><p>none</p></td>
<td><p>Define the Stackdriver service context data (service and version). This allows filtering of error reports for service and version in the <a href="https://console.cloud.google.com/errors">Google Cloud Error Reporting View</a>.</p></td>
</tr>
<tr class="even">
<td><p><code>customJson</code></p></td>
<td><p>none</p></td>
<td><p>Defines custom json data. Data will be added to the json output.</p></td>
</tr>
<tr class="odd">
<td><p><code>loggingEventEnhancer</code></p></td>
<td><p>none</p></td>
<td><p>Name of a class implementing <code>JsonLoggingEventEnhancer</code> which modifies the JSON logging output. This tag is repeatable.</p>
<p>Examples are provided in the <a href="https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-logging/src/main/java/com/google/cloud/spring/logging/extensions">extensions package</a>.</p>
<p>- <a href="https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-logging/src/main/java/com/google/cloud/spring/logging/extensions/LogstashLoggingEventEnhancer.java">Logstash Enhancer</a></p></td>
</tr>
</tbody>
</table>

This is an example of such an Logback configuration:

``` xml
<configuration >
  <property name="projectId" value="${projectId:-${GOOGLE_CLOUD_PROJECT}}"/>

  <appender name="CONSOLE_JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="com.google.cloud.spring.logging.StackdriverJsonLayout">
        <projectId>${projectId}</projectId>

        <!--<traceIdMdcField>traceId</traceIdMdcField>-->
        <!--<spanIdMdcField>spanId</spanIdMdcField>-->
        <!--<includeTraceId>true</includeTraceId>-->
        <!--<includeSpanId>true</includeSpanId>-->
        <!--<includeLevel>true</includeLevel>-->
        <!--<includeThreadName>true</includeThreadName>-->
        <!--<includeMDC>true</includeMDC>-->
        <!--<includeLoggerName>true</includeLoggerName>-->
        <!--<includeFormattedMessage>true</includeFormattedMessage>-->
        <!--<includeExceptionInMessage>true</includeExceptionInMessage>-->
        <!--<includeContextName>true</includeContextName>-->
        <!--<includeMessage>false</includeMessage>-->
        <!--<includeException>false</includeException>-->
        <!--<serviceContext>
              <service>service-name</service>
              <version>service-version</version>
            </serviceContext>-->
        <!--<customJson>{"custom-key": "custom-value"}</customJson>-->
        <!--<loggingEventEnhancer>your.package.YourLoggingEventEnhancer</loggingEventEnhancer> -->
      </layout>
    </encoder>
  </appender>
</configuration>
```

### Sample

A [Sample Spring Boot
Application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-logging-sample)
is provided to show how to use the Cloud logging starter.
