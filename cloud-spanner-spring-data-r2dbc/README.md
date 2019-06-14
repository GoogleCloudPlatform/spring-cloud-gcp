# Cloud Spanner R2DBC Driver

[![experimental](http://badges.github.io/stability-badges/dist/experimental.svg)](http://github.com/badges/stability-badges)

An implementation of the [R2DBC](https://r2dbc.io/) driver for [Cloud Spanner](https://cloud.google.com/spanner/) is being developed in this repository.

## Setup Instructions

This section describes how to setup and begin using the Cloud Spanner R2DBC driver.
Below are the dependencies to add to your build configuration.

### Maven Coordinates

```
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>cloud-spanner-r2dbc</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle Coordinates

```
dependencies {
  compile group: 'com.google.cloud', name: 'cloud-spanner-r2dbc', version: '0.1.0-SNAPSHOT'
}
```

### Usage

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
| `DRIVER`    | Must be "spanner"          | True     |               |
| `PROJECT`   | Your GCP Project ID        | True     |               |
| `INSTANCE`  | Your Spanner Instance name | True     |               |
| `DATABASE`  | Your Spanner Database name | True     |               |
| `GOOGLE_CREDENTIALS` | Optional [Google credentials](https://cloud.google.com/docs/authentication/production) to specify for your Google Cloud account. | False | If not provided, credentials will be [inferred from your runtime environment](https://cloud.google.com/docs/authentication/production#finding_credentials_automatically).
| `PARTIAL_RESULT_SET_FETCH_SIZE` | Number of intermediate result sets that are buffered in transit for a read query. | False | 1 |

## Mapping of Data Types

Cloud Spanner R2DBC Driver supports the following types:


| Spanner Type   | Java type           |
|----------------|---------------------|
|`BOOL`          |`java.lang.Bolean`   |
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

## Binding Query Parameters

The Cloud Spanner R2DBC driver supports named parameter binding using Cloud Spanner's [parameter syntax](https://cloud.google.com/spanner/docs/sql-best-practices).

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
Positional parameters are not supported.

The example above binds two sets of parameters to a single DML template. 
It will produce a `Publisher` (implemented by a `Flux`) containing two `SpannerResult` objects for the two instances of the statement that are executed. 

Note that calling `execute` produces R2DBC `Result` objects, but this doesn't cause the query to be run on the database. 
You must use the `map` or `getRowsUpdated` methods of the results to complete the underlying queries.

## Back Pressure

Table rows are transmitted from Cloud Spanner in fragments called `PartialResultset`.
The number of fragments for each row cannot be determined beforehand. 
While you can decide the number of rows you request from `SpannerResult`, the Cloud Spanner R2DBC driver will always request a fixed number of fragments from Cloud Spanner to fulfill your request and will do so repeatedly if necessary.

The default number of fragments per request to Cloud Spanner is 1, but this can be configured with the `partial_result_set_fetch_size` config property for your situation.
