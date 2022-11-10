## Getting Started

This section describes how to get up to speed with Spring Cloud GCP
libraries.

### Compatibility with Spring Project Versions

Spring Cloud GCP has dependency and transitive dependencies on Spring
Projects. The table below outlines the versions of Spring Cloud, Spring
Boot and Spring Framework versions that are compatible with certain
Spring Cloud GCP version.

| Spring Cloud GCP | Spring Cloud                                                                                                          | Spring Boot  | Spring Framework |
|------------------| --------------------------------------------------------------------------------------------------------------------- |--------------| ---------------- |
| 2.x              | [2020.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2020.0-Release-Notes) (3.0/Illford) | 2.4.x, 2.5.x | 5.3.x            |
| 3.x              | [2021.0.x](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2021.0-Release-Notes) (3.1/Jubilee) | 2.6.x, 2.7.x | 5.3.x            |

### Setting up Dependencies

All Spring Cloud GCP artifacts are made available through Maven Central.
The following resources are provided to help you setup the libraries for
your project:

  - Maven Bill of Materials for dependency management

  - Starter Dependencies for depending on Spring Cloud GCP modules

You may also consult our [Github
project](https://github.com/GoogleCloudPlatform/spring-cloud-gcp) to
examine the code or build directly from source.

#### Bill of Materials

The Spring Cloud GCP Bill of Materials (BOM) contains the versions of
all the dependencies it uses.

If you’re a Maven user, adding the following to your pom.xml file will
allow you omit any Spring Cloud GCP dependency version numbers from your
configuration. Instead, the version of the BOM you’re using determines
the versions of the used dependencies.

``` xml
<dependencyManagement>
   <dependencies>
       <dependency>
           <groupId>com.google.cloud</groupId>
           <artifactId>spring-cloud-gcp-dependencies</artifactId>
           <version>{project-version}</version>
           <type>pom</type>
           <scope>import</scope>
       </dependency>
   </dependencies>
</dependencyManagement>
```

Or, if you’re a Gradle user:

    dependencies {
        implementation platform("com.google.cloud:spring-cloud-gcp-dependencies:{project-version}")
    }


In the following sections, it will be assumed you are using the Spring
Cloud GCP BOM and the dependency snippets will not contain versions.

#### Starter Dependencies

Spring Cloud GCP offers [starter
dependencies](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-starters)
through Maven to easily depend on different modules of the library. Each
starter contains all the dependencies and transitive dependencies needed
to begin using their corresponding Spring Cloud GCP module.

For example, if you wish to write a Spring application with Cloud
Pub/Sub, you would include the `spring-cloud-gcp-starter-pubsub`
dependency in your project. You do **not** need to include the
underlying `spring-cloud-gcp-pubsub` dependency, because the `starter`
dependency includes it.

A summary of these artifacts are provided below.

| Spring Cloud GCP Starter | Description                                                                       | Maven Artifact Name                                                                                    |
| ------------------------ | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| Core                     | Automatically configure authentication and Google project settings                | [com.google.cloud:spring-cloud-gcp-starter](core.xml#spring-cloud-gcp-core)                            |
| Cloud Spanner            | Provides integrations with Google Cloud Spanner                                   | [com.google.cloud:spring-cloud-gcp-starter-data-spanner](spanner.xml#spring-data-cloud-spanner)        |
| Cloud Datastore          | Provides integrations with Google Cloud Datastore                                 | [com.google.cloud:spring-cloud-gcp-starter-data-datastore](datastore.xml#spring-data-cloud-datastore)  |
| Cloud Pub/Sub            | Provides integrations with Google Cloud Pub/Sub                                   | [com.google.cloud:spring-cloud-gcp-starter-pubsub](pubsub.xml#cloud-pubsub)                            |
| Logging                  | Enables Cloud Logging                                                             | [com.google.cloud:spring-cloud-gcp-starter-logging](logging.xml#cloud-logging)                         |
| SQL - MySQL              | Cloud SQL integrations with MySQL                                                 | [com.google.cloud:spring-cloud-gcp-starter-sql-mysql](sql.xml#cloud-sql)                               |
| SQL - PostgreSQL         | Cloud SQL integrations with PostgreSQL                                            | [com.google.cloud:spring-cloud-gcp-starter-sql-postgresql](sql.xml#cloud-sql)                          |
| Storage                  | Provides integrations with Google Cloud Storage and Spring Resource               | [com.google.cloud:spring-cloud-gcp-starter-storage](storage.xml#cloud-storage)                         |
| Config                   | Enables usage of Google Runtime Configuration API as a Spring Cloud Config server | [com.google.cloud:spring-cloud-gcp-starter-config](config.xml#cloud-runtime-configuration-api)         |
| Trace                    | Enables instrumentation with Google Cloud Trace                                   | [com.google.cloud:spring-cloud-gcp-starter-trace](trace.xml#cloud-trace)                               |
| Vision                   | Provides integrations with Google Cloud Vision                                    | [com.google.cloud:spring-cloud-gcp-starter-vision](vision.xml#cloud-vision)                            |
| Security - IAP           | Provides a security layer over applications deployed to Google Cloud              | [com.google.cloud:spring-cloud-gcp-starter-security-iap](security-iap.xml#cloud-iap)                   |
| Security - Firebase      | Provides a security layer over applications deployed to Firebase                  | [com.google.cloud:spring-cloud-gcp-starter-security-firebase](security-firebase.xml#security-firebase) |

#### Spring Initializr

[Spring Initializr](https://start.spring.io/) is a tool which generates
the scaffolding code for a new Spring Boot project. It handles the work
of generating the Maven or Gradle build file so you do not have to
manually add the dependencies yourself.

Spring Initializr offers three modules from Spring Cloud GCP that you
can use to generate your project.

  - **GCP Support**: The GCP Support module contains auto-configuration
    support for every Spring Cloud GCP integration. Most of the
    autoconfiguration code is only enabled if the required dependency is
    added to your project.

  - **GCP Messaging**: Google Cloud Pub/Sub integrations work out of the
    box.

  - **GCP Storage**: Google Cloud Storage integrations work out of the
    box.

### Learning Spring Cloud GCP

There are a variety of resources to help you learn how to use Spring
Cloud GCP libraries.

#### Sample Applications

The easiest way to learn how to use Spring Cloud GCP is to consult the
[sample applications on
Github](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples).
Spring Cloud GCP provides sample applications which demonstrate how to
use every integration in the library. The table below highlights several
samples of the most commonly used integrations in Spring Cloud GCP.

| GCP Integration | Sample Application                                                                                                                                                                                                                                                                                                                                                                           |
|----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Cloud Pub/Sub  | [spring-cloud-gcp-pubsub-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-pubsub-sample)                                                                                                                                                                                                                                  |
| Cloud Spanner  | [spring-cloud-gcp-data-spanner-repository-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-data-spanner-repository-sample)  <br/> [spring-cloud-gcp-data-spanner-template-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-data-spanner-template-sample) |
| Datastore      | [spring-cloud-gcp-data-datastore-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-data-datastore-sample)                                                                                                                                                                                                                  |
| Cloud SQL (w/ MySQL) | [spring-cloud-gcp-sql-mysql-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-sql-mysql-sample)                                                                                                                                                                                                                            |
| Cloud Storage | [spring-cloud-gcp-storage-resource-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-storage-resource-sample)                                                                                                                                                                                                              |
| Cloud Logging | [spring-cloud-gcp-logging-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-logging-sample)                                                                                                                                                                                                                                |
| Trace | [spring-cloud-gcp-trace-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-trace-sample)                                                                                                                                                                                                                                    |
| Cloud Vision | [spring-cloud-gcp-vision-api-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-vision-api-sample)                                                                                                                                                                                                                          |
| Cloud Security - IAP | [spring-cloud-gcp-security-iap-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-security-iap-sample)                                                                                                                                                                                                                      |
| Cloud Security - Firebase | [spring-cloud-gcp-security-firebase-sample](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-security-firebase-sample)                                                                                                                                                                                                            |



Each sample application demonstrates how to use Spring Cloud GCP
libraries in context and how to setup the dependencies for the project.
The applications are fully functional and can be deployed to Google
Cloud Platform as well. If you are interested, you may consult guides
for [deploying an application to
AppEngine](https://codelabs.developers.google.com/codelabs/cloud-app-engine-springboot/index.html)
and [to Google Kubernetes
Engine](https://codelabs.developers.google.com/codelabs/cloud-springboot-kubernetes/index.html).



#### Codelabs

For a more hands-on approach, there are several guides and codelabs to
help you get up to speed. These guides provide step-by-step instructions
for building an application using Spring Cloud GCP.

Some examples include:

  - [Deploy a Spring Boot app to App
    Engine](https://codelabs.developers.google.com/codelabs/cloud-app-engine-springboot/index.html)

  - [Build a Kotlin Spring Boot app with Cloud SQL and Cloud
    Pub/Sub](https://codelabs.developers.google.com/codelabs/cloud-spring-cloud-gcp-kotlin/index.html)

  - [Build a Spring Boot application with
    Datastore](https://codelabs.developers.google.com/codelabs/cloud-spring-datastore/index.html)

  - [Messaging with Spring Integration and Cloud
    Pub/Sub](https://codelabs.developers.google.com/codelabs/cloud-spring-cloud-gcp-pubsub-integration/index.html)

The full collection of Spring codelabs can be found on the [Google
Developer Codelabs page](https://codelabs.developers.google.com/spring).
