## Cloud Monitoring

Google Cloud Platform provides a service called [Cloud
Monitoring](https://cloud.google.com/monitoring/), and
[Micrometer](https://micrometer.io/docs/registry/stackdriver) can be
used with it to easily instrument Spring Boot applications for
observability.

Spring Boot already provides auto-configuration for Cloud Monitoring.
This module enables auto-detection of the `project-id` and
`credentials`. Also, it can be customized.

Maven coordinates, using [Spring Cloud GCP
BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-metrics</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-metrics")
    }

You must enable Cloud Monitoring API from the Google Cloud Console in
order to capture metrics. Navigate to the [Cloud Monitoring
API](https://console.cloud.google.com/apis/api/monitoring.googleapis.com/overview)
for your project and make sure it’s enabled.

Spring Boot Starter for Cloud Monitoring uses Micrometer.

### Configuration

All configurations are optional:

|                                                    |                                                                                                  |          |               |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------ | -------- | ------------- |
| Name                                               | Description                                                                                      | Required | Default value |
| `spring.cloud.gcp.metrics.enabled`                 | Auto-configure Micrometer to send metrics to Cloud Monitoring.                                   | No       | `true`        |
| `spring.cloud.gcp.metrics.project-id`              | Overrides the project ID from the [Spring Cloud GCP Module](#spring-cloud-gcp-core)              | No       |               |
| `spring.cloud.gcp.metrics.credentials.location`    | Overrides the credentials location from the [Spring Cloud GCP Module](#spring-cloud-gcp-core)    | No       |               |
| `spring.cloud.gcp.metrics.credentials.encoded-key` | Overrides the credentials encoded key from the [Spring Cloud GCP Module](#spring-cloud-gcp-core) | No       |               |
| `spring.cloud.gcp.metrics.credentials.scopes`      | Overrides the credentials scopes from the [Spring Cloud GCP Module](#spring-cloud-gcp-core)      | No       |               |

You can use core Spring Boot Actuator properties to control reporting
frequency, etc. Read [Spring Boot Actuator
documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-metrics-export-stackdriver)
for more information on Stackdriver Actuator configurations.

#### Metrics Disambiguation

By default, `spring-cloud-gcp-starter-metrics`/the `StackdriverMeterRegistry` does not add any application/pod specific tags to the metrics,
thus google is unable to distinguish between multiple metric sources.
This could lead to the following warning inside your applications logs:

````txt
2022-08-15 10:26:00.248  WARN 1 --- [trics-publisher] i.m.s.StackdriverMeterRegistry           : failed to send metrics to Stackdriver
````

The actual cause message may vary:

````txt
One or more TimeSeries could not be written:
One or more points were written more frequently than the maximum sampling period configured for the metric.: global{} timeSeries[4]: custom.googleapis.com/process/uptime{};
One or more points were written more frequently than the maximum sampling period configured for the metric.: global{} timeSeries[6]: custom.googleapis.com/system/load/average/1m{};
One or more points ...
````

or even:

````txt
Caused by: io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Exception: Header size exceeded max allowed size (10240)
````

(due to the error message being too long)

Google rejects metric updates for entries that it has received before (from another application) for the same interval.
To avoid these conflicts and in order to distinguish between applications/instances you should add a configuration similar to this:

````yaml
management:
  metrics:
    tags:
      app: my-foobar-service
      instance: ${random.uuid}
````

Instead of the random uuid you could also use the pod id/the hostname or some other instance id.
Read more about custom tags [here](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator.metrics.customizing.common-tags).

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-metrics-sample)
is available.
