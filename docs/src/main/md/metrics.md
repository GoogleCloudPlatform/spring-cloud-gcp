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

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-metrics-sample)
is available.
