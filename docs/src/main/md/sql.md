## Cloud SQL

Spring Framework on Google Cloud adds integrations with [Spring
JDBC](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html)
and [Spring
R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#r2dbc.core)
so you can run your MySQL or PostgreSQL databases in [Google Cloud
SQL](https://cloud.google.com/sql) using Spring JDBC and other libraries
that depend on it like Spring Data JPA or Spring Data R2DBC (only R2DBC PostgreSQL is supported).

The Cloud SQL support is provided by Spring Framework on Google Cloud in the form of two
Spring Boot starters, one for MySQL and another one for PostgreSQL. The
role of the starters is to read configuration from properties and assume
default settings so that user experience connecting to MySQL and
PostgreSQL is as simple as possible.

### JDBC Support

Maven and Gradle coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

To use MySQL:

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-sql-mysql</artifactId>
</dependency>
```

    dependencies {
    implementation("com.google.cloud:spring-cloud-gcp-starter-sql-mysql")
    }

To use PostgreSQL:

``` xml
<dependency>
<groupId>com.google.cloud</groupId>
<artifactId>spring-cloud-gcp-starter-sql-postgresql</artifactId>
</dependency>
```

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-sql-postgresql")
    }

#### Prerequisites

In order to use the Spring Boot Starters for Google Cloud SQL, the
Google Cloud SQL API must be enabled in your Google Cloud project.

To do that, go to the [API library
page](https://console.cloud.google.com/apis/library) of the Google Cloud
Console, search for "Cloud SQL API" and enable the option that is called
"Cloud SQL" .

#### Spring Boot Starter for Google Cloud SQL

The Spring Boot Starters for Google Cloud SQL provide an autoconfigured
[`DataSource`](https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html)
object. Coupled with Spring JDBC, it provides a
[`JdbcTemplate`](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-JdbcTemplate)
object bean that allows for operations such as querying and modifying a
database.

``` java
public List<Map<String, Object>> listUsers() {
    return jdbcTemplate.queryForList("SELECT * FROM user;");
}
```

You can rely on [Spring Boot data source
autoconfiguration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-connect-to-production-database)
to configure a `DataSource` bean. In other words, properties like the
SQL username, `spring.datasource.username`, and password,
`spring.datasource.password` can be used. There is also some
configuration specific to Google Cloud SQL (see "Cloud SQL Configuration
Properties" section below).

|                                       |                     |          |                                                                        |
| ------------------------------------- | ------------------- | -------- | ---------------------------------------------------------------------- |
| Property name                         | Description         | Required | Default value                                                          |
| `spring.datasource.username`          | Database username   | No       | MySQL: `root`; PostgreSQL: `postgres`                                  |
| `spring.datasource.password`          | Database password   | No       | `null`                                                                 |
| `spring.datasource.driver-class-name` | JDBC driver to use. | No       | MySQL: `com.mysql.cj.jdbc.Driver`; PostgreSQL: `org.postgresql.Driver` |

<div class="note">

If you provide your own `spring.datasource.url`, it will be ignored,
unless you disable Cloud SQL autoconfiguration with
`spring.cloud.gcp.sql.enabled=false` or
`spring.cloud.gcp.sql.jdbc.enabled=false`.

</div>

##### `DataSource` creation flow

Spring Boot starter for Google Cloud SQL registers a
`CloudSqlEnvironmentPostProcessor` that provides a correctly formatted
`spring.datasource.url` property to the environment based on the
properties mentioned above. It also provides defaults for
`spring.datasource.username` and `spring.datasource.driver-class-name`,
which can be overridden. The starter also configures credentials for the
JDBC connection based on the properties below.

The user properties and the properties provided by the
`CloudSqlEnvironmentPostProcessor` are then used by [Spring
Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html)
to create the `DataSource`. You can select the type of connection pool
(e.g., Tomcat, HikariCP, etc.) by [adding their dependency to the
classpath](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-connect-to-production-database).

Using the created `DataSource` in conjunction with Spring JDBC provides
you with a fully configured and operational `JdbcTemplate` object that
you can use to interact with your SQL database. You can connect to your
database with as little as a database and instance names.

### R2DBC Support

Maven and Gradle coordinates, using [Spring Framework on Google Cloud
BOM](getting-started.xml#bill-of-materials):

To use PostgreSQL:

``` xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>spring-cloud-gcp-starter-sql-postgres-r2dbc</artifactId>
</dependency>
```

#### Prerequisites

In order to use the Spring Boot Starters for Google Cloud SQL, the
Google Cloud SQL API must be enabled in your Google Cloud project.

To do that, go to the [API library
page](https://console.cloud.google.com/apis/library) of the Google Cloud
Console, search for "Cloud SQL API" and enable the option that is called
"Cloud SQL".

#### Spring Boot Starter for Google Cloud SQL

The Cloud SQL R2DBC starter provides a customized
`io.r2dbc.spi.ConnectionFactory` bean for connecting to Cloud SQL with
the help of the [Cloud SQL Socket
Factory](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory).
Similar to the JDBC support, you can connect to your database with as
little as a database and instance names.

A higher level convenience object
[`R2dbcEntityTemplate`](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#r2dbc.core)
is also provided for operations such as querying and modifying a
database.

``` java
@Autowired R2dbcEntityTemplate template;

public Flux<String> listUsers() {
  return template.select(User.class).all().map(user -> user.toString());
}
```

Standard R2DBC properties like the SQL username,
`spring.r2dbc.username`, and password, `spring.r2dbc.password` can be
used. There is also some configuration specific to Google Cloud SQL (see
"Cloud SQL Configuration Properties" section below).

|                         |                   |          |                                      |
| ----------------------- | ----------------- | -------- | ------------------------------------ |
| Property name           | Description       | Required | Default value                        |
| `spring.r2dbc.username` | Database username | No       | `postgres` |
| `spring.r2dbc.password` | Database password | No       | `null`                               |

<div class="note">

If you provide your own `spring.r2dbc.url`, it will be ignored, unless
you disable Cloud SQL autoconfiguration for R2DBC with
`spring.cloud.gcp.sql.enabled=false` or
`spring.cloud.gcp.sql.r2dbc.enabled=false` .

</div>

##### `ConnectionFactory` creation flow

Spring Framework on Google Cloud starter for Google Cloud SQL registers a
`R2dbcCloudSqlEnvironmentPostProcessor` that provides a correctly
formatted `spring.r2dbc.url` property to the environment based on the
properties mentioned above. It also provides a default value for
`spring.r2dbc.username`, which can be overridden. The starter also
configures credentials for the R2DBC connection based on the properties
below.

The user properties and the properties provided by the
`R2dbcCloudSqlEnvironmentPostProcessor` are then used by Spring Boot to
create the `ConnectionFactory`.

The customized `ConnectionFactory` is then ready to connect to Cloud
SQL. The rest of Spring Data R2DBC objects built on it (
`R2dbcEntityTemplate`, `DatabaseClient`) are automatically configured
and operational, ready to interact with your SQL database.

### Cloud SQL IAM database authentication

Currently, Cloud SQL only supports [IAM database authentication for
PostgreSQL](https://cloud.google.com/sql/docs/postgres/authentication).
It allows you to connect to the database using an IAM account, rather
than a predefined database username and password. You will need to do
the following to enable it:

1. In your database instance settings, turn on the
    `cloudsql.iam_authentication` flag.

2. Add the IAM user or service account to the list of database users.

3. In the application settings, set
    `spring.cloud.gcp.sql.enableIamAuth` to `true`. (Note that this will
    also set the database protocol `sslmode` to `disabled`, as it’s
    required for IAM authentication to work. However, it doesn’t
    compromise the security of the communication because the connection
    is always encrypted.)
4. Set `spring.datasource.username` to the IAM user or service account created in step 2. Note that IAM user or service account still needs to be [granted permissions](https://www.postgresql.org/docs/current/sql-grant.html) before modifying or querying the database.

### Cloud SQL Configuration Properties

|                                                 |                                                                                                                                                                                                                                                                                                                                           |          |                                                             |
| ----------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ----------------------------------------------------------- |
| Property name                                   | Description                                                                                                                                                                                                                                                                                                                               | Required | Default value                                               |
| `spring.cloud.gcp.sql.enabled`                  | Enables or disables Cloud SQL auto configuration                                                                                                                                                                                                                                                                                          | No       | `true`                                                      |
| `spring.cloud.gcp.sql.jdbc.enabled`             | Enables or disables Cloud SQL auto-configuration for JDBC                                                                                                                                                                                                                                                                                 | No       | `true`                                                      |
| `spring.cloud.gcp.sql.r2dbc.enabled`            | Enables or disables Cloud SQL auto-configuration for R2DBC                                                                                                                                                                                                                                                                                | No       | `true`                                                      |
| `spring.cloud.gcp.sql.database-name`            | Name of the database to connect to.                                                                                                                                                                                                                                                                                                       | Yes      |                                                             |
| `spring.cloud.gcp.sql.instance-connection-name` | A string containing a Google Cloud SQL instance’s project ID, region and name, each separated by a colon.                                                                                                                                                                                                                                 | Yes      | For example, `my-project-id:my-region:my-instance-name`.    |
| `spring.cloud.gcp.sql.ip-types`                 | Allows you to specify a comma delimited list of preferred IP types for connecting to a Cloud SQL instance. Left unconfigured Cloud SQL Socket Factory will default it to `PUBLIC,PRIVATE`. See [Cloud SQL Socket Factory - Specifying IP Types](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory#specifying-ip-types) | No       | `PUBLIC,PRIVATE`                                            |
| `spring.cloud.gcp.sql.credentials.location`     | File system path to the Google OAuth2 credentials private key file. Used to authenticate and authorize new connections to a Google Cloud SQL instance.                                                                                                                                                                                    | No       | Default credentials provided by the Spring Framework on Google Cloud Core Starter |
| `spring.cloud.gcp.sql.credentials.encoded-key`  | Base64-encoded contents of OAuth2 account private key in JSON format. Used to authenticate and authorize new connections to a Google Cloud SQL instance.                                                                                                                                                                                  | No       | Default credentials provided by the Spring Framework on Google Cloud Core Starter |
| `spring.cloud.gcp.sql.enableIamAuth`            | Specifies whether to enable IAM database authentication (PostgreSQL only).                                                                                                                                                                                                                                                                | No       | `False`                                                     |

### Troubleshooting tips

#### Connection issues

If you’re not able to connect to a database and see an endless loop of
`Connecting to Cloud SQL instance […​] on IP […​]`, it’s likely that
exceptions are being thrown and logged at a level lower than your
logger’s level. This may be the case with HikariCP, if your logger is
set to INFO or higher level.

To see what’s going on in the background, you should add a `logback.xml`
file to your application resources folder, that looks like this:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="org/springframework/boot/logging/logback/base.xml"/>
  <logger name="com.zaxxer.hikari.pool" level="DEBUG"/>
</configuration>
```

#### Errors like `c.g.cloud.sql.core.SslSocketFactory : Re-throwing cached exception due to attempt to refresh instance information too soon after error`

If you see a lot of errors like this in a loop and can’t connect to your
database, this is usually a symptom that something isn’t right with the
permissions of your credentials or the Google Cloud SQL API is not
enabled. Verify that the Google Cloud SQL API is enabled in the Cloud
Console and that your service account has the [necessary IAM
roles](https://cloud.google.com/sql/docs/mysql/project-access-control#roles).

To find out what’s causing the issue, you can enable DEBUG logging level
as mentioned [above](#connection-issues).

#### PostgreSQL: `java.net.SocketException: already connected` issue

We found this exception to be common if your Maven project’s parent is
`spring-boot` version `1.5.x`, or in any other circumstance that would
cause the version of the `org.postgresql:postgresql` dependency to be an
older one (e.g., `9.4.1212.jre7`).

To fix this, re-declare the dependency in its correct version. For
example, in Maven:

``` xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.1.1</version>
</dependency>
```

### Samples

Available sample applications and codelabs:

  - [Spring Framework on Google Cloud
    MySQL](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-sql-mysql-sample)

  - [Spring Framework on Google Cloud
    PostgreSQL](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-sql-postgres-sample)

  - [Spring Data JPA with Spring Framework on Google Cloud
    SQL](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-data-jpa-sample)

  - Codelab: [Spring Pet Clinic using Cloud
    SQL](https://codelabs.developers.google.com/codelabs/cloud-spring-petclinic-cloudsql/index.html)

  - [R2DBC: Spring Framework on Google Cloud
    PostgreSQL](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-sql-postgres-r2dbc-sample)
