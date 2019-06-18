# Cloud Spanner R2DBC Driver

[![experimental](http://badges.github.io/stability-badges/dist/experimental.svg)](http://github.com/badges/stability-badges)

An implementation of the [R2DBC](https://r2dbc.io/) driver for [Cloud Spanner](https://cloud.google.com/spanner/) is being developed in this repository.

## Setup Instructions

The sections below describe how to setup and begin using the Cloud Spanner R2DBC driver.

An overview of the setup is as follows:

1. Add the Cloud Spanner R2DBC driver dependency to your build configuration.
2. Configure the driver credentials/authentication for your Google Cloud Platform project to access
    Cloud Spanner.
3. Instantiate the R2DBC `ConnectionFactory` in Java code to build Connections and run queries.

Details about each step is provided below.

### Project Dependency Setup

The easiest way to start using the driver is to add the driver dependency through Maven or Gradle.

**Maven Coordinates**

```
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>cloud-spanner-r2dbc</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

**Gradle Coordinates**

```
dependencies {
  compile group: 'com.google.cloud', name: 'cloud-spanner-r2dbc', version: '0.1.0-SNAPSHOT'
}
```

### Authentication

By default, the R2DBC driver will attempt to infer your account credentials from the environment
in which the application is run. There are a number of different ways to conveniently provide
account credentials to the driver.

#### Using Google Cloud SDK

Google Cloud SDK is a command line interface for Google Cloud Platform products and services.
This is a convenient way of setting up authentication during local development.

If you are using the SDK, the driver can automatically infer your account credentials from your
SDK configuration.

Instructions:

1. Install the [Google Cloud SDK](https://cloud.google.com/sdk/) for command line and
    follow the [Cloud SDK quickstart](https://cloud.google.com/sdk/docs/quickstarts)
    for your operating system.
    
2. Once setup, run `gcloud auth application-default login` and login with your Google account
    credentials. 

After completing the SDK configuration, the Spanner R2DBC driver will automatically pick up your
credentials allowing you to access your Spanner database. 

#### Using a Service Account

A [Google Service Account](https://cloud.google.com/iam/docs/understanding-service-accounts) is a
special type of Google Account intended to represent a non-human user that needs to authenticate
and be authorized to access your Google Cloud resources. Each service account has an account key JSON file that you can use to provide credentials to your
application.

This is the recommended method of authentication for production use.

You can learn how to create a service account and authenticate your application by following
[these instructions](https://cloud.google.com/docs/authentication/production#obtaining_and_providing_service_account_credentials_manually).

If you are unsatisfied with credentials inference methods, you may override this behavior
by manually specifying a service account key JSON file using the `google_credentials`
option to the `ConnectionFactory` builder.

Example:

```java
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.GOOGLE_CREDENTIALS;

String pathToCredentialsKeyFile = ...;

GoogleCredentials creds = GoogleCredentials.fromStream(new FileInputStream(credentialsLocation));
ConnectionFactoryOptions options =
    ConnectionFactoryOptions.builder()
        .option(GOOGLE_CREDENTIALS, creds)
        .option(..) // Other options here
        .build();
```

#### Using Google Cloud Platform Environment

If your application is running on Google Cloud Platform infrastructure including: Compute Engine,
Kubernetes Engine, the App Engine flexible environment, or Cloud Functions, the credentials will
be automatically inferred from the runtime environment in the Cloud. For more information, see
the [Google Cloud Platform Authentication documentation](https://cloud.google.com/docs/authentication/production#obtaining_credentials_on_compute_engine_kubernetes_engine_app_engine_flexible_environment_and_cloud_functions).

### Usage

After setting up the dependency and authentication, one can begin directly using the driver.

The entry point to using the R2DBC driver is to first configure the R2DBC connection factory.

```
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;

ConnectionFactory connectionFactory =
    ConnectionFactories.get(ConnectionFactoryOptions.builder()
        .option(DRIVER, "spanner")
        .option(PROJECT, "your-gcp-project-id")
        .option(INSTANCE, "your-spanner-instance")
        .option(DATABASE, "your-database-name")
        .build());
        
// The R2DBC connection may now be created.
Publisher<? extends Connection> connectionPublisher = connectionFactory.create();
```

The following options are available to be configured for the connection factory:

| Option Name | Description                | Required | Default Value |
|-------------|----------------------------|----------|---------------|
| `driver`    | Must be "spanner"          | True     |               |
| `project`   | Your GCP Project ID        | True     |               |
| `instance`  | Your Spanner Instance name | True     |               |
| `database`  | Your Spanner Database name | True     |               |
| `google_credentials` | Optional [Google credentials](https://cloud.google.com/docs/authentication/production) override to specify for your Google Cloud account. | False | If not provided, credentials will be [inferred from your runtime environment](https://cloud.google.com/docs/authentication/production#finding_credentials_automatically).
| `partial_result_set_fetch_size` | Number of intermediate result sets that are buffered in transit for a read query. | False | 1 |
| `ddl_operation_timeout` | Duration in seconds to wait for a DDL operation to complete before timing out | False | 600 seconds |
| `ddl_operation_poll_interval` | Duration in seconds to wait between each polling request for the completion of a DDL operation | False | 5 seconds |

## Mapping of Data Types

Cloud Spanner R2DBC Driver supports the following types:


| Spanner Type   | Java type           |
|----------------|---------------------|
|`BOOL`          |`java.lang.Boolean`  |
|`BYTES`         |`java.nio.ByteBuffer`|
|`DATE`          |`java.time.LocalDate`|
|`FLOAT64`       |`java.lang.Double`   |
|`INT64`         |`java.lang.Long`     |
|`INT64`         |`java.lang.Integer`  |
|`STRING`        |`java.lang.String`   |
|`TIMESTAMP`     |`java.sql.Timestamp` |
|`ARRAY`         |Array-Variant of the corresponding Java type (e.g. `Long[]` for `ARRAY<INT64>`)|

Null values mapping is supported in both directions.

See [Cloud Spanner documentation](https://cloud.google.com/spanner/docs/data-types) to learn more about Spanner types.

## Statements 

R2DBC statement objects are used to run statements on your Cloud Spanner database. Based on the type
of statement, the Cloud Spanner R2DBC handles the treatment and execution of the statement
slightly differently.

The table below describes whether parameter bindings are available for each statement type and
the Spanner GRPC endpoint used to execute the query.

| Statement Type | Allows Parameter Bindings | Cloud Spanner API Method   |
|----------------|---------------------------|----------------------------|
| SELECT Queries | Yes                       | [ExecuteStreamingSql](https://cloud.google.com/spanner/docs/reference/rpc/google.spanner.v1#google.spanner.v1.Spanner.ExecuteStreamingSql) |
| DML Statements | Yes                       | [ExecuteBatchDml](https://cloud.google.com/spanner/docs/reference/rpc/google.spanner.v1#google.spanner.v1.Spanner.ExecuteBatchDml) |
| DDL Statements | No                        | [UpdateDatabaseDdl](https://cloud.google.com/spanner/docs/reference/rpc/google.spanner.admin.database.v1#google.spanner.admin.database.v1.DatabaseAdmin.UpdateDatabaseDdl) |

### Binding Query Parameters

Cloud Spanner R2DBC statements support *named* parameter binding using Cloud Spanner's [parameter syntax](https://cloud.google.com/spanner/docs/sql-best-practices).
Parameter bindings by numeric indices are not supported.

SQL and DML statements can be constructed with parameters:

```java
mySpannerConnection.createStatement(
  "INSERT BOOKS (ID, TITLE) VALUES (@id, @title)")
    .bind("id", "book-id-1")
    .bind("title", "Book One")
    .add()
    .bind("id", "book-id-2")
    .bind("title", "Book Two")
    .execute()
    .flatMap(r -> r.getRowsUpdated());
``` 

The parameter identifiers must be `String`. 

The example above binds two sets of parameters to a single DML template. 
It will produce a `Publisher` (implemented by a `Flux`) containing two `SpannerResult` objects for the two instances of the statement that are executed. 

Note that calling `execute` produces R2DBC `Result` objects, but this doesn't cause the query to be run on the database. 
You must use the `map` or `getRowsUpdated` methods of the results to complete the underlying queries.

### DDL Statements

DDL statements in Spanner receive special treatment by Cloud Spanner. Creating and
dropping tables can take a long time (on the order of minutes). As a result, Cloud Spanner 
ordinarily requires that clients poll the service for the completion of these operations.

The Cloud Spanner R2DBC driver automatically handles DDL statement status polling.

The only two settings that users need to worry about are polling settings configurable
through the Spanner connection factory:

- `ddl_operation_timeout`: Duration in seconds to wait for a DDL operation to complete
    before timing out.
- `ddl_operation_poll_interval`: Duration in seconds to wait between each polling request
    for the completion of a DDL operation.
    
See the above section regarding `ConnectionFactory` options for more information.

## Back Pressure

Table rows are transmitted from Cloud Spanner in fragments called `PartialResultset`.
The number of fragments for each row cannot be determined beforehand. 
While you can decide the number of rows you request from `SpannerResult`, the Cloud Spanner R2DBC driver will always request a fixed number of fragments from Cloud Spanner to fulfill your request and will do so repeatedly if necessary.

The default number of fragments per request to Cloud Spanner is 1, but this can be configured with the `partial_result_set_fetch_size` config property for your situation.
