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
| `project`   | Your GCP Project ID        | True (if `url` not provided) | |
| `instance`  | Your Spanner Instance name | True (if `url` not provided) | |
| `database`  | Your Spanner Database name | True (if `url` not provided) | |
| `url`       | A Cloud Spanner R2DBC URL specifying your Spanner database. An alternative to specifying `project`, `instance`, and `database` separately. | False |
| `google_credentials` | Optional [Google credentials](https://cloud.google.com/docs/authentication/production) override to specify for your Google Cloud account. | False | If not provided, credentials will be [inferred from your runtime environment](https://cloud.google.com/docs/authentication/production#finding_credentials_automatically).
| `partial_result_set_fetch_size` | Number of intermediate result sets that are buffered in transit for a read query. | False | 1 |
| `ddl_operation_timeout` | Duration in seconds to wait for a DDL operation to complete before timing out | False | 600 seconds |
| `ddl_operation_poll_interval` | Duration in seconds to wait between each polling request for the completion of a DDL operation | False | 5 seconds |

### Connection URLs

You may specify the coordinates of your Cloud Spanner database using the `url` property instead of
specifying the `project`, `instance`, and `database` properties separately.

A Cloud Spanner R2DBC URL is constructed using the following format:

```
r2dbc:spanner://spanner.googleapis.com:443/projects/${PROJECT_NAME}/instances/${INSTANCE_NAME}/databases/${DB_NAME}
```

- `${PROJECT_NAME}`: Replace with the name of your Google Cloud Platform Project ID.
- `${INSTANCE_NAME}`: Replace with the name of your Spanner Instance.
- `${DB_NAME}`: Replace with the name of your Spanner database.

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

## Connections

The R2DBC Cloud Spanner `Connection` object represents a persistent connection to a Spanner
database.

When you instantiate a `Connection` object using the `ConnectionFactory`, a [Spanner session](https://cloud.google.com/spanner/docs/sessions)
is created and encapsulated within the connection. Creating a session is typically expensive, so
it is preferable to reuse your `Connection` object to run multiple statements rather than create
a new `Connection` for each statement you wish to run.

Additionally, if a `Connection` is not used for more than 1 hour, the Cloud Spanner database service
reserves the right to drop the connection. If this occurs, a `R2dbcNonTransientException` will be
thrown when you attempt to run queries using the connection, and you will have to recreate the
connection in order to reattempt the query.

If you definitely need to keep an idle connection alive, for example, if a significant near-term
increase in database use is expected, then you can prevent the connection from being dropped.
Perform an inexpensive operation such as executing the SQL query `SELECT 1` to keep the
connection alive.

## Transactions

In Cloud Spanner, a transaction represents a set of read and write statements that execute
atomically at a single logical point in time across columns, rows, and tables in a database.

Note: Transactional save points are unsupported in Cloud Spanner and are unimplemented by
this R2DBC driver.

### Transaction Types

Spanner offers [three transaction types](https://cloud.google.com/spanner/docs/transactions)
in which to execute SQL statements:

- Read-Write: Supports reading and writing data into Cloud Spanner.

- Read-Only: Provides guaranteed consistency across multiple reads but does not allow writing data.

- Partitioned DML: A transaction designed for bulk updates and deletes with certain restrictions.
    See the [Partitioned DML documentation](https://cloud.google.com/spanner/docs/dml-partitioned)
    for more information.

When you begin a transaction in the `Connection` object using `connection.beginTransaction()`,
a read-write transaction is started.

If you would like to begin a transaction and leverage the custom transaction types, you will have
to cast the `Connection` object into `SpannerConnection` and call 
`spannerConnection.beginTransaction(TransactionOptions options)`. The overloaded `beginTransaction`
allows you to pass in custom `TransactionOptions` to customize your transaction.

The below example demonstrates how this might be done using Project Reactor:

```java
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;

ConnectionFactory connectionFactory =
    ConnectionFactories.get(ConnectionFactoryOptions.builder()
        .option(DRIVER, "spanner")
        .option(PROJECT, "your-gcp-project-id")
        .option(INSTANCE, "your-spanner-instance")
        .option(DATABASE, "your-database-name")
        .build());

// Your TransactionOptions to customize the transaction type.
TransactionOptions transactionOptions =
    TransactionOptions.newBuilder()
        .setReadOnly(
            ReadOnly.newBuilder().setStrong(true))
        .build();
        
// Create and cast the Connection to SpannerConnection.
Mono<SpannerConnection> spannerConnection =
    Mono.from(this.connectionFactory.create())
        .cast(SpannerConnection.class);

// Call beginTransaction and pass in the transactionOptions.
spannerConnection
    .delayUntil(connection -> connection.beginTransaction(transactionOptions));
    ... continued ...
```

See the [TransactionOptions documentation](https://cloud.google.com/spanner/docs/reference/rpc/google.spanner.v1#google.spanner.v1.TransactionOptions)
for more information about all of the transaction type settings that are available.

### Autocommit Mode

The Spanner R2DBC driver can be used in autocommit mode in which statements are executed
independently outside of a transaction.

You may immediately call `connection.createStatement(sql)` and begin executing SQL statements.
Each statement will be executed as an independent unit of work.

- DML statements are executed in a stand-alone read-write transaction.
- Read queries are executed in a strongly consistent, read-only temporary transaction.

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

When you execute a **read** query in Cloud Spanner, the table rows of the result are transmitted
back to the client in chunks called `PartialResultSets`. Every `PartialResultSet` object may
contain any number of complete (or incomplete) table rows, and the number of rows cannot be
determined beforehand. Consequently, the driver requests these `PartialResultSet` objects from
upstream and holds a buffer of these objects from which the table rows of the results are extracted.
As the buffer is depleted, more `PartialResultSets` will be streamed from Cloud Spanner and
will replenish the buffer.

In order to support backpressure under these conditions, the driver provides a setting
`partial_result_set_fetch_size` which specifies how many PartialResultSet objects the driver
requests from Spanner in it's first request to store in the buffer. This number is an upper
bound that affects how it is replenished; when the client has consumed 25% of the PartialResultSets,
it will request 25% upstream from Spanner. See the [prefetch documentation](https://projectreactor.io/docs/core/release/reference/#_operators_changing_the_demand_from_downstream)
for more info.

The default buffer size of `PartialResultSets` is 1 and this may be increased by setting
`partial_result_set_fetch_size`.

## Exception Handling

The Cloud Spanner R2DBC propagates all exceptions down to the user. All exceptions thrown
are wrapped by and propagated through two exception classes:

- `R2dbcTransientException`: Errors caused by network problems or causes outside of the
    user's control. The operations that fail due to these errors can be retried.
    
- `R2dbcNonTransientException`: Errors caused by invalid operations or user error.
    These include SQL syntax errors, invalid requests, performing invalid operations on the
    Spanner driver, etc. These errors should not be retried.
    
The user may leverage reactive methods to retry operations which throw `R2dbcTransientException`.

Example using Project Reactor's [`Retry` utilities](https://projectreactor.io/docs/extra/snapshot/api/overview-summary.html):

```java
// This describes a retry strategy which only attempts a retry if the exception class
// matches R2dbcTransientException.class
Retry retry =
    Retry.anyOf(R2dbcTransientException.class)
        .randomBackoff(Duration.ofMillis(100), Duration.ofSeconds(60))
        .retryMax(5);

Mono.from(connection
    .createStatement("Select * from table")
    .execute())
    .retryWhen(retry); // This retries the subscription using the retry strategy.
```

## Batches
A batch contains multiple statements that are executed in one remote call for performance reasons.
Only DML statements are supported.

The call to `execute()` produces a publisher that will publish results.
The statements are executed in sequential order.
For every successfully executed statement, there will be a result that contatins a number of updated rows.
Execution stops after the first failed statement; the remaining statements are not executed. 
 
```java
Flux.from(connection.createBatch()
    .add("INSERT INTO books VALUES('Mark Twain', 'The Adventures of Tom Sawyer'")
    .add("INSERT INTO books VALUES('Mark Twain', 'Adventures of Huckleberry Finn'")
    .execute())
    .flatMap(r -> r.getRowsUpdated());
```
