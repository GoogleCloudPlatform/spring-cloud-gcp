# Cloud Spanner R2DBC Driver

This project contains:
* An implementation of Java Reactive Relational Database Connectivity SPI [R2DBC](https://r2dbc.io/) for [Cloud Spanner](https://cloud.google.com/spanner/) based on the Cloud Spanner [client library](https://github.com/googleapis/java-spanner).
* A [Spring Data R2DBC dialect for Cloud Spanner](https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/tree/main/cloud-spanner-spring-data-r2dbc).
* [Sample applications](https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/tree/main/cloud-spanner-r2dbc-samples) to help you get started.

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

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>cloud-spanner-r2dbc</artifactId>
  <version>1.2.0</version>
</dependency>
```

**Gradle Coordinates**

```
dependencies {
  compile group: 'com.google.cloud', name: 'cloud-spanner-r2dbc', version: '1.2.0'
}
```

### Usage

After setting up the dependency and [authentication](#authentication), you can begin directly using the driver.

The rest of this documentation will show examples of directly using the driver.
In a real application, you should use one of R2DBC's user-friendly [client APIs](https://r2dbc.io/clients/) instead.

To start using Cloud Spanner R2DBC driver, configure the R2DBC connection factory either programmatically, as shown below, or with a URL.

```
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;

ConnectionFactory connectionFactory =
    ConnectionFactories.get(ConnectionFactoryOptions.builder()
        .option(DRIVER, "cloudspanner")
        .option(PROJECT, "your-gcp-project-id")
        .option(INSTANCE, "your-spanner-instance")
        .option(DATABASE, "your-database-name")
        .build());
        
// The R2DBC connection may now be created.
Publisher<? extends Connection> connectionPublisher = connectionFactory.create();
```

### Connection URLs

You may specify the coordinates of your Cloud Spanner database using the `ConnectionFactories.get(String)` SPI method instead of specifying the `project`, `instance`, and `database` properties individually.

A Cloud Spanner R2DBC URL is constructed in the following format:

```
r2dbc:cloudspanner://spanner.googleapis.com:443/projects/${PROJECT_NAME}/instances/${INSTANCE_NAME}/databases/${DB_NAME}
```

- `${PROJECT_NAME}`: Replace with the name of your Google Cloud Platform Project ID.
- `${INSTANCE_NAME}`: Replace with the name of your Spanner Instance.
- `${DB_NAME}`: Replace with the name of your Spanner database.

## Cleaning Up

Client library-based `ConnectionFactory` must be closed as part of application shutdown process to ensure all server-side Cloud Spanner sessions are cleaned up.

```
Mono.from(((Closeable) connectionFactory).close()).subscribe();
```

### Authentication

The driver allows the following options for authentication:

* a `String` property `credentials` containing the local file location of the JSON credentials file.
* a `String` OAuth token provided as `oauthToken`.
* a `Credentials` object provided as `google_credentials`. This will only work with programmatically constructed `ConnectionFactoryOptions`.
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

In the absence of explicit authentication options, Application Default Credentials will be automatically inferred from the environment in which the application is running, unless the connection is in plain-text, indicating the use of Cloud Spanner emulator.
For more information, see the [Google Cloud Platform Authentication documentation](https://cloud.google.com/docs/authentication/production#automatically)


#### Using Google Cloud SDK

Google Cloud SDK is a command line interface for Google Cloud Platform products and services.
This is a convenient way of setting up authentication during local development.

If you are using the SDK, the driver can automatically infer your account credentials from your SDK configuration.

Instructions:

1. Install the [Google Cloud SDK](https://cloud.google.com/sdk/) for command line and follow the [Cloud SDK quickstart](https://cloud.google.com/sdk/docs/quickstarts) for your operating system.
    
2. Once setup, run `gcloud auth application-default login` and login with your Google account credentials. 

After completing the SDK configuration, the Cloud Spanner R2DBC driver will automatically pick up your credentials. 

#### Using a Service Account

A [Google Service Account](https://cloud.google.com/iam/docs/understanding-service-accounts) is a special type of Google Account intended to represent a non-human user that needs to authenticate and be authorized to access your Google Cloud resources.
Each service account has an account key JSON file that you can use to provide credentials to your application.

You can learn how to create a service account and authenticate your application by following
[these instructions](https://cloud.google.com/docs/authentication/production#obtaining_and_providing_service_account_credentials_manually).


## Supported connection options

All connection options of primitive and String type can be passed through the connection URL in the `?key1=value1&key2=value2` format.
Object-typed options can only be passed in programmatically.

|Property name            |Type                              |Allowed in URL connection |Default |Comments|
|-------------------------|-------|--------------------------|--------|--------|
|`credentials`            |String |Yes                       |null    |The location of the credentials file to use for this connection
|`oauthToken`             |String |Yes                       |null    |A valid pre-existing OAuth token to use for authentication
|`google_credentials`     |com.google.auth.oauth2.OAuth2Credentials|No|null|A pre-authenticated authentication object that can only be supplied with programmatic connection options
|`usePlainText`           |boolean|Yes                       |false   |Turns off SSL and credentials use (only valid when using Cloud Spanner emulator)
|`optimizerVersion`       |String |Yes                       |null    |Determines version of Cloud Spanner https://cloud.google.com/spanner/docs/query-optimizer/query-optimizer-versions[optimizer] to use in queries
|`autocommit`             |boolean|Yes                       |true    |Whether new connections are created in autocommit mode
|`readonly`               |boolean|Yes                       |false   |Whether new connections start with a read-only transaction

## Mapping of Data Types

Cloud Spanner R2DBC Driver supports the following types:


| Spanner Type   | Java type           |
|----------------|---------------------|
|`BOOL`          |`java.lang.Boolean`  |
|`BYTES`         |`java.nio.ByteBuffer`|
|`DATE`          |`com.google.cloud.Date`|
|`FLOAT64`       |`java.lang.Double`   |
|`INT64`         |`java.lang.Long`     |
|`INT64`         |`java.lang.Integer`  |
|`STRING`        |`java.lang.String`   |
|`JSON`          |`com.google.cloud.spanner.r2dbc.v2.JsonWrapper`   |
|`TIMESTAMP`     |`com.google.cloud.Timestamp` |
|`ARRAY`         |Arrays or `Iterable` collections with hint. `ARRAY<JSON>` is not supported.|

Null values mapping is supported in both directions.
See [Cloud Spanner documentation](https://cloud.google.com/spanner/docs/data-types) to learn more about Spanner types.

### TIMESTAMP and DATE Mapping

`TIMESTAMP` and `DATE` Spanner column types are supported via `com.google.cloud.Timestamp` and `com.google.cloud.Date` classes.

Custom converters need to be implemented and registered if you want to use other Time/Date classes.
For examples, please refer to the following integration test: [SpannerR2dbcDialectDateTimeBindingIntegrationTest.java](https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/blob/main/cloud-spanner-spring-data-r2dbc/src/test/java/com/google/cloud/spanner/r2dbc/springdata/it/SpannerR2dbcDialectDateTimeBindingIntegrationTest.java)

### JSON Mapping

`JSON` Spanner type is supported through `JsonWrapper.class`. This is a wrapper class around String representation of the Json value. Below are the basic usages wrapping and un-wrapping string: 
```java
  // Create jsonWrapper object from String
  JsonWrapper jsonWrapper = JsonWrapper.of(jsonString);
  JsonWrapper jsonWrapper = new JsonWrapper(jsonString);

  // Get underlying string from jsonWrapper object
  String jsonString = jsonWrapper.toString();
```

If using Spring Data, default converters to/from `Map` are ready to use out-of-box for key or value type of `String`, `Boolean` and `Double`. Custom converters can be used to allow Json conversion directly to/from collections or user-defined types. Examples of using `Map` and custom class `Review` for Json field are provided in the [Spring Data sample application](https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/tree/main/cloud-spanner-r2dbc-samples/cloud-spanner-spring-data-r2dbc-sample)

### Array Mapping

Cloud Spanner arrays can be mapped to/from either primitive Java arrays or `Iterable` collections of wrapper types. For example, a column of type `ARRAY<INT64>` can be represented as `long[]` or `List<Long>`.

However, binding `Iterable` parameters requires a `SpannerType` hint for the specific `com.google.cloud.spanner.Type` to use.

```
  List value = ...;
  SpannerType typeHint = SpannerType.of( Type.array(Type.string()) );
  statement.bind("columnName", Parameters.in(typeHint, value));
```
This is not a concern when using Spring Data, as collections will automatically be converted to typed arrays by the framework.

NOTE: Using `long` and `double` arrays is more efficient than using `int` and `float`, as the latter need to get converted for every element.

## Connections

The R2DBC Cloud Spanner `Connection` object is a lightweight wrapper around the shared Cloud Spanner client library object combined with transaction state.

The client library takes care of reconnecting lapsed Cloud Spanner sessions.

If you'd like to ensure the current connection stays connected, you may keep a connection active by calling `validate(ValidationDepth.REMOTE)` on the `Connection` object and subscribing to the returned `Publisher`.
Remote validation performs an inexpensive SQL query `SELECT 1` against the database. 

## Transactions

In Cloud Spanner, a transaction represents a set of read and write statements that execute atomically at a single logical point in time across columns, rows, and tables in a database.

Note: Transactional save points are unsupported in Cloud Spanner and are unimplemented by this R2DBC driver.

### Transaction Types

Spanner offers [three transaction types](https://cloud.google.com/spanner/docs/transactions) in which to execute SQL statements:

- Read-Write: Supports reading and writing data into Cloud Spanner.
    When you begin a transaction in the `Connection` object using `connection.beginTransaction()`, a read-write transaction is started by default, unless the connection was created or altered to run in read-only mode.
    
    ```java
    Mono.from(connectionFactory.create())
                .flatMapMany(c -> Flux.concat(
                    c.beginTransaction(),
                    ...
                    c.commitTransaction(),
                    c.close()))
    ```

- Read-Only: Provides guaranteed consistency across multiple reads but does not allow writing data.
    Read-only transactions, including stale transactions, can be used by downcasting the `Connection` object to `com.google.cloud.spanner.r2dbc.api.SpannerConnection` and calling `beginReadonlyTransaction()` on it.
    Invoking `beginReadonlyTransaction()` without parameters will begin a new strongly consistent readonly transaction, as does creating a new connection from a `ConnectionFactory` in read-only mode (`readonly=true`).
    
    To customize staleness, pass in a `TimestampBound` parameter.
    See the [TransactionOptions documentation](https://cloud.google.com/spanner/docs/reference/rpc/google.spanner.v1#google.spanner.v1.TransactionOptions) for more information about all of the transaction type settings that are available.

    ```java
    Mono.from(connectionFactory.create())
                .flatMapMany(c ->
                    Flux.concat(
                              ((SpannerConnection) conn).beginReadonlyTransaction(TimestampBound.ofExactStaleness(1, TimeUnit.SECONDS)),
                                ...
                              conn.commitTransaction(),
                        )
    ```
    NOTE: Readonly transactions must be closed by calling `commit()` before starting a new read-write or a read-only transaction.


- Partitioned DML: A transaction designed for bulk updates and deletes with certain restrictions.
    See the [Partitioned DML documentation](https://cloud.google.com/spanner/docs/dml-partitioned) for more information.
    This driver does not support Partitioned DML transactions at the time.


### Nesting transactions
Cloud Spanner does not support nested transactions, so each transaction must be either committed or rolled back.
For readonly transactions, either committing or rolling back will result in closing of the readonly transaction.


### Autocommit Mode

The Spanner R2DBC driver can be used in autocommit mode in which statements are executed independently outside of a transaction.

You may immediately call `connection.createStatement(sql)` and begin executing SQL statements.
Each statement will be executed as an independent unit of work.

- DML statements are executed in a stand-alone read-write transaction.
- Read queries are executed in a strongly consistent, read-only temporary transaction.

## Statements 

R2DBC statement objects are used to run statements on your Cloud Spanner database. 
The table below describes whether parameter bindings are available for each statement type.

| Statement Type | Allows Parameter Bindings |
|----------------|---------------------------|
| SELECT Queries | Yes                       | 
| DML Statements | Yes                       |
| DDL Statements | No                        |

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


## Backpressure

Backpressure on SQL SELECT queries is supported out of the box.
Take care to always ultimately exhaust or cancel the query result `Publisher`, since not doing so may lead to objects not being deallocated properly.

## Exception Handling

The Cloud Spanner R2DBC propagates all exceptions down to the user.
All exceptions thrown are wrapped by and propagated through two exception classes:

- `R2dbcTransientException`: Errors caused by network problems or causes outside of the user's control.
    The operations that fail due to these errors can be retried.
    
- `R2dbcNonTransientException`: Errors caused by invalid operations or user error.
    These include SQL syntax errors, invalid requests, performing invalid operations on the Spanner driver, etc.
    These errors should not be retried.
    
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
For every successfully executed statement, there will be a result that contains a number of updated rows.
 
```java
Flux.from(connection.createBatch()
    .add("INSERT INTO books VALUES('Mark Twain', 'The Adventures of Tom Sawyer'")
    .add("INSERT INTO books VALUES('Mark Twain', 'Adventures of Huckleberry Finn'")
    .execute())
    .flatMap(r -> r.getRowsUpdated());
```

## Using Connection Pool

Client Spanner client library maintains its own low-level connection pool, making use of [r2dbc pool](https://github.com/r2dbc/r2dbc-pool) unnecessary.
When R2DBC connections are closed, the underlying Client Spanner connection is reused internally.
