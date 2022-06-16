## Cloud Foundry

Spring Cloud GCP provides support for Cloud Foundry’s [GCP Service
Broker](https://docs.pivotal.io/partners/gcp-sb/index.html). Our
Pub/Sub, Cloud Spanner, Storage, Cloud Trace and Cloud SQL MySQL and
PostgreSQL starters are Cloud Foundry aware and retrieve properties like
project ID, credentials, etc., that are used in auto configuration from
the Cloud Foundry environment.

In order to take advantage of the Cloud Foundry support make sure the
following dependency is added:

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-cloudfoundry</artifactId>
</dependency>
```

In cases like Pub/Sub’s topic and subscription, or Storage’s bucket
name, where those parameters are not used in auto configuration, you can
fetch them using the VCAP mapping provided by Spring Boot. For example,
to retrieve the provisioned Pub/Sub topic, you can use the
`vcap.services.mypubsub.credentials.topic_name` property from the
application environment.

<div class="note">

If the same service is bound to the same application more than once, the
auto configuration will not be able to choose among bindings and will
not be activated for that service. This includes both MySQL and
PostgreSQL bindings to the same app.

</div>

<div class="warning">

In order for the Cloud SQL integration to work in Cloud Foundry,
auto-reconfiguration must be disabled. You can do so using the `cf
set-env <APP> JBP_CONFIG_SPRING_AUTO_RECONFIGURATION '{enabled: false}'`
command. Otherwise, Cloud Foundry will produce a `DataSource` with an
invalid JDBC URL (i.e., `jdbc:mysql://null/null`).

</div>

### User-Provided Services

[User-provided
services](https://docs.cloudfoundry.org/devguide/services/user-provided.html)
enable developers to use services that are not available in the
marketplace with their apps running on Cloud Foundry. For example, you
may want to use a user-provided service that points to a shared Google
Service (like Cloud Spanner) used across your organization.

In order for Spring Cloud GCP to detect your user-provided service as a
Google Cloud Service, you must add an [instance
tag](https://docs.cloudfoundry.org/devguide/services/managing-services.html#instance-tags-create)
indicating the Google Cloud Service it uses. The tag should simply be
the Cloud Foundry name for the Google Service.

For example, if you create a user-provided service using Cloud Spanner,
you might run:

``` shell
$ cf create-user-provided-service user-spanner-service -t "google-spanner" ...
```

This allows Spring Cloud GCP to retrieve the correct service properties
from Cloud Foundry and use them in the auto configuration for your
application.

A mapping of Google service names to Cloud Foundry names are provided
below:

|                                                             |                                        |
| ----------------------------------------------------------- | -------------------------------------- |
| Google Cloud Service                                        | Cloud Foundry Name (add this as a tag) |
| [Google Cloud Pub/Sub](https://cloud.google.com/pubsub)     | `google-pubsub`                        |
| [Google Cloud Storage](https://cloud.google.com/storage)    | `google-storage`                       |
| [Google Cloud Spanner](https://cloud.google.com/spanner)    | `google-spanner`                       |
| [Datastore](https://cloud.google.com/datastore)             | `google-datastore`                     |
| [Firestore](https://cloud.google.com/firestore)             | `google-firestore`                     |
| [BigQuery](https://cloud.google.com/bigquery)               | `google-bigquery`                      |
| [Cloud Trace](https://cloud.google.com/products/operations) | `google-stackdriver-trace`             |
| [Cloud Sql (MySQL)](https://cloud.google.com/sql)           | `google-cloudsql-mysql`                |
| [Cloud Sql (PostgreSQL)](https://cloud.google.com/sql)      | `google-cloudsql-postgres`             |
