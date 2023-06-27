# Cloud Spanner Spring Data R2DBC

The Spring Data R2DBC Dialect for Cloud Spanner enables the usage of [Spring Data R2DBC](https://github.com/spring-projects/spring-data-r2dbc) with Cloud Spanner.

The goal of the Spring Data project is to create easy and consistent ways of using data access technologies from Spring Framework applications.

## Setup

Maven coordinates, using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

```xml
<dependencies>
    <!-- The starter dependency for the Spring Data R2DBC framework. -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        <version>${r2dbc-version}</version>
    </dependency>

    <!--
        Dialect for Spring Data R2DBC Spanner; This includes the
        Cloud Spanner R2DBC driver as a transitive dependency.
    -->
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>spring-cloud-spanner-spring-data-r2dbc</artifactId>
    </dependency>
</dependencies>
```

## Overview

Spring Data R2DBC allows you to use the convenient features of Spring Data in a reactive application.
These features include:

* Spring configuration support using Java based `@Configuration` classes.
* Annotation based mapping metadata.
* Automatic implementation of Repository interfaces.
* Support for Reactive Transactions.
* Schema and data initialization utilities.

See the [Spring Data R2DBC documentation](https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference) for more information on how to use Spring Data R2DBC.

## Sample Application

We provide a [sample application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-spanner-r2dbc-samples) which demonstrates using the Spring Data R2DBC framework with Cloud Spanner in [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html).
