# Spring Cloud GCP Changelog

For 2.x changelog, go to [this
page](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/blob/2.x/CHANGELOG.adoc).

[Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) is a set
of integrations between Spring Framework and Google Cloud Platform. It
makes it much easier for Spring framework users to run their
applications on Google Cloud Platform.

This document provides a high-level overview of the changes introduced
in Spring Cloud GCP by release. For a detailed view of what has changed,
refer to the [commit
history](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commits/main)
on GitHub.

## 3.5.0-SNAPSHOT

## 3.4.0

### General

This release officially introduces Spring Boot 2.7 compatibility. Note that the previous releases of Spring Cloud GCP 3.x are also compatible with Spring Boot 2.7. The one exception is that if you use Cloud SQL with R2DBC, you'd have to manage the driver versions in your own application dependencies (see [refdoc](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/blob/main/docs/src/main/asciidoc/sql.adoc#r2dbc-support) for details).

### Important version upgrades
* Upgrade to support Spring Boot 2.7 ([#1185](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1185))
* Bump spring-cloud-dependencies to 2021.0.3 ([#1149](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1149))
* Bump libraries-bom to 26.1.3 ([#1282](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1282))
* Bump cloud-sql-socket-factory.version to 1.7.0 ([#1261](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1261))

### BigQuery
* BigQuery Storage Write API integration ([#1219](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1219))
* Make CreateDisposition configurable on BigQueryTemplate ([#1286](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1286))

## Cloud SQL
* Add version management for the older MySQL and Postgres R2DBC drivers ([#1185](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1185)).

## KMS
* Support KMS-specific credentials with fallback to global project credentials ([#1272](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1272))

## Pub/Sub
* Subscriber thread name customization ([#1152](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1152))
* Allow Publishers shutdown gracefully ([#1260](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1260))
* Support min/max duration per ack extension ([#1254](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1254))

## Secret Manager
* Support for `spring.config.import` property source discovery ([#1204](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1204))
* Support default values for non-existent secrets ([#1246](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1246)).

## Spanner
* Fix edge case with `null` value in a simple field ([#1208](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1208))
* Support `ARRAY<JSON>` type ([#1157](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1157))
* Fix `IsNotNull` conditions ([#1171](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1171))
* Removed duplicate `Gson` bean from autoconfiguration ([#1241](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1241))


## 3.3.0

### General

  - Bump spring-cloud-dependencies to 2021.0.2
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1109>)

  - Bump spring-boot-dependencies from 2.6.7 to 2.6.8
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1130>)

  - Bump cloud-sql-socket-factory.version from 1.5.0 to 1.6.0
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1111>)

  - Bump libraries-bom from 25.2.0 to 25.3.0
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1123>)

### Pub/Sub

  - Allow customizing Pub/Sub Spring Cloud Stream header mapping
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1038>)

  - Fully qualified subscription configuration support + immutable
    configuration in Pub/Sub
    (<https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1110>)

## 3.2.1

### General

  - Spring Cloud GCP BOM (`spring-cloud-gcp-dependencies`) overrides
    `spring-cloud-function` dependencies to version `3.2.3` to address
    \[CVE-2022-22963\](<https://tanzu.vmware.com/security/cve-2022-22963>)
    (\#1059).

  - Updated `cloud-sql-socket-factory.version` to 1.5.0 (\#1053)

## 3.2.0

### General

  - Version updates:

  - Spring Boot to 2.6.6 (transitively, Spring Framework 5.3.18). See
    Spring \[blog
    post\](<https://spring.io/blog/2022/03/31/spring-boot-2-5-12-available-now>)
    for details.

  - `guava-bom` to 31.1-jre (\#968).

  - `cloud-sql-socket-factory.version` to 1.4.4 (\#971).

  - `gcp-libraries-bom.version` to 25.0.0 (\#999)

  - Overrode \<url\> field in maven POM files to point to the same root
    URL, preventing maven from generating invalid URLs by concatenating
    root URL with module name (\#1007).

### Cloud SQL

  - R2DBC autoconfiguration now allows external credentials to be
    provided to Cloud SQL (\#775).

### Pub/Sub

  - Removed a forced startup-time validation for Pub/Sub Actuator Health
    Indicator that could prevent application startup \[\#1018\].

### Spanner

  - Fixed a spec bug for `SimpleSpannerRepository.findAllById()`: on an
    empty `Iterable` input, it used to return all rows. New behavior is
    to return empty output on an empty input. ⚠ behavior change (\#934)

  - Allow user override of Gson object used for JSON field conversion
    (\#937).

  - Allowed `Pageable` parameter appear in any position in query method
    argument list (\#958).

## 3.1.0

### Cloud SQL

  - Added starters for accessing Cloud SQL with Spring Data R2DBC
    ([\#772](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/772)):

  - `spring-cloud-gcp-starter-sql-mysql-r2dbc` for MySQL

  - `spring-cloud-gcp-starter-sql-postgres-r2dbc` for PostgreSQL

  - Added property `spring.cloud.gcp.sql.jdbc.enabled` to turn off JDBC
    Cloud SQL autoconfiguration
    ([\#903](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/903))

### Pub/Sub

  - Added ability to change `Publisher.Builder` settings prior to
    `Publisher` object being constructed by providing
    `PublisherCustomizer` beans
    ([\#900](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/900)).

### Spanner

  - Fixed session leak in Spanner actuator healthcheck
    ([\#902](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/902)).

## 3.0.0

**This release introduces Spring Boot 2.6 and Spring Cloud 2021.0
Compatibility.**

### General

  - Updated `gcp-libraries-bom.version` to 24.2.0
    ([\#861](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/861)).

### Datastore

  - `SimpleDatastoreRepository` now supports `findBy()` with fluent
    query semantics
    ([\#836](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/836))

### Logging

  - Renamed methods
    ([\#865](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/865))
    ⚠️ **breaking change**
    
      - Renamed `XCloudTraceIdExtractor` to `CloudTraceIdExtractor`
    
      - Renamed methods in `StackdriverJsonLayout`. Make sure to update
        Logback XML configuration with custom layout and rename
        `traceIdMDCField` and `spanIdMDCField` to `traceIdMdcField` and
        `spanIdMdcField` respectively.
    
      - getTraceIdMDCField() → getTraceIdMdcField()
    
      - setTraceIdMDCField() → setTraceIdMdcField()
    
      - getSpanIdMDCField() → getSpanIdMdcField()
    
      - setSpanIdMDCField() → setSpanIdMdcField().

### Pub/Sub

  - Spring Integration and Spring Cloud Stream outgoing adapters will
    now exclude headers with `googclient_` prefix from being propagated
    to Cloud Pub/Sub
    ([\#845](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/845)).

### Spanner

  - Reduced visibility and renamed `SpannerQueryMethod.getMethod()`
    ([/\#815](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull815))
    ⚠️ **breaking change**

## 2.0.7

This is a maintenance release upgrading dependency versions.

### General

  - Switched to explicitly defining Spring Boot version as 2.5.x train
    ([\#804](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/804)).

  - Upgraded Spring Boot dependencies to 2.5.8, which includes log4j-api
    upgrade to 2.17.0
    ([\#812](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/812)).

  - Updated `gcp-libraries-bom.version` to 24.1.1
    ([\#816](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/816)).

  - Updated `cloud-sql-socket-factory.version` to 1.4.1
    ([\#773](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/773))

## 2.0.6

### General

  - Updated `gcp-libraries-bom.version` to 24.0.0.

### Datastore

  - Added Blob to byte\[\] conversion on read
    ([\#729](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/729)).

  - Removed unused array input handling logic in `TwoStepsConversions`
    ([\#733](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/733)).

### Logging

  - Fixed potential NPE for a null message in `StackdriverJsonLayout`
    ([\#694](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/694)).

### Pub/Sub

  - Added support for per-subscription configurations for Subscriber
    settings.
    ([\#418](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/418)).
    
      - A global custom bean for a setting takes precedence over any
        property-based auto-configuration. In order to use
        per-subscription configuration for a Subscriber setting, the
        custom bean for that setting needs to be removed. When using
        auto-configuration, per-subscription configuration takes
        precedence over global configuration.

  - Added a health indicator validating for each subscription that there
    was a recent successfully processed message or that the backlog is
    under threshold.
    ([\#613](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/613)).

### Spanner

  - Added Spanner health indicator
    ([\#643](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/643)).

### Trace

  - Exposed `spring.cloud.gcp.trace.server-response-timeout-ms` property
    ([\#698](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/698)).

Thanks to our community contributors: @gkatzioura, @ikeyat, @mirehasfun
and @mvpzone\!

## 2.0.5 (2021-10-25)

### Pub/Sub

  - Fixed: Allow overriding Pub/Sub retryableCodes in pull settings
    ([\#670](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/670)).

## 2.0.4 (2021-08-11)

### General

  - Updated `gcp-libraries-bom.version` to 20.9.0.

### Datastore

  - Added support for `Stream` return type in both GQL and method
    name-based queries
    ([\#551](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/551)).

  - Made `DatastorePageable` compatible with Spring Data 2.5.x
    ([\#569](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/569)).

### Firestore

  - Fixed: Unable to query by document ID.
    ([\#506](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/506)).

  - Fixed: Attempting to infer environment credentials when using
    emulator.
    ([\#555](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/555)).

  - Added support for `OrderBy` clause in method name.
    ([\#516](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/516)).

### Pub/Sub

  - Fixed: bean factory propagation in consumer binding
    ([\#515](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/515)).

  - Removed workaround in `PubSubInboundChannelAdapter` ensuring error
    propagation during application shutdown. This should be a no-op to
    users, as Spring Integration starting with v5.4.3 and Spring Cloud
    Stream starting with v3.1.1 use `requireSubscribers=true` on the
    default error channels, causing any errors reaching error channels
    with no subscribers to propagate an exception.

  - Added IDE discovery for `management.health.pubsub.enabled` property
    ([\#543](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/543))

### Secret Manager

  - Fixed: `ByteString` value conversion compatibility with Spring Boot
    2.5.x
    ([\#496](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/496)).

Thanks to our community contributors, @artemptushkin, @garywg04 and
@alos\!

## 2.0.3 (2021-06-08)

### General

  - Upgraded to GCP Libraries BOM 20.6.0

  - Added version management for
    `com.google.cloud.sql:jdbc-socket-factory-core`
    ([\#466](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/466))

### Cloud Pub/Sub

  - Exposed publisher endpoint for message ordering
    ([\#421](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/421))

  - Pub/Sub Health Indicator timeout increased to 2 seconds
    ([\#420](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/420)).

  - Gated Cloud Pub/Sub emulator autoconfiguration Pub/Sub module being
    present and enabled
    ([\#446](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/446))

  - `PubSubMessageHandler` now passes the original message to new
    success/failure callbacks, allowing applications to track message
    publish status
    ([\#482](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/482)).
    The old `PubSubMessageHandler.setPublishCallback()` method is now
    deprecated.

### Cloud Spanner

  - Stopped Cloud Spanner emulator autoconfiguration from triggering
    default credentials creation
    ([\#457](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/457)).

  - Added ability to customize arbitrary `SpannerOptions` settings by
    configuring a `SpannerOptionsCustomizer` bean
    ([\#489](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/489)).

### Cloud SQL

  - Added IAM authentication option for PostgreSQL
    ([\#488](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/488)).

  - Enabled placeholder interpretation in Cloud SQL properties
    ([\#495](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/495)).

Thanks to our community contributors, @herder and @melburne\!

## 2.0.2 (2021-03-25)

### General

  - Upgraded to Spring Cloud 2020.0.2 and Spring Boot 2.4.4

  - Upgraded to Google Cloud Libraries BOM 19.2.1

  - Added Java 16 support
    ([\#391](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/391))

  - Various code quality improvements with the help of SonarCloud.

### Cloud SQL

  - Disabled `CloudSqlEnvironmentPostProcessor` in bootstrap context
    ([\#273](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/273))
    
      - This enables the use of Secrets Manager property placeholders
        together with Cloud SQL configuration.

### BigQuery

  - Fixed a bug in the `BigQueryFileMessageHandler` where it referenced
    the wrong variable in the setter
    ([\#270](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/270))

### Datastore

  - Added `storeOnDisk` and `dataDir` configuration properties for
    Datastore Emulator
    ([\#344](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/344))

  - Fixed resolution of references and descendants for subclasses
    ([\#377](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/377))

### Firestore

  - Modified `Firestore.withParent()` to accept `String` instead of
    `Object`
    ([\#315](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/315))

### Logging

  - Fixed the JSON layout logging levels mapping
    ([\#314](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/314))

### Pub/Sub

  - In Cloud Stream Pub/Sub Binder, added support for specifying a
    custom subscription as a consumer endpoint
    ([\#262](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/262))

  - Added `PubSubAdmin.createSubscription(Subscription.Builder)` to
    allow access to all subscription properties
    ([\#343](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/343))

  - Added warnings about the use of `returnImmediately=true`
    ([\#354](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/354))

  - Added Cloud Stream Dead Letter Topic support
    ([\#358](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/358))

  - Added support for custom subscription name for Pub/Sub health check
    ([\#330](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/330))

  - Added support for message ordering when publishing
    ([\#408](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/408))
    
      - Introduced
        `spring.cloud.gcp.pubsub.publisher.enable-message-ordering` and
        `GcpPubSubHeaders.ORDERING_KEY` header

### Storage

  - Fixed: `PathResourceResolver` can’t resolve a
    `GoogleStorageResource` due to no Google Storage `UrlStreamHandler`
    ([\#210](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/210))

## 2.0.1 (2021-02-04)

### General

  - Upgraded to Spring Cloud 2020.0.1 and Spring Boot 2.4.2
    ([\#233](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/233))

  - Multiple code quality improvements with help from SonarCloud

### Firestore

  - Fixed: Firestore emulator not using configured project id
    ([\#211](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/211))

### Logging

  - Fixed: Trace ID not populated when using `AsyncAppender`
    ([\#196](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/196))

  - Made `StackdriverJsonLayout` more customizable with support for
    logging event enhancers
    ([\#208](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/208))
    
      - Added an extension for Logstash markers support

### Pub/Sub

  - Fixed: Spring Cloud Stream unable to setup subscription to a topic
    in a different GCP project
    ([\#232](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/232))

### Spanner

  - Fixed session leak with aborted read/write transactions
    ([\#251](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/251))

## 2.0.0 (2021-01-06)

### General

  - Compatible with Spring Cloud `2020.0.0` (Ilford release train)

  - Package renamed from `org.springframework.cloud.gcp` to
    `com.google.cloud.spring`

  - Maven coordinates now use `com.google.cloud` as the group ID

  - All `deprecated` items removed

For a full list, please see the [2.x migration
guide](https://googlecloudplatform.github.io/spring-cloud-gcp/reference/html/index.html#migration-guide-from-spring-cloud-gcp-1-x-to-2-x).

### Cloud SQL

  - Replaced `CloudSqlAutoConfiguration` with
    `CloudSqlEnvironmentPostProcessor`
    ([\#131](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/131))

### Datastore

  - Fixed auditing when running through
    `DatastoreTemplate.performTransaction()`
    ([\#157](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/157))

  - Fixed `findAll(example, pageable)` ignores `@Reference` annotated
    fields
    ([\#177](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/177))

### Firestore

  - Resolved 10 simultaneous writes limitation
    ([\#135](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/135))

  - Added update time and optimistic locking support
    ([\#171](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/171))

### KMS

  - Added Cloud Key Management Service (KMS) support
    ([\#175](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/175))
    
      - Spring Boot starter, sample, and documentation included

### Logging

  - Added support for trace with async logging
    ([\#197](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/197))

### Metrics

  - Multiple fixes for the metrics auto-config and sample
    ([\#121](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/121))

### Pub/Sub

  - Addded support for binder customizers
    ([\#186](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/186))

### Secret Manager

  - Changed secret manager module to use v1 instead of v1beta
    ([\#173](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/173))

### Spanner

  - Added support `spring.cloud.gcp.project-id` property for Spanner
    Emulator config
    ([\#123](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/123))

## 1.2.7 (TBD)

### Pub/Sub

  - Fixed Pub/Sub emulator `ManagedChannel` shutdown
    ([\#2583](https://github.com/spring-cloud/spring-cloud-gcp/issues/2583))

## 1.2.6.RELEASE (2020-11-09)

### General

  - Added `proxyBeanMethods = false` to configuration classes for better
    GraalVM support
    ([\#2525](https://github.com/spring-cloud/spring-cloud-gcp/issues/2525))

  - Updated `gcp-libraries-bom.version` to 13.4.0
    ([\#2571](https://github.com/spring-cloud/spring-cloud-gcp/issues/2571))

### Pub/Sub

  - Differentiate between Publisher and Subscriber
    `TransportChannelProvider`
    ([\#2520](https://github.com/spring-cloud/spring-cloud-gcp/issues/2520))
    
      - If you’ve been overwriting the auto-configured
        `transportChannelProvider` bean for Pub/Sub, you will need to
        rename it to `{"subscriberTransportChannelProvider",
        "publisherTransportChannelProvider"}`.

  - Better generics for ack operations in `PubSubSubscriberOperations`
    ([\#2539](https://github.com/spring-cloud/spring-cloud-gcp/issues/2539))
    
      - This a minor breaking change if you have a custom implementation
        of `PubSubSubscriberOperations`.

  - Fixed: With MessageHistory enabled, sending a Pub/Sub message and
    consuming it in a subscription fails due to
    `IllegalArgumentException`
    ([\#2562](https://github.com/spring-cloud/spring-cloud-gcp/issues/2562))

### Cloud SQL

  - Added support for configuring Cloud SQL ipTypes with the
    `spring.cloud.gcp.sql.ip-types` property
    ([\#2513](https://github.com/spring-cloud/spring-cloud-gcp/issues/2513))

  - Fixed: starter-sql-mysql doesn’t override `spring.datasource.url`
    ([\#2537](https://github.com/spring-cloud/spring-cloud-gcp/issues/2537))

### Spanner

  - Added NUMERIC data type support for Spanner (BigDecimal)
    ([\#2515](https://github.com/spring-cloud/spring-cloud-gcp/issues/2515))

### Firestore

  - Fixed: StructuredQuery.from cannot have more than one collection
    selector
    ([\#2510](https://github.com/spring-cloud/spring-cloud-gcp/issues/2510))

  - Added query methods that return `Slice` to `DatastoreTemplate` to
    allow pagination
    ([\#2541](https://github.com/spring-cloud/spring-cloud-gcp/issues/2541))

  - Added support for `is not equal` and `not in` filters in method name
    based queries
    ([\#2563](https://github.com/spring-cloud/spring-cloud-gcp/issues/2563))

## 1.2.5.RELEASE (2020-08-28)

### Secret Manager

  - Fixed: Spring GCP Secrets references not working when using Spring
    Cloud Server
    ([\#2483](https://github.com/spring-cloud/spring-cloud-gcp/issues/2483))

  - Fixed: Spring boot error when using google-cloud-secretmanager
    library without spring-cloud-gcp-starter-secretmanager
    ([\#2506](https://github.com/spring-cloud/spring-cloud-gcp/issues/2506))

### Pub/Sub

  - Added support for composite actuator contributor for multiple
    Pub/Sub templates
    ([\#2493](https://github.com/spring-cloud/spring-cloud-gcp/issues/2493))

### Datastore

  - Added value nullity check to avoid NPE with primitive types
    ([\#2505](https://github.com/spring-cloud/spring-cloud-gcp/issues/2505))

## 1.2.4.RELEASE (2020-07-31)

### General

  - Upgraded GCP libraries BOM and other dependencies
    ([\#2477](https://github.com/spring-cloud/spring-cloud-gcp/issues/2477))

### Metrics

  - New Spring Cloud GCP starter, `spring-cloud-gcp-starter-metrics`,
    configures Micrometer Stackdriver to automatically pick up project
    ID and credentials (thanks to @eddumelendez).

  - Added Metrics Sample App
    ([\#2455](https://github.com/spring-cloud/spring-cloud-gcp/issues/2455))

### Firebase Security

  - Allow `projectId` override in Firebase Authentication
    ([\#2405](https://github.com/spring-cloud/spring-cloud-gcp/issues/2405))

### Spanner

  - Allow `Pageable` and `Sort` in method-style (part-tree) queries
    ([\#2394](https://github.com/spring-cloud/spring-cloud-gcp/issues/2394))

  - Fixed: `NullPointerException` when passing the null value for
    nullable column
    ([\#2448](https://github.com/spring-cloud/spring-cloud-gcp/issues/2448))

### Secret Manager

  - Added additional operations for managing secret versions with
    `SecretManagerTemplate` (thanks to @kioie)

### Storage

  - Added the `spring.cloud.gcp.storage.project-id` autoconfig property
    ([\#2440](https://github.com/spring-cloud/spring-cloud-gcp/issues/2440))

  - Additional GCS Spring Integration file filters
    `GcsAcceptModifiedAfterFileListFilter` and
    `GcsDiscardRecentModifiedFileListFilter` (thanks to @hosainnet)

### Datastore

  - Fixed: Unable to exclude indexes on nested properties of embedded
    entity
    ([\#2439](https://github.com/spring-cloud/spring-cloud-gcp/issues/2439))

  - Fixed slice query execution in `PartTreeDatastoreQuery`
    ([\#2452](https://github.com/spring-cloud/spring-cloud-gcp/issues/2452))

  - Fixed `null` handling for ID in query-by-example
    ([\#2471](https://github.com/spring-cloud/spring-cloud-gcp/issues/2471))

### Pub/Sub

  - Added `maxMessages` to `PubSubReactiveFactory.poll`
    ([\#2441](https://github.com/spring-cloud/spring-cloud-gcp/issues/2441))

  - Control sync/async publish in Spring Cloud Stream binder
    ([\#2473](https://github.com/spring-cloud/spring-cloud-gcp/issues/2473))

### Firestore

  - Add subcollection support for `FirestoreTemplate`
    ([\#2434](https://github.com/spring-cloud/spring-cloud-gcp/issues/2434))

  - Added support for automatic ID generation
    ([\#2466](https://github.com/spring-cloud/spring-cloud-gcp/issues/2466))

  - Added `FirestoreTemplate` reference documentation
    ([\#2480](https://github.com/spring-cloud/spring-cloud-gcp/issues/2480))

## 1.2.3.RELEASE (2020-05-29)

### General

  - Upgrade to latest libraries bom and sql socket factory
    ([\#2373](https://github.com/spring-cloud/spring-cloud-gcp/issues/2373))

  - Make transaction managers conditional on enabled flag for Spanner
    and Datastore
    ([\#2376](https://github.com/spring-cloud/spring-cloud-gcp/issues/2376))

### Logging

<div class="note">

As we upgraded to the latest version of `google-cloud-logging-logback`,
we picked up a [breaking
change](https://github.com/googleapis/java-logging-logback/pull/43)
where the log entry payload is now written in JSON rather than plain
text. So, if you’re reading log entries back from Cloud Logging using
`LogEntry.getPayload()`, make sure to cast the returned payload object
to `JsonPayload` instead of `StringPayload`.

</div>

### Secret Manager

<div class="note">

This version introduced several breaking changes to Secret Manager
property source. Please see the [reference
documentation](https://cloud.spring.io/spring-cloud-static/spring-cloud-gcp/1.2.3.RELEASE/reference/html/#secret-manager-property-source\))
for the new way for accessing secrets as properties.

</div>

  - Remove the version property in secret manager
    ([\#2270](https://github.com/spring-cloud/spring-cloud-gcp/issues/2270))

  - Secret manager template with project
    ([\#2283](https://github.com/spring-cloud/spring-cloud-gcp/issues/2283))
    ([\#2284](https://github.com/spring-cloud/spring-cloud-gcp/issues/2284))

  - Create protocol for specifying secrets' project and versions
    ([\#2302](https://github.com/spring-cloud/spring-cloud-gcp/issues/2302))

  - Add secret manager autoconfigure property
    ([\#2363](https://github.com/spring-cloud/spring-cloud-gcp/issues/2363))

### Pub/Sub

  - New async pull methods in `Pub/Sub Template`, as well as fully
    asynchronous `PubSubReactiveFactory.poll()`
    ([\#2227](https://github.com/spring-cloud/spring-cloud-gcp/pull/2227))

  - Suppress exception in Pub/Sub adapter in AUTO\_ACK and MANUAL modes
    ([\#2319](https://github.com/spring-cloud/spring-cloud-gcp/issues/2319))

  - Make 403 an allowable Pub/Sub UP status
    ([\#2385](https://github.com/spring-cloud/spring-cloud-gcp/issues/2385))

### Trace

  - Support Extra Propagation Fields with Trace
    ([\#2290](https://github.com/spring-cloud/spring-cloud-gcp/issues/2290))

### Spanner

  - Fix @Where with ORDER BY query generation
    ([\#2267](https://github.com/spring-cloud/spring-cloud-gcp/issues/2267))

  - Add SpannerOptions auto-configuration for emulator
    ([\#2356](https://github.com/spring-cloud/spring-cloud-gcp/issues/2356))

### Datastore

  - Support for nested properties in PartTree methods
    ([\#2307](https://github.com/spring-cloud/spring-cloud-gcp/issues/2307))

  - Datastore Projections should restrict query to contain only the
    necessary fields
    ([\#2335](https://github.com/spring-cloud/spring-cloud-gcp/issues/2335))

  - Support custom maps
    ([\#2345](https://github.com/spring-cloud/spring-cloud-gcp/issues/2345))

### Firestore

  - Firestore nested properties
    ([\#2300](https://github.com/spring-cloud/spring-cloud-gcp/issues/2300))

  - Add autoconfiguration for Firestore Emulator
    ([\#2244](https://github.com/spring-cloud/spring-cloud-gcp/issues/2244))

  - Add support for Firestore Sort PartTree queries
    ([\#2341](https://github.com/spring-cloud/spring-cloud-gcp/issues/2341))

  - Add child collection to the entity class in Firestore sample
    ([\#2388](https://github.com/spring-cloud/spring-cloud-gcp/issues/2388))

### Vision

  - Allow users to provide the ImageContext in CloudVisionTemplate
    ([\#2286](https://github.com/spring-cloud/spring-cloud-gcp/issues/2286))

### Firebase Security

  - Make Firebase Security Autoconfiguration conditional
    ([\#2258](https://github.com/spring-cloud/spring-cloud-gcp/issues/2258))
    Thank you to the contributors from our user community:
    @eddumelendez, @mzeijen, @s13o, @acet, @guillaumeblaquiere

## 1.2.2.RELEASE (2020-03-04)

### General

  - Switched to using GCP Libraries BOM for managing GCP library
    versions
    ([\#2109](https://github.com/spring-cloud/spring-cloud-gcp/issues/2109))

  - Core auto-configuration can now be disabled with
    `spring.cloud.gcp.core.enabled=false`
    ([\#2147](https://github.com/spring-cloud/spring-cloud-gcp/issues/2147))

  - Reference documentation improvements

  - Two new modules: Firebase Auth and Secret Manager

### Datastore

  - Support lazy loading entities using @LazyReference
    ([\#2104](https://github.com/spring-cloud/spring-cloud-gcp/issues/2104))

  - Made existsById more efficient by retrieving only the key field
    ([\#2127](https://github.com/spring-cloud/spring-cloud-gcp/issues/2127))

  - Projections now work with the Slice return type
    ([\#2133](https://github.com/spring-cloud/spring-cloud-gcp/issues/2133))
    and GQL queries
    ([\#2139](https://github.com/spring-cloud/spring-cloud-gcp/issues/2139))
    in repositories

  - Improved repository method name validation
    ([\#2155](https://github.com/spring-cloud/spring-cloud-gcp/issues/2155))

  - Fixed delete for void repository method return type
    ([\#2169](https://github.com/spring-cloud/spring-cloud-gcp/issues/2169))

### Firebase (NEW)

  - Introduced Firebase Authentication module
    ([\#2111](https://github.com/spring-cloud/spring-cloud-gcp/issues/2111))

### Firestore

  - Added IN support in name-based queries
    ([\#2054](https://github.com/spring-cloud/spring-cloud-gcp/issues/2054))

### Pub/Sub

  - ACK\_MODE is now configurable using stream binders
    ([\#2079](https://github.com/spring-cloud/spring-cloud-gcp/issues/2079))

  - Added HealthIndicator implementation
    ([\#2030](https://github.com/spring-cloud/spring-cloud-gcp/issues/2030))

  - Fixed: `PubSubReactiveFactory.poll` doesn’t handle exceptions thrown
    by the `PubSubSubscriberOperations`
    ([\#2229](https://github.com/spring-cloud/spring-cloud-gcp/issues/2229))
    
      - NOTE: previously silently ignored exceptions are now forwarded
        to the Flux

### Secret Manager (NEW)

  - Bootstrap Property Source which loads secrets from Secret Manager to
    be accessible as environment properties to your application
    ([\#2168](https://github.com/spring-cloud/spring-cloud-gcp/issues/2168))

  - SecretManagerTemplate implementation
    ([\#2195](https://github.com/spring-cloud/spring-cloud-gcp/issues/2195))

  - New Secret Manager sample app
    ([\#2190](https://github.com/spring-cloud/spring-cloud-gcp/issues/2190))

### Spanner

  - Fixed java.util.Date conversion and added LocalDate and
    LocalDateTime support
    ([\#2067](https://github.com/spring-cloud/spring-cloud-gcp/issues/2067))

  - Added support for non-Key ID types in Spring Data REST repositories
    ([\#2049](https://github.com/spring-cloud/spring-cloud-gcp/issues/2049))

  - Optimized eager loading for interleaved properties
    ([\#2110](https://github.com/spring-cloud/spring-cloud-gcp/issues/2110))
    ([\#2165](https://github.com/spring-cloud/spring-cloud-gcp/issues/2165))

  - Enable using PENDING\_COMMIT\_TIMESTAMP in Spring Data Spanner
    ([\#2203](https://github.com/spring-cloud/spring-cloud-gcp/issues/2203))

### Storage

  - Added ability to provide initial file contents on blob creation
    ([\#2097](https://github.com/spring-cloud/spring-cloud-gcp/issues/2097))

  - You can now use a comparator with GcsStreamingMessageSource to
    process blobs from Cloud Storage in an ordered manner.
    ([\#2117](https://github.com/spring-cloud/spring-cloud-gcp/issues/2117))

  - Fixed GCS emulator BlobInfo update time initialization
    ([\#2113](https://github.com/spring-cloud/spring-cloud-gcp/issues/2113))

### Trace

  - Hid trace scheduler from Spring Sleuth
    ([\#2158](https://github.com/spring-cloud/spring-cloud-gcp/issues/2158))

## 1.2.1.RELEASE (2019-12-20)

### Spanner

  - Fixed java.sql.Timestamp to com.google.cloud.Timestamp conversion
    ([\#2064](https://github.com/spring-cloud/spring-cloud-gcp/issues/2064))

### Pub/Sub

  - Fixed AUTO\_ACK acking behavior in PubSubInboundChannelAdapter
    ([\#2075](https://github.com/spring-cloud/spring-cloud-gcp/issues/2075))

## 1.2.0.RELEASE (2019-11-26)

### BigQuery

  - New module

  - Autoconfiguration for the BigQuery client objects with credentials
    needed to interface with BigQuery

  - A Spring Integration message handler for loading data into BigQuery
    tables in your Spring integration pipelines

### Cloud Foundry

  - Created a separate starter for Cloud Foundry:
    spring-cloud-gcp-starter-cloudfoundry

### Datastore

  - Datastore emulator support and auto-configuration

  - Entity Inheritance Hierarchies support

  - Query by example

  - Support Pagination for @Query annotated methods

  - Support key fields in name-based query methods

  - Events and Auditing support

  - Support for multiple namespaces

  - Spring Boot Actuator Support for Datastore Health Indicator
    ([\#1423](https://github.com/spring-cloud/spring-cloud-gcp/issues/1423))

### Firestore

  - Spring Data Reactive Repositories for Cloud Firestore

  - Cloud Firestore Spring Boot Starter

### Logging

  - Additional metadata support for JSON logging
    ([\#1310](https://github.com/spring-cloud/spring-cloud-gcp/issues/1310))

  - Add service context for Stackdriver Error Reporting

  - Add option to add custom json to log messages

  - A separate module for Logging outside of autoconfiguration
    ([\#1455](https://github.com/spring-cloud/spring-cloud-gcp/issues/1455))

### Pub/Sub

  - PubsubTemplate publish to topics in other projects
    ([\#1678](https://github.com/spring-cloud/spring-cloud-gcp/issues/1678))

  - PubsubTemplate subscribe in other projects
    ([\#1880](https://github.com/spring-cloud/spring-cloud-gcp/issues/1880))

  - Reactive support for Pub/Sub subscription
    ([\#1461](https://github.com/spring-cloud/spring-cloud-gcp/issues/1461))

  - Spring Integration - Pollable Message Source (using Pub/Sub
    Synchronous Pull)
    ([\#1321](https://github.com/spring-cloud/spring-cloud-gcp/issues/1321))

  - Pubsub stream binder via synchronous pull
    ([\#1419](https://github.com/spring-cloud/spring-cloud-gcp/issues/1419))

  - Add keepalive property to pubsub; set default at 5 minutes
    ([\#1807](https://github.com/spring-cloud/spring-cloud-gcp/issues/1807))

  - Change thread pools to create daemon threads that do not prevent JVM
    shutdown
    ([\#2010](https://github.com/spring-cloud/spring-cloud-gcp/issues/2010))
    
      - This is a change in behavior for non-web applications that
        subscribe to a Cloud Pub/Sub topic. The subscription threads
        used to keep the application alive, but will now allow the
        application to shut down if no other work needs to be done.

  - Added original message to the throwable for Pub/Sub publish failures
    ([\#2020](https://github.com/spring-cloud/spring-cloud-gcp/issues/2020))

### IAP

  - Added support to allow multiple IAP audience claims
    ([\#1856](https://github.com/spring-cloud/spring-cloud-gcp/issues/1856))

### Spanner

  - Expose Spanner failIfPoolExhausted property
    ([\#1889](https://github.com/spring-cloud/spring-cloud-gcp/issues/1889))

  - Lazy fetch support for interleaved collections
    ([\#1460](https://github.com/spring-cloud/spring-cloud-gcp/issues/1460))

  - Bounded staleness option support
    ([\#1727](https://github.com/spring-cloud/spring-cloud-gcp/issues/1727))

  - Spring Data Spanner Repositories `In` clause queries support
    ([\#1701](https://github.com/spring-cloud/spring-cloud-gcp/issues/1701))

  - Spanner array param binding

  - Events and Auditing support

  - Multi-Instance support
    ([\#1530](https://github.com/spring-cloud/spring-cloud-gcp/issues/1530))

  - Fixed conversion for timestamps older than unix epoch
    ([\#2043](https://github.com/spring-cloud/spring-cloud-gcp/issues/2043))

  - Fixed REST Repositories PUT by populating key fields when virtual
    key property is set
    ([\#2053](https://github.com/spring-cloud/spring-cloud-gcp/issues/2053))

### Spring Cloud Bus

  - Spring Cloud Config and Bus over Pub/Sub sample/docs
    ([\#1550](https://github.com/spring-cloud/spring-cloud-gcp/issues/1550))

### Vision

  - Cloud Vision Document OCR support

## 1.1.0.RELEASE (2019-01-22)

  - [1.1
    announcement](https://cloud.google.com/blog/products/application-development/announcing-spring-cloud-gcp-1-1-deepening-ties-pivotals-spring-framework)

## 1.0.0.RELEASE (2018-09-18)

  - [1.0
    announcement](https://cloud.google.com/blog/products/gcp/calling-java-developers-spring-cloud-gcp-1-0-is-now-generally-available)
