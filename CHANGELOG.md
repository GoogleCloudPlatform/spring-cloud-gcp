# Spring Cloud GCP Changelog

For 2.x changelog, go to [this
page](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/blob/2.x/CHANGELOG.adoc).

[Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) is a set
of integrations between Spring Framework and Google Cloud. It
makes it much easier for Spring framework users to run their
applications on Google Cloud.

This document provides a high-level overview of the changes introduced
in Spring Cloud GCP by release. For a detailed view of what has changed,
refer to the [commit
history](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commits/main)
on GitHub.

## [6.1.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v6.0.1...v6.1.0) (2025-03-12)


### Features

* add support for `not equals` queries in datastore integration ([#3635](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3635)) ([548823f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/548823f70a7459bb56fb36c10b5c21ab20c3314b))
* **datastore:** support `--use-firestore-in-datastore-mode` in the datastore emulator ([#3633](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3633)) ([06ca7a7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/06ca7a796636ffc8290787b4e12fe6de02015bcd))


### Bug Fixes

* create a datastore property to skip insertion if the value is `null` ([#3611](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3611)) ([925c0e1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/925c0e17052445e20380cd7ba1d2fc1041a34b91))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.56.0 ([#3612](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3612)) ([6cb3e2e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6cb3e2ecd6a0a62c1a39f9f38cb1ecc113133553))
* **deps:** update gapic-generator-java-bom.version to v2.54.0 ([#3598](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3598)) ([c6b6e45](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c6b6e457addf5d561287dc8a885d8db5568c94dd))
* **test:** change logback default flush lev ([#3627](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3627)) ([#3637](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3637)) ([dbe8f95](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dbe8f959e3cc512a19a7719ef2de4efba1fd6e9c))

## [6.0.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v6.0.0...v6.0.1) (2025-02-24)


### Bug Fixes

* **deps:** update dependency com.google.api:gapic-generator-java-bom to v2.53.0 ([#3543](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3543)) ([abaca12](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/abaca12899b2c5ec0aea1a409e199fb169f04888))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.55.0 ([#3588](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3588)) ([7dfb676](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7dfb67656b047a85924c42f4314cba5a81a3cfcf))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.23.0 to 1.23.1 ([#3555](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3555)) ([576c8d2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/576c8d294ab91be928a72b0206dfc01e24d3dfed))
* bump com.google.cloud:alloydb-jdbc-connector from 1.1.8 to 1.2.0 ([#3552](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3552)) ([c412fac](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c412facefb1ee80b40242501be951faf9c2ae035))
* bump io.micrometer:micrometer-tracing-bom from 1.4.2 to 1.4.3 ([#3547](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3547)) ([4a6b7a5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4a6b7a5e781053674d6d9251e3a794ca71d43ec7))
* bump io.opentelemetry:opentelemetry-api from 1.46.0 to 1.47.0 ([#3540](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3540)) ([3d4d8c6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3d4d8c6b9bd8253cf9c40724bb98d6fc7fbab5a4))
* bump org.awaitility:awaitility from 4.2.2 to 4.3.0 ([#3575](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3575)) ([8087e1e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8087e1e7be46ff6d5c66dfb91b071f878ea5efef))
* bump org.graalvm.buildtools:native-maven-plugin ([#3530](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3530)) ([0688e0b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0688e0b5b47e873238cf0bb42a2f35aece5c4ff0))
* bump org.springframework.boot:spring-boot-starter-parent ([#3571](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3571)) ([7ab75a3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7ab75a312bc2eccb5afca923870961b5dae5dd2b))
* bump zipkin-gcp.version from 2.2.6 to 2.3.0 ([#3561](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3561)) ([9c08062](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9c080620534fa458ee77466e7fa2d602d0a9068d))


### Documentation

* add clarification for `spring.cloud.gcp.core.enabled` ([#3572](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3572)) ([8af6325](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8af632589c09d7e0029d07a2d4d90882d9567d5d))
* Update README.adoc with latest released versions ([#3535](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3535)) ([76101e5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/76101e5c5432093c12af6d40f9bf1582fff70599))
* update secret manager documentation ([#3534](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3534)) ([11f862e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/11f862efd1626f4d814533366fd267a9f07d8bb0))

## [6.0.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.10.0...v6.0.0) (2025-02-04)


### ⚠ BREAKING CHANGES

* **pubsub:** set default max ack extension period to 60 minutes ([#3501](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3501))
* Spring Cloud 2024.0 and Spring Boot 3.4.2 ([#3500](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3500))

### Features

* **pubsub:** set default max ack extension period to 60 minutes ([#3501](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3501)) ([9245031](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9245031042054efc2ab52cdffad75298922a5a79))
* Spring Cloud 2024.0 and Spring Boot 3.4.1 ([a72a86b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a72a86b6091ffbc208e2aa4bbb02273b94ae32fc))
* Spring Cloud 2024.0 and Spring Boot 3.4.2 ([#3500](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3500)) ([0108054](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/01080543e9133850ecb60d1d41120356a8af18fc))


### Bug Fixes

* **deps:** update gapic-generator-java-bom.version to v2.52.0 ([#3512](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3512)) ([c184e69](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c184e6993898a6f9be8f00a2e6ef5e12deb39bb5))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.21.2 to 1.23.0 ([#3502](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3502)) ([7760474](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7760474ffd74897bcf67631157a100e2bc329569))
* bump com.google.cloud:libraries-bom from 26.53.0 to 26.54.0 ([#3522](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3522)) ([f54327d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f54327dc20dc3e9019883b37413dbc1f5d76cba3))
* bump org.springframework.boot:spring-boot-starter-parent ([#3495](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3495)) ([969c4b6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/969c4b6cb147295d690f3225591b7343713676f2))

## [5.10.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.9.0...v5.10.0) (2025-01-15)


### Features

* log stacktrace ([#3418](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3418)) ([cebcbed](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cebcbed7d98f3965f0283b04663e11411cec53b4))


### Bug Fixes

* **deps:** update cloud-sql-socket-factory.version to v1.21.2 ([#3451](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3451)) ([d227783](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d22778372ecf4c4f3487cd6cd581c167420765f2))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.52.0 ([#3421](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3421)) ([5c74e41](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5c74e410a92b40ebf51f76ad9e3bea4091af3f20))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.53.0 ([#3471](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3471)) ([5d2615f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5d2615fa02f540570e5e0004203c53552fed8460))
* **deps:** update dependency commons-io:commons-io to v2.18.0 ([#3455](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3455)) ([5371117](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5371117849e36c2ae4e330006790045824fbe6a0))
* **deps:** update gapic-generator-java-bom.version to v2.51.0 ([#3422](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3422)) ([aae9f99](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/aae9f994697dd4ef22064c50d2461be9bb5dd5be))
* **deps:** update gapic-generator-java-bom.version to v2.51.1 ([#3452](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3452)) ([e653ab1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e653ab1ce8abd74526e2212980cbde7b226ed663))
* **deps:** update zipkin-gcp.version to v2.2.6 ([#3453](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3453)) ([ce5d9eb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ce5d9eb836e718e6e615486b74c42f0d3b249008))
* increase default pubsub health indicator timeout from 2000ms to 5000ms ([#3400](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3400)) ([b67caa9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b67caa9953e574cee3d99909047dc4ac9ac8d518))


### Dependencies

* bump com.google.cloud:alloydb-jdbc-connector from 1.1.6 to 1.1.8 ([#3414](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3414)) ([283b1fa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/283b1fa5f49afb34abb8cd402e02629f5f99bd4b))
* bump io.micrometer:micrometer-tracing-bom from 1.3.5 to 1.4.2 ([#3446](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3446)) ([ce85135](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ce85135f846f33c0cf3c44b2c57d88f93dccc1bb))
* bump io.opentelemetry:opentelemetry-api from 1.43.0 to 1.46.0 ([#3442](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3442)) ([4e2b9bc](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4e2b9bccadc17aa4a7e57ce63eedad8e49d067ef))
* bump org.apache.maven.plugins:maven-checkstyle-plugin ([#3344](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3344)) ([324efdf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/324efdf0e5c43527ee0c4a4bee8738292a46ea52))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#3409](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3409)) ([985031e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/985031eda2b7385975449e98547cb84d0c3823cd))
* bump org.graalvm.buildtools:native-maven-plugin ([#3464](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3464)) ([e3dee62](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e3dee62bec46cbb8e79b6c1ab6ac91bf7e9f979a))
* bump org.springframework.cloud:spring-cloud-dependencies ([#3463](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3463)) ([86d61ad](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/86d61ade69a4ff2c7a95ccac386ab61f4c7ebbd1))
* bump spring-cloud-config.version from 4.1.3 to 4.2.0 ([#3461](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3461)) ([9ee363e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9ee363eadece6174c3a0e4c449f985b4e129fc93))


### Documentation

* fix references about other integrations in spanner doc ([#3413](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3413)) ([1e99a52](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1e99a52cb1194d1d1d3b6f2eb886c6130fa1a58b))

## [5.9.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.8.0...v5.9.0) (2024-12-06)


### Features

* Add all additional Cloud SQL Java Connector parameters to the spring configuration. ([#3286](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3286)) ([f2212d3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f2212d3944f13a46a6eea496c16e922a0c8fab56))
* add property to customize universe domain in Pub/Sub ([#3348](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3348)) ([9cf2145](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9cf214507a7ce47613443e55ae7f010d7f0e5ed9))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.51.0 ([#3350](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3350)) ([a270a7d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a270a7de48a5214cd61c10e7915b4bd46ba1b050))
* include limit in query for findFirst and findTop support in Firestore ([#3387](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3387)) ([9fe8e91](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9fe8e9128c89d22755f648fd185c9140f1de3a2d))
* migrate logic to create tmpdir to build script ([#3367](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3367)) ([a12638c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a12638ce18725fb91319581571affd378d7241ad))
* need to update to use new trampoline_release.py script as well as update to configure docker auth with new image ([#3356](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3356)) ([b5ace26](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b5ace263651d8ccfd9078da1fa792ab54931f8a4))
* sonar build 401 issue ([#3381](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3381)) ([fa53707](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fa53707893c40c413fc23c30f0391d03fb4e45fa))

## [5.8.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.7.0...v5.8.0) (2024-10-23)


### Features

* add properties to customize universe-domain and host in Storage ([#3287](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3287)) ([f5879d9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f5879d92d81d5b480d2c402bd2e9e792832ebca3))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.49.0 ([e838c64](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e838c646af98792dfaa48374b3d4c76156c695d7))
* **deps:** update dependency io.opentelemetry:opentelemetry-api to v1.43.0 ([#3298](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3298)) ([964756c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/964756c23fabda0f0cc52e03c52ef615b7bf48fb))
* **test:** add mock method to mock credentials. ([e838c64](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e838c646af98792dfaa48374b3d4c76156c695d7))


### Dependencies

* bump com.google.errorprone:error_prone_core from 2.33.0 to 2.34.0 ([#3319](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3319)) ([6a91c0d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6a91c0d9fc7a52b9f1c8eb460a21bac09727cc8f))
* bump io.micrometer:micrometer-tracing-bom from 1.3.4 to 1.3.5 ([#3302](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3302)) ([8ab63c8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8ab63c8d72f4f30c2e378eb38f0001074f441e2f))
* bump io.opentelemetry:opentelemetry-api from 1.42.1 to 1.43.0 ([#3299](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3299)) ([eea8e28](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/eea8e2814dc73dbd8d7cca17f531c2632cfcc269))
* bump org.postgresql:r2dbc-postgresql ([#3310](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3310)) ([40b8b2a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/40b8b2adad2e957216d95a409c0a4a5fb838f452))

## [5.7.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.6.1...v5.7.0) (2024-10-03)


### Features

* add properties to set universe domain and endpoint in bigquery ([#3158](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3158)) ([9b3c780](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9b3c780b45f5b5d6317a30e62ff7ec028982578e))


### Bug Fixes

* **deps:** update cloud-sql-socket-factory.version to v1.21.0 ([#3270](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3270)) ([fdd8957](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fdd8957bcad3281ed86468af9a4df2fc1aeedf78))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.48.0 ([#3271](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3271)) ([2d65a64](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2d65a6403080e1cb853b8fe0c4b2ac4c507ae048))
* set `maxInboundMetadataSize` in pubsub  ([#3157](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3157)) ([f333e41](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f333e41987a8db51ba35ad0728216b0adbffbdc9))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.20.0 to 1.20.1 ([#3191](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3191)) ([16518c6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/16518c6529400bacf6f8e256257fd50b5b9481ae))
* bump com.google.cloud:alloydb-jdbc-connector from 1.1.5 to 1.1.6 ([#3206](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3206)) ([6e2b0e3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6e2b0e357a07c39e54b51515ca15122733f68ace))
* bump com.google.errorprone:error_prone_core from 2.30.0 to 2.33.0 ([#3265](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3265)) ([e462698](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e4626989b49b99247fb147c9d3e067f9c6f1f2cb))
* bump commons-io:commons-io from 2.16.1 to 2.17.0 ([#3253](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3253)) ([97af1d0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/97af1d083943a8cdafdd57d19858580ab9ce38be))
* bump io.micrometer:micrometer-tracing-bom from 1.3.3 to 1.3.4 ([#3201](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3201)) ([4437d65](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4437d65801c252ea97eba634a91d7fc983a44e0c))
* bump io.opentelemetry:opentelemetry-api from 1.41.0 to 1.42.1 ([#3207](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3207)) ([b43e8e7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b43e8e7c531252d2f520f0f192e67ced917112af))
* bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.5 to 3.2.7 ([#3261](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3261)) ([e3da8fe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e3da8fe3d8458e37a87af0ba8219922400b4c426))
* bump org.graalvm.buildtools:native-maven-plugin ([#3208](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3208)) ([faa4ef5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/faa4ef5cd3f921e9a8943f0971c44a2ec4f7f0b2))
* bump org.springframework.boot:spring-boot-starter-parent ([#3259](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3259)) ([b8ae0f0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b8ae0f03d29cbdbbb944679df42ff99fd2233ad8))
* bump spring-boot-dependencies.version from 3.3.3 to 3.3.4 ([#3242](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3242)) ([af60cab](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/af60cab5adcc42aa4fa1d2bb72cfaee2c9718e73))
* bump zipkin-gcp.version from 2.2.4 to 2.2.5 ([#3189](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3189)) ([4718eaf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4718eaf0f09e979a460daf3988fae26b90632b4d))


### Documentation

* Update README links ([#3246](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3246)) ([bfc0fbd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bfc0fbd57e4e65dbf933f72aee01f6e82d7b800c))

## [5.6.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.6.0...v5.6.1) (2024-09-20)


### Bug Fixes

* add credential runtime hint for graalvm compilation ([#3200](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3200)) ([f7fc095](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f7fc095a55966a910693718deaa74d9bd2a0d2f8))
* added native image class initialization workaround to storage samples ([#3195](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3195)) ([84e4fc4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/84e4fc413af018e9c707c1b65e6f6619ac44d731))
* **dep:** update all actions/upload-artifact to v4 as v2 was deprecated and now fails work flows ([#3204](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3204)) ([2f0f275](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2f0f275253c0c596cfa1c09b1b4db5d5626aa6d2))
* Descendants entities losing database ID from parent ([#3142](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3142)) ([#3156](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3156)) ([02a1e65](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/02a1e65a4bd5c33039d4ee438591fc3632bb0c1f))
* handle null primary keys between parent and child objects ([#3179](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3179)) ([42c69b6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/42c69b6298ea06b8043811b4048eaf9a6b9cba19))


### Dependencies

* bump com.google.cloud:libraries-bom from 26.45.0 to 26.47.0 ([#3225](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3225)) ([c56409f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c56409f5d02e99534bddd21b80b8dd9f8608b40d))


### Documentation

* Update README.adoc for v5.6.0, v4.10.8 and v3.8.8 ([#3194](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3194)) ([96709b2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/96709b292ccd383475365c9f1d780a1cd654eea2))

## [5.6.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.5.1...v5.6.0) (2024-08-29)


### Features

* add properties to customize universeDomain and endpoint in KMS module ([#3104](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3104)) ([1de0e36](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1de0e36f1264864c6616fe3a499e615089da950f))


### Bug Fixes

* CastClassException in PubSubDeadLetterTopicSampleAppIntegrationTest [#3139](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3139) ([#3145](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3145)) ([ae3626a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ae3626a34c350fc504f8065f2093cfb09865703f))
* noSuchMethodError in BigQuerySampleApplicationIntegrationTests ([#3146](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3146)) ([a891693](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a891693d59c481af23dc2ca44e6b16f200b197fb))
* update compatibility statement for doc ([#3136](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3136)) ([f3591b8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f3591b8f72d711392d8ae927a282c51f7611f93f))


### Dependencies

* bump com.google.cloud:alloydb-jdbc-connector from 1.1.4 to 1.1.5 ([#3129](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3129)) ([fb375f6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fb375f64de411b971b997dec27960e49011df865))
* bump com.google.cloud:libraries-bom from 26.44.0 to 26.45.0 ([#3163](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3163)) ([2ed4f55](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2ed4f556cf21364b39da66c97d410958e7075f6e))
* bump io.opentelemetry:opentelemetry-api from 1.32.0 to 1.41.0 ([#3153](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3153)) ([0ef3aa6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0ef3aa69a04351d214bdc0567ca5f270aa1bbf1d))
* bump org.apache.maven.plugins:maven-checkstyle-plugin ([#3151](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3151)) ([cd77242](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cd77242c61efdaf877107454acaa6129c48f60f5))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#3164](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3164)) ([237e9fd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/237e9fd0ce20693af75d715659ae35d245ef8756))
* bump org.springframework.boot:spring-boot-starter-parent ([#3152](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3152)) ([7c8e434](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7c8e434933e1f093c7699d54f3799b9c2ad70189))
* bump spring-boot-dependencies.version from 3.3.2 to 3.3.3 ([#3150](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3150)) ([f264ea2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f264ea240c16bad43797ec01557931b6fb26c6ec))


### Documentation

* add documentation for kms universe domain and endpoint properties ([#3173](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3173)) ([acf58a0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/acf58a052c6f8997dca9a8c9393b0d7d230d069d))
* adjust spring-cloud-gcp-core documentation auth guides. ([#3144](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3144)) ([20b3393](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/20b339380d4a069795f1656e9f68501c3dbef3dd))
* Update README post releases ([#3137](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3137)) ([7733fe5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7733fe54158ea14af5e27952de0d3efcd0a79004))

## [5.5.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.5.0...v5.5.1) (2024-08-15)


### Bug Fixes

* change to generation script in manipulating googleapis/WORKSPACE. ([#3120](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3120)) ([48e0681](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/48e06810e803ba497f84c6f0de4d04318d8da2f1))
* **deps:** update cloud-sql-socket-factory.version to v1.19.1 ([#3049](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3049)) ([87c3b54](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/87c3b54c00ec4bfd1dbba7faf0648d7c692375a9))
* **deps:** update cloud-sql-socket-factory.version to v1.20.0 ([#3121](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3121)) ([c03e305](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c03e305ad975f5e874dd1b37b31cef61121fdf30))
* **deps:** update dependency com.google.cloud:alloydb-jdbc-connector to v1.1.4 ([#3052](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3052)) ([5090efe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5090efe0164985e13dd526d6e002b7b7a7cc5747))
* pubsub template hanging if exception is thrown when paring response ([#2696](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2696)) ([#3091](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3091)) ([d605691](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d60569132be80f664cfb205da15701925a2d3bee))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.19.0 to 1.19.1 ([#3055](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3055)) ([d07f699](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d07f699620801dec7db1953874f9dfbee5cd3869))
* bump com.google.cloud:alloydb-jdbc-connector from 1.1.3 to 1.1.4 ([#3056](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3056)) ([212522b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/212522b80e0aad8ededbbfa97864f45c9186eb13))
* bump com.google.cloud:libraries-bom from 26.43.0 to 26.44.0 ([#3108](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3108)) ([17e237e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/17e237ee26d83b35069885e8c9fdb1dd07c9bd30))
* bump com.google.cloud.tools:appengine-maven-plugin ([#3086](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3086)) ([85b27f2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/85b27f2419d80b94b65b557c11af63a30555be38))
* bump com.google.errorprone:error_prone_core from 2.28.0 to 2.30.0 ([#3101](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3101)) ([8b62250](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8b6225055cf0a3aa1ada51477b3d455101278557))
* bump com.google.truth:truth from 1.4.3 to 1.4.4 ([#3061](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3061)) ([abb85f5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/abb85f50695a8641dfea437b875e9874349df54b))
* bump io.micrometer:micrometer-tracing-bom from 1.3.2 to 1.3.3 ([#3109](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3109)) ([5533192](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/55331927b83bcb6325e5fb4f087caa12d15ce77e))
* bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.4 to 3.2.5 ([#3110](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3110)) ([7a6f478](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7a6f4784fd7874d35a44c4edd61e8f851c3737f3))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#3085](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3085)) ([fd1b37e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fd1b37e952aedb7f835d940158c27ad72b261f23))
* bump org.awaitility:awaitility from 4.2.1 to 4.2.2 ([#3111](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3111)) ([88607bb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/88607bbf4d2fa12a99a895065cbf3c9dcfa32ad2))
* bump org.springframework.boot:spring-boot-starter-parent ([#3076](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3076)) ([f635a58](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f635a58b22de6b08c977b6db38e8bb5b2ad483aa))
* bump org.springframework.cloud:spring-cloud-dependencies ([#3058](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3058)) ([ab3103f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ab3103fe6ed5d8bc7ca9704cef466c8ba82f451a))
* bump spring-boot-dependencies.version from 3.3.1 to 3.3.2 ([#3075](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3075)) ([e32d6a9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e32d6a90bac4497a5dda008eabcbf683df85211b))
* bump spring-cloud-config.version from 4.1.2 to 4.1.3 ([#3057](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3057)) ([69ecd96](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/69ecd96be8523da7c34b92b387e5233d7ed84b89))


### Documentation

* add Google Cloud Support link to new issues templates and choices ([#3087](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3087)) ([a0d3094](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a0d30941fcd10b7f33fee58db624f7847e753441))

## [5.5.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.4.3...v5.5.0) (2024-07-10)


### Features

* spring boot 3.3.1 ([#2990](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2990)) ([b9de319](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b9de319d1961a2fbb6927b65acd72d5b90d48c79))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.43.0 ([#3029](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3029)) ([a7e8117](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a7e811773fbdeec4c77f3b4d6983f923eab95e81))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.3.1 ([#3021](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3021)) ([ccfc0a1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ccfc0a13df0e513a8515767a65aeaaa3ed2c6ea2))


### Dependencies

* bump com.google.truth:truth from 1.4.2 to 1.4.3 ([#3007](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3007)) ([291123a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/291123a4ed26f850999851e949b5dc8cf6d657eb))
* bump io.micrometer:micrometer-tracing-bom from 1.2.5 to 1.3.1 ([#3024](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3024)) ([c9479e3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c9479e343dbdbb154fa6b30cfcc819a00659fdc1))
* bump io.micrometer:micrometer-tracing-bom from 1.3.1 to 1.3.2 ([#3042](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3042)) ([dbc696e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dbc696e008e9a7022255e42c667be1d19cb92169))


### Documentation

* Update README links ([#3027](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3027)) ([f1e3b82](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f1e3b824e4952392f283a786996660fdeae4b2fc))

## [5.4.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.4.2...v5.4.3) (2024-06-28)


### Bug Fixes

* use maven wrapper in release scripts ([#3015](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3015)) ([9bca063](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9bca063f5d4c78e3a9250badb5db3a8ad0026271))

## [5.4.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.4.1...v5.4.2) (2024-06-27)


### Bug Fixes

* **deps:** update cloud-sql-socket-factory.version to v1.19.0 ([#2936](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2936)) ([456a334](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/456a334940e3345237274e020ad005f5e2c935cf))
* **deps:** update dependency com.google.cloud:alloydb-jdbc-connector to v1.1.3 ([#2982](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2982)) ([09a1be6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/09a1be6df761ae899a3d79c29b0110afaf6f36f7))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.42.0 ([#2979](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2979)) ([b7c4d93](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b7c4d93894c3500d770b7d6f2b8655070842bf6e))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.18.0 to 1.18.1 ([#2944](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2944)) ([b663237](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b6632378e661ad0299824d57f1f9120fc28d6b5d))
* bump com.google.errorprone:error_prone_core from 2.27.1 to 2.28.0 ([#2956](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2956)) ([d2b8fc2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d2b8fc2ebdd073d10bdb505be1280b7390c775be))
* bump org.apache.maven.plugins:maven-checkstyle-plugin ([#2963](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2963)) ([f136e47](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f136e4763108b5a2cedd38463c538164c6b98ee5))
* bump org.apache.maven.plugins:maven-enforcer-plugin ([#2946](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2946)) ([2dacb38](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2dacb386c1a12efc5c24ba914f6b6f2cbddc40bd))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2955](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2955)) ([dad552c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dad552c1a8394ef7e0d3bc3e441b26f5231ab72e))
* bump org.graalvm.buildtools:native-maven-plugin ([#2904](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2904)) ([855808a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/855808ab4a0e1a2655ad06dc04c37116a8d20f4f))
* bump org.springframework.boot:spring-boot-starter-parent ([#2975](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2975)) ([892e001](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/892e001328b41a3159bb28d1fd101f2695385f0b))
* bump org.springframework.cloud:spring-cloud-dependencies ([#2947](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2947)) ([f8a572a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f8a572a04cc4c171599a1602e374fee3bab025c5))
* bump spring-cloud-config.version from 4.1.1 to 4.1.2 ([#2945](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2945)) ([3e06244](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3e06244bed2749954f903086c120bfdc276fd87d))

## [5.4.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.4.0...v5.4.1) (2024-05-31)


### Dependencies

* remove `micrometer-tracing-bom` from pom.xml because this dependency is managed by spring-boot. ([8ebc064](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8ebc0642d2277c19b75e84ed67c35bd962e493bf))

## [5.4.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.3.0...v5.4.0) (2024-05-29)


### Features

* configurable expiration policy on auto-created subscriptions ([#2876](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2876)) ([@jmitash](https://github.com/jmitash)) ([#2897](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2897)) ([d8d7168](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d8d7168e57683adf76b1e56b1a3e7d87fb7d8bdf))


### Bug Fixes

* **deps:** update dependency com.google.cloud:alloydb-jdbc-connector to v1.1.2 ([#2883](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2883)) ([98fcfea](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/98fcfea7dbfc53fb04aa74e92253093b9b7d851d))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.40.0 ([#2924](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2924)) ([3fcd7e7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3fcd7e7586272df19469062aa39fb66dde5f6ec8))
* stackdriver logback appender now defaults to flushlevel off ([#2906](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2906)) ([#2922](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2922)) ([b5bd5d4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b5bd5d430f0c0a7e9389d6eaac432e9c2898622a))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.18.0 to 1.18.1 ([#2886](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2886)) ([d72b0ed](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d72b0ede8e609185752e5dec9df8f8dd51df9dc0))
* bump com.google.cloud:alloydb-jdbc-connector from 1.1.1 to 1.1.2 ([#2884](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2884)) ([f52dc7d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f52dc7d339d819e9e7bd0b7382ec6556a447187a))
* bump io.micrometer:micrometer-tracing-bom from 1.2.5 to 1.3.0 ([#2879](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2879)) ([990c511](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/990c51170bef40a42355a40834489dc386850f69))
* bump org.codehaus.mojo:build-helper-maven-plugin ([#2903](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2903)) ([6dde442](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6dde442f065841ec37a308b1d9d7640fc50f48e5))
* bump org.sonatype.plugins:nexus-staging-maven-plugin ([#2918](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2918)) ([c6edc2e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c6edc2e01cd40c2ea46fc81c17e93ac9837001ec))

## [5.3.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.2.1...v5.3.0) (2024-05-15)


### Features

* [#2576](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2576) Add spring-cloud-gcp-data-spanner support for Instant values ([@ablx](https://github.com/ablx)) ([#2881](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2881)) ([7d4bb44](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7d4bb4493423fb9c88c73125e96426ac71793a59))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.39.0 ([#2866](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2866)) ([c6d734b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c6d734bb87e3fc7e0b115c76ecadca2aa2e0c34f))
* update generate-library-list.sh for duplicate api_shortnames ([#2873](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2873)) ([035f2c3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/035f2c307c7f5634342aa419613ffa5142b72dbf))
* update sanity-checks.sh ([#2891](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2891)) ([2bd16e5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2bd16e55ee967c71196ffb8bba130358b5def969))


### Documentation

* spanner support for float32 type ([#2882](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2882)) ([4aca9e6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4aca9e6ef79d5777c51e0aab64c600e1d95186fd))

## [5.2.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.2.0...v5.2.1) (2024-05-06)


### Bug Fixes

* AlloyDB should not load when Postgres starter is used ([#2848](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2848)) ([2c8e5cb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2c8e5cbdd2d3ce75a01628c91efe2b28a9aa8776))

## [5.2.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.1.2...v5.2.0) (2024-05-02)


### Features

* Spring Cloud AlloyDB integration ([#2787](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2787)) ([#2788](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2788)) ([67d994e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/67d994e7f39372be7d0e9010e234f2fe46e68fd9))


### Bug Fixes

* BigQueryTemplate now closes its BigQueryJsonDataWriter ([#2765](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2765)) ([1e0c206](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1e0c206e941d39d0e490ac851c577422701dce02))
* **datastore:** guard against "Type must not be Null" ([#2797](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2797)) ([9a2ee15](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9a2ee15973a58b5dd2ca57821a4df8791d99b2fe))
* **deps:** update cloud-sql-socket-factory.version to v1.18.0 ([#2681](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2681)) ([65c528c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/65c528c8df1da9ec4296101ae4bb1f4f1c98a8e4))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.38.0 ([#2814](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2814)) ([bfd45b7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bfd45b76927a70a9397116c2b44e43408d42ce5d))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.2.5 ([#2809](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2809)) ([e974214](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e974214ad80c98d0ac161e149f62e86ea17bf50b))
* **deps:** update dependency org.postgresql:r2dbc-postgresql to v1.0.5.release ([#2810](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2810)) ([7eb0441](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7eb0441a334f4f921973cbffb4c21238b02c0c7e))
* Use specified bigQueryWriteClient when creating JsonStreamWriter ([#2711](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2711)) ([21657a7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/21657a751aa487797780b3866a335fc24b256310))


### Dependencies

* bump com.google.errorprone:error_prone_core from 2.26.0 to 2.27.0 ([#2798](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2798)) ([507832a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/507832a811f246b363005a930978640ee47ee83d))
* bump com.google.errorprone:error_prone_core from 2.27.0 to 2.27.1 ([#2835](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2835)) ([dcec57a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dcec57a75df72b512b68fa5366a121acdb9bf14c))
* bump commons-io:commons-io from 2.15.1 to 2.16.1 ([#2775](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2775)) ([2eb24aa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2eb24aa1cdbe76c6873baa7dc75d092ddc30b731))
* bump org.apache.maven.plugins:maven-compiler-plugin ([#2716](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2716)) ([8a0f03a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8a0f03a16e6394037600fc558d730b0283cf716d))
* bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.0 to 3.2.4 ([#2792](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2792)) ([4a550e6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4a550e69579448dafd26edd7bae2e982fbc4a5b1))
* bump org.awaitility:awaitility from 4.2.0 to 4.2.1 ([#2704](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2704)) ([072570b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/072570bc8089c296a92cc04a88632881236b0e48))
* bump org.jacoco:jacoco-maven-plugin from 0.8.11 to 0.8.12 ([#2767](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2767)) ([0a4d36d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0a4d36d4809f730ea7ad4d0b0d969f273c381fa2))
* bump org.springframework.boot:spring-boot-starter-parent ([#2789](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2789)) ([6b9c285](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6b9c2850862b759de042033c8f8ba9b8d4911a65))
* bump spring-boot-dependencies.version from 3.2.3 to 3.2.4 ([#2729](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2729)) ([78b3a68](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/78b3a6851b6856679fa899bda51991b45dd66d97))


### Documentation

* Add contribution guidelines ([#2771](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2771)) ([add0da7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/add0da7af8a71962b6f06544eaf15007c3c11877))
* Memorystore documentation improvements ([#2737](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2737)) ([b1e87b3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b1e87b32a636fd370fa30007dc7aa574539fac13))
* publisher executor config properties ([#2746](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2746)) ([590ec21](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/590ec2110082280f4580b8d3462e110edffa22c3))

## [5.1.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.1.1...v5.1.2) (2024-03-29)


### Dependencies

* bump org.springframework.cloud:spring-cloud-dependencies ([#2744](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2744)) ([a0d97da](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a0d97da63ced23327de55b6955deabfb7597cfe8))
* bump spring-cloud-config.version from 4.1.0 to 4.1.1 ([#2743](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2743)) ([631f39e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/631f39e99fedaded0909df273cd74bbc04b50beb))

## [5.1.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.1.0...v5.1.1) (2024-03-29)


### Bug Fixes

* add config options to facilitate late shutdown of pubsub publisher ThreadPoolTaskScheduler (GoogleCloudPlatform[#2721](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2721)) ([#2738](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2738)) ([13e4911](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/13e49116d4b33843120821b33a98a01ea80f9f4d))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.35.0 ([#2754](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2754)) ([cd3bc45](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cd3bc45c61a77f04adf503084302e0bb35fa1739))
* Update README.adoc on doc versions ([#2724](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2724)) ([10fd702](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/10fd702c18ca39e9f030050c87993377e8ba7041))

## [5.1.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.0.4...v5.1.0) (2024-03-12)


### Features

* **spanner:** support float32 type. ([4cad2f6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4cad2f6b7c3e9d2c30e5f3d41360c71527e4dabe))


### Bug Fixes

* **deps:** update cloud-sql-socket-factory.version to v1.16.0 ([#2643](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2643)) ([67d54f0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/67d54f0a7437ffd38aab17f8d98dc75e170007b2))
* Use GoogleCredentials to fetch user credentials for Cloud SQL ([#2644](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2644)) ([5eb77b4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5eb77b450a925d7c5846fe3966e246182380a4f3))


### Dependencies

* bump com.google.cloud:libraries-bom from 26.33.0 to 26.34.0  ([4cad2f6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4cad2f6b7c3e9d2c30e5f3d41360c71527e4dabe))
* bump io.micrometer:micrometer-tracing-bom from 1.2.3 to 1.2.4 ([#2677](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2677)) ([414e54e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/414e54ef2e8eeee4afe9a7462b49aff7e5c129d7))

## [5.0.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.0.3...v5.0.4) (2024-02-23)


### Bug Fixes

* configure the ObjectMapper to prevent from flaky behavior in the test case testPublish_Object ([#2340](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2340)) ([8f3afad](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8f3afad9750f779ea1fff6b7fdc0e091e191decf))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.33.0 ([#2622](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2622)) ([51f1d6d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/51f1d6d8c788508786f8655ded878d97edf37694))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.2.3 ([#2589](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2589)) ([c9f833d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c9f833dde1da29ff03f17ff82b36a2bc587d5fc8))
* metrics documentation wording in Metrics Disambiguation section. ([#2476](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2476)) ([f6b5411](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f6b5411dcbfe2789b8f085032017b387e0b6c379))
* use bazelisk info to obtain googleapis bazel-bin when generating… ([#2635](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2635)) ([3e39459](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3e39459d45e60e65ae684a22e8bc1ff91b24d799))


### Dependencies

* bump com.google.errorprone:error_prone_core from 2.24.1 to 2.25.0 ([#2616](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2616)) ([8840612](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8840612cb430dccfa8b57bef6477b21feae39d28))
* bump io.micrometer:micrometer-tracing-bom from 1.2.2 to 1.2.3 ([#2591](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2591)) ([eb14314](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/eb14314ac07bf0823995a89cc12e0fcd3a8b8269))
* bump org.graalvm.buildtools:native-maven-plugin ([#2619](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2619)) ([7c1ed93](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7c1ed93d8a343217f2f36e624a75e05be6ea679f))
* bump org.springframework.boot:spring-boot-starter-parent ([#2638](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2638)) ([b405965](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b405965f553c7ba04ff39b1bc96276580d7384d2))
* bump spring-boot-dependencies.version from 3.2.2 to 3.2.3 ([#2639](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2639)) ([fa5ab16](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fa5ab16e0b276043c2a9d757be6d9367fff83973))

## [5.0.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.0.2...v5.0.3) (2024-02-15)


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.32.0 ([#2588](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2588)) ([750ae11](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/750ae115c14c48f0fd87e01cfce4ccdcaf3230c6))
* transfer `TestProtoLoader.parseShowcaseEcho()` to autogen tests ([#2603](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2603)) ([72a4f76](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/72a4f766930abe342d773cf51390ce30d802b4cf))

## [5.0.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.0.1...v5.0.2) (2024-02-05)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.30.0 to 26.31.0 ([#2565](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2565)) ([6efd6d0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6efd6d0de86cbdfba3df8e60ae0cac81d5e6c4d7))
* bump com.google.cloud.tools:appengine-maven-plugin ([#2558](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2558)) ([908c115](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/908c115afdd729cdf4abc9e0363475e77a5945a6))
* bump com.google.truth:truth from 1.3.0 to 1.4.0 ([#2578](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2578)) ([796cde7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/796cde7c58210e27df0525246aa7fa884137da7a))
* bump org.graalvm.buildtools:native-maven-plugin ([#2572](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2572)) ([cd98502](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cd98502c12d74c1ce3df6ff75c84bdf19f0198c5))

## [5.0.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v5.0.0...v5.0.1) (2024-01-26)


### Bug Fixes

* (Spanner) include schema name in table name ([#2510](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2510)) ([5dfe226](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5dfe2260c60203970ea7cff917e9bf15c528ddfa))
* delegate to all parent logging options ([#2500](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2500)) ([cd706b7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cd706b7b5c845461dccede0ce615310261dd6ab8))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.30.0 ([#2440](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2440)) ([7d11582](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7d11582dd8cc4bc0925cbf030eb28def43144e27))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.2.2 ([#2423](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2423)) ([07ade87](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/07ade87dae3b5f63f9c9ec4299a6c700ac4f619e))
* **deps:** update dependency org.postgresql:r2dbc-postgresql to v1.0.4.release ([#2430](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2430)) ([4bd907f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4bd907f9eecdd31ff22ddea2d3483d2a0c5dc543))
* **deps:** update zipkin-gcp.version to v1.1.1 ([#2475](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2475)) ([675da8e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/675da8eb396fd3f2305749e5a379b5e93b929b02))


### Dependencies

* bump com.google.cloud.tools:appengine-maven-plugin ([#2524](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2524)) ([31b8ea2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/31b8ea27fa474739bc67208a18eab8797dd5e027))
* bump com.google.errorprone:error_prone_core from 2.23.0 to 2.24.1 ([#2551](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2551)) ([e23e488](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e23e48852350f089873c33c389db630a1b7bba20))
* bump io.micrometer:micrometer-tracing-bom from 1.2.0 to 1.2.2 ([#2507](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2507)) ([838bb0d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/838bb0d90c9bfed125615833d84dec6de744338d))
* bump org.apache.maven.plugins:maven-compiler-plugin ([#2499](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2499)) ([d0aa4b3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d0aa4b3079942ca9a1013162a12850e3d34cba73))
* bump org.codehaus.mojo:flatten-maven-plugin from 1.5.0 to 1.6.0 ([#2550](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2550)) ([0594973](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0594973fc8a2172bcc131055b58fa2b7d3ba7b72))
* bump org.postgresql:r2dbc-postgresql ([#2508](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2508)) ([9324393](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/932439388a5057d5eedd4ac60b5670fbf2eb08fc))
* bump org.springframework.boot:spring-boot-starter-parent ([#2529](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2529)) ([8908612](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/890861240ec8435c631ba4d38c67a29e9c409c8f))
* bump spring-boot-dependencies.version from 3.2.0 to 3.2.2 ([#2549](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2549)) ([20148b9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/20148b95d57163f6a5231f99c88cd983eb8d3474))
* bump zipkin-gcp.version from 1.0.4 to 1.1.0 ([#2482](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2482)) ([c3a835d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c3a835d20d7ed7597008848d271b99700df702ce))
* bump zipkin-gcp.version from 1.1.0 to 1.1.1 ([#2552](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2552)) ([f7b4d1e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f7b4d1eda2981f9ca1983436b88fd9b7708eae4e))


### Documentation

* Improved CHANGELOG.md for 5.x ([#2466](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2466)) ([c30ae65](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c30ae65024bb52efcd77da7bf6aeb7c14351d92c))
* update compatibility table in docs ([#2487](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2487)) ([8ed8ecd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8ed8ecd756a1a11db97bca88abf9fbaecc3e68fe))
* Update SECURITY.md for 5.x ([#2465](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2465)) ([5ac7cc9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5ac7cc92b18d03af8cd1f4475338908782724315)), closes [#2460](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2460)

## [5.0.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.9.0...v5.0.0) (2023-12-13)

### ⚠ BREAKING CHANGES

* Spring Cloud 2023 and Spring Boot 3.2.0 ([#2438](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2438))

### 🎉 Get Ready to Explore!
[Spring Boot 3.2.0 Upgrade](https://spring.io/blog/2023/11/23/spring-boot-3-2-0-available-now): 
Upgrade today and unlock the power of Spring Boot 3.2.0! For detailed information and instructions, 
refer to the official Spring Boot [documentation](https://spring.io/projects/spring-boot#learn) and 
[release notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes).


## [4.9.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.8.4...v4.9.0) (2023-12-12)


### Features

* only commit change if it's not a snapshot version ([#2370](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2370)) ([a50fb4b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a50fb4ba83f618d77342a013125ae81bc8c82dc5))


### Bug Fixes

* add metrics sample to native CI ([#2345](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2345)) ([44d48df](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/44d48df4c56fc737f06d8e121b428b26aa309e8e))
* backport Spring Boot 3.2 compatibility updates to Spring Cloud GCP 4.x ([#2397](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2397)) ([a640b75](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a640b758ad8d8b7f1d36b720b5efa2b6cc26f29d))
* disable profile `CI` when building reference docs ([#2367](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2367)) ([d207fd8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d207fd8cfeacc37dda65dfd70de156487dbf3a83))
* GcpFirestoreEmulatorAutoConfiguration constructs invalid document parent ([#2348](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2348)) ([7e66d55](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7e66d55f8c48573b03494ad251535c466c63a70e))


### Dependencies

* bump com.google.cloud:libraries-bom from 26.27.0 to 26.28.0 ([#2414](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2414)) ([f58a315](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f58a3153c42599225c308d087743408e509a46ec))
* bump commons-io:commons-io from 2.15.0 to 2.15.1 ([#2393](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2393)) ([fd56746](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fd56746f8c909e49b0b897d525f33b88c17379e6))
* bump io.micrometer:micrometer-tracing-bom from 1.1.6 to 1.2.0 ([#2349](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2349)) ([bfc58bd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bfc58bd1b9e4cf9d837b04a2ab477b78e2a4ad49))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2401](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2401)) ([2b64f9a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2b64f9ab8a4485e352e4de36afd23e068521345b))
* bump org.codehaus.mojo:build-helper-maven-plugin ([#2381](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2381)) ([9045e48](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9045e48291817299a8c13987de05a52447be46cf))


### Documentation

* fix readme to reflect Spring Boot 3.0.x & 3.1.x compatibility ([#2402](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2402)) ([20d536e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/20d536e018bc8fd4c12a6d585cac81f4ab583f15))

## [4.8.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.8.3...v4.8.4) (2023-11-09)


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.27.0 ([#2331](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2331)) ([22a817d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/22a817d45769598995f047a3b4856f8c74194b3b))


### Dependencies

* bump com.google.cloud:cloud-spanner-r2dbc from 1.2.2 to 1.3.0 ([#2316](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2316)) ([2afa03a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2afa03a3e0514dd89309e07eac0bd152054baca0))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2329](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2329)) ([f212e4a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f212e4a37492d587ff33f943c3325e633297daa7))

## [4.8.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.8.2...v4.8.3) (2023-10-27)


### Bug Fixes

* add back original comment. ([#2263](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2263)) ([dc1b821](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dc1b82167f9be99cf86bac34c55defcc41485c92))
* **deps:** update cloud-sql-socket-factory.version to v1.14.1 ([#2241](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2241)) ([e2527ae](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e2527ae6b39400516bc4cf155c0ba980cc23598d))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.26.0 ([#2297](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2297)) ([5b7db44](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5b7db44f4c73eb70c4a8e5fa6faaa21d875fc4a3))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.1.6 ([#2232](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2232)) ([06ac904](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/06ac9041884ad40b62d166c7fd3e70a0b5ccafbc))
* Ensure topic exists in consumer only if autoCreateResources is true ([#2296](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2296)) ([c8d25c8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c8d25c86492dd59784f407683f883fab274c6d93))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.14.0 to 1.14.1 ([#2243](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2243)) ([79dfcfe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/79dfcfe7d94bb7f69f39ca1facdddff258b05f7f))
* bump com.google.cloud.tools:appengine-maven-plugin ([#2226](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2226)) ([db0d63e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/db0d63eaa3e32dc0abf9ab8841e72afb04945c31))
* bump com.google.errorprone:error_prone_core from 2.22.0 to 2.23.0 ([#2268](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2268)) ([45ebb90](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/45ebb9003218fc2105b73bbbbc3f160969f42b0a))
* bump commons-io:commons-io from 2.14.0 to 2.15.0 ([#2292](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2292)) ([3b32c77](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3b32c772ac630106a51ed4fbc1e9505c5ce09724))
* bump io.micrometer:micrometer-tracing-bom from 1.1.5 to 1.1.6 ([#2234](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2234)) ([8970fb2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8970fb238db59cdc9643db49a0fa2d50d4395146))
* bump org.apache.maven.plugins:maven-checkstyle-plugin ([#2287](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2287)) ([7f56ce0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7f56ce029d1f0c938b41ef13f023ff6711071ebb))
* bump org.graalvm.buildtools:native-maven-plugin ([#2282](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2282)) ([2e32f5f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2e32f5fd5906c2122b7f551a62fe5aa0377a2805))
* bump org.jacoco:jacoco-maven-plugin from 0.8.10 to 0.8.11 ([#2253](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2253)) ([5e05fc2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5e05fc2753bb79f0c8561fc06e9eccb728be7398))

## [4.8.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.8.1...v4.8.2) (2023-10-17)


### Bug Fixes

* allow minimal permissions for consumer destination use ([#2233](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2233)) ([d5a42c4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d5a42c4c6035c9b61af129586e87baa9f244984f))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.25.0 ([#2249](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2249)) ([903c3c7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/903c3c7619515d7d9ef4a7afa094e184b7771970))
* remove unnecessary topic name comparison on subscription ([#2239](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2239)) ([f0f0124](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f0f0124e06e20cdc23d6baf24ecdc1fab5b3f8a2))

## [4.8.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.8.0...v4.8.1) (2023-10-03)


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.24.0 ([#2213](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2213)) ([4408d63](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4408d630c1312fbe0c4551083f93ff97dad8c0e1))
* guard against null projectId in Firestore routing header ([29229f7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/29229f726218228a9ed424e35644a1305b25b545))
* language test moved to `v2` ([#2216](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2216)) ([ddb2398](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ddb2398204e21d12886b73ff0370e8e158ca3d78))


### Dependencies

* bump com.google.errorprone:error_prone_core from 2.21.1 to 2.22.0 ([#2202](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2202)) ([9759fd7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9759fd7166ad4cba262721d2160fade258a7602c))
* bump commons-io:commons-io from 2.13.0 to 2.14.0 ([#2211](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2211)) ([efc116c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/efc116cf38a528c493097f978680fbfdc0b20d65))
* bump org.springframework.boot:spring-boot-starter-parent ([#2203](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2203)) ([a86e485](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a86e485bc9b5a9ac16c0d4032bec5cf2b4bd7c13))
* bump spring-boot-dependencies.version from 3.1.3 to 3.1.4 ([#2201](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2201)) ([e763569](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e7635694c11af39802032afd8c61e9bd73a7f8e0))

## [4.8.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.7.2...v4.8.0) (2023-09-20)


### Features

* add support for Datastore database name configuration ([#2150](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2150)) ([dc45fd8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dc45fd8df0996ef82d7e869c696b5156acf8d872))
* add support for Firestore database id configuration ([#2164](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2164)) ([1ea0cfb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1ea0cfb5769a132af43440d96eb7d23b063d3d03))


### Bug Fixes

* add runtime hints for spanner  ([#2123](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2123)) ([170948e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/170948e55dea17aadcb9685ac4b0a56f8c8846b2))
* **deps:** update cloud-sql-socket-factory.version to v1.14.0 ([#2160](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2160)) ([21a1561](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/21a15615b465e6f572972100d8712e4cd1785d8d))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.1.5 ([#2124](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2124)) ([0cf3d72](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0cf3d72dab23711b0bec78102798b2ab8a350ecd))
* Ensure proper merging of Binder default props ([#2177](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2177)) ([01b3dad](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/01b3dadde3561f03dfa7ae7270276d28e2f62d03))
* Firestore updateTime extraction after commit ([#2165](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2165)) ([bacdfe9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bacdfe997ab522a345fdb288be7c3dd417c5b7a0))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.13.1 to 1.14.0 ([#2161](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2161)) ([c1a7cac](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c1a7cac61727c98d925d973e22d9d25a93e61531))
* bump com.google.cloud:libraries-bom from 26.22.0 to 26.23.0 ([#2166](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2166)) ([a4050ec](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a4050eca4672e01e22f3add01c086f25f693b973))
* bump com.google.cloud:libraries-bom from 26.22.0 to 26.23.0 ([#2183](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2183)) ([70edcc8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/70edcc8d0eaafdf3702eb2ff1fa34d9a31590f73))
* bump io.micrometer:micrometer-tracing-bom from 1.1.3 to 1.1.5 ([#2159](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2159)) ([fdbad48](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fdbad48b260344264d8fa3c6efcbef759672b2ab))
* bump java-cfenv.version from 2.4.2 to 2.5.0 ([#2131](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2131)) ([a7aac12](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a7aac12a5f0b27012818a9bf0ab1c337a42ce984))
* bump org.apache.maven.plugins:maven-enforcer-plugin ([#2155](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2155)) ([dfa4f63](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dfa4f63c5f2c11648dde05c1cbd75ff66cc90792))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2171](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2171)) ([7ebb496](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7ebb49698f922098cc576f23ace78f2224e11972))
* bump org.graalvm.buildtools:native-maven-plugin ([#2170](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2170)) ([a87a7d2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a87a7d213c986b6fe369cab31ad83f85d3d6b5d8))
* bump org.springframework.boot:spring-boot-starter-parent ([#2139](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2139)) ([f18bf41](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f18bf412c5b57b7d9eb78045495ba0543ccd1c3d))
* bump spring-boot-dependencies.version from 3.1.2 to 3.1.3 ([#2138](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2138)) ([ef412db](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ef412db2c443a13fbae7b49e1ecdac9c7a0fe3a1))

## [4.7.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.7.1...v4.7.2) (2023-08-10)


### Bug Fixes

* add runtime hints for storage ([#2001](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2001)) ([793c798](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/793c79874a49a20d0cb0023d69e68b9b76000409))
* add runtime hints for trace ([#1990](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1990)) ([2ba4a75](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2ba4a75bc58fbc48a3f92f6bd6d79a1fd46a905b))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.22.0 ([#2110](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2110)) ([1806818](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/18068189dd47162690b86f230df894ad432ca028))


### Dependencies

* bump com.google.errorprone:error_prone_core from 2.20.0 to 2.21.1 ([#2104](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2104)) ([0c9718a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0c9718a3010cf8be750e756a3c0a2064fc18db73))
* bump gapic-generator-java-bom.version from 2.23.1 to 2.24.0 ([#2097](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2097)) ([67a5756](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/67a57563b1f2b1b5613496835ddb695af286e918))
* bump org.graalvm.buildtools:native-maven-plugin ([#2107](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2107)) ([f7964c5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f7964c51db2b197fd4c45ce6558a61da985a370d))

## [4.7.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.7.0...v4.7.1) (2023-08-01)


### Bug Fixes

* add missing exception cause in createSecretManagerClient ([#1923](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1923)) ([154cbe5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/154cbe5c7a4f3be6eba76e6607ae5fa0528bf909))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.21.0 ([#2088](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2088)) ([e352ad5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e352ad58cb5a23825e213125fbfbc306631fdcb2))

## [4.7.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.6.0...v4.7.0) (2023-07-28)


### Features

* **main:** Migrate spanner spring data r2dbc ([#2080](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2080)) ([f5e3c7a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f5e3c7a5d1033d533e40fd1d6297bcd2fd19dbcb))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.20.0 (main) ([#2071](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2071)) ([ca9d1fb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ca9d1fbbdd6ebc4adffbc67b5b6fa71ae6b3ad48))
* **deps:** update spring cloud ([#2076](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2076)) ([64561c7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/64561c7172617e887af003addae0823c701a0dd2))


### Dependencies

* stop forcing Guava version ([#2069](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2069)) ([308c56d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/308c56d0a029915b211d8242cc2bd0a8ffeece9c))

## [4.6.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.5.1...v4.6.0) (2023-07-24)


### Features

* Add overloaded `executePartitionedDmlStatement` to support update options ([#2025](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2025)) ([df65e54](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/df65e547278b27cc8dff5e70f140f722ed81c8c3))


### Bug Fixes

* add runtime hints for logging ([#1933](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1933)) ([21903af](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/21903af1a1fde4e64a3f45588ef4ad38a8dd14c2))
* add runtime hints for vision ([#1991](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1991)) ([dcbf202](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dcbf202e431963c78a5f9e5aa056f2d1c579a994))
* **deps:** update cloud-sql-socket-factory.version to v1.13.1 ([#2020](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2020)) ([fe69d8a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fe69d8aba9d64d948639d757bbb6b3d69abd7c2f))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.19.0 ([#2033](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2033)) ([c215536](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c215536b25418090451838138388d86b828cd046))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.1.3 ([#2016](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2016)) ([465a88b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/465a88b7c19824ebfbfd16cb019d81900ea31b05))
* **deps:** update dependency org.jetbrains.kotlin:kotlin-stdlib-jdk8 to v1.9.0 ([#2002](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2002)) ([d715085](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d71508562b8d01d1d84569a313ebeb853dd689c5))
* **deps:** update dependency org.postgresql:r2dbc-postgresql to v1.0.2.release ([#2026](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2026)) ([a76298a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a76298a9ae2c5c743416823400b934a47669bf65))
* **deps:** update dependency org.springframework.cloud:spring-cloud-build to v4.0.4 ([#2004](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2004)) ([2ce3e1f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2ce3e1f1942cd95329b20265368ffdc02b5588d2))
* timestampNanos field with nanos precision ([#2012](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2012)) ([2a4c14a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2a4c14ab43b99c80879d94779bde943bd3e3ba78)), closes [#1996](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1996)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.12.0 to 1.13.1 ([#2047](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2047)) ([109255b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/109255b9c8fb5c42818cb0eee318927ef09d367d))
* bump libraries-bom from 26.18.0 to 26.19.0 ([#2032](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2032)) ([b36e847](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b36e847c7d59fe4f57e152510bc42e1fb4a4e9df))
* bump org.junit.platform:junit-platform-launcher ([#2048](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2048)) ([43e9422](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/43e942248f53731d1b6302f3937e091c7645650f))
* bump org.springframework.boot:spring-boot-starter-parent ([#2040](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2040)) ([28307cb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/28307cbf0688bab6a8394b41af8445c0f70b50dd))
* bump r2dbc-postgresql from 1.0.1.RELEASE to 1.0.2.RELEASE ([#2029](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2029)) ([912edfe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/912edfe0f580cbc7adf6035e05bd373846d0d471))
* bump spring-boot-dependencies.version from 3.1.1 to 3.1.2 ([#2041](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2041)) ([0ea6195](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0ea6195f0523a8b2656e9339cb4175faf0833fa7))
* manual  bump gapic-generator-java-bom.version from 2.22.0 to 2.23.1 ([#2044](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2044)) ([08946d5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/08946d581badc98c5fc84c2b7caa5f784062e161))

## [4.5.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.5.0...v4.5.1) (2023-06-28)


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.18.0 (main) ([#1985](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1985)) ([3445ce7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3445ce7d8e73708e99450646c843cc60e2e2f3f4))
* Wiring `bigQueryThreadPoolTaskScheduler ` with `writeJsonStream` ([#1855](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1855)) ([6467a08](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6467a089e235efbebfbe5f38c83f1d18bebb9b78)), closes [#1599](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1599)


### Dependencies

* bump gapic-generator-java-bom.version from 2.21.0 to 2.22.0 ([#1978](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1978)) ([0cb44fe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0cb44fe2e9027b4575f49aaef21aca0b2d9990d3))
* bump native-maven-plugin from 0.9.22 to 0.9.23 ([#1968](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1968)) ([d60fa50](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d60fa509788ea03c2fda7716719670027cc33d6e))
* bump spring-boot-dependencies.version from 3.0.6 to 3.1.0 ([#1876](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1876)) ([39b6f1e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/39b6f1e72cd34fa8126fce54a48a02fc189d24bf))
* bump spring-boot-dependencies.version from 3.0.6 to 3.1.1 ([#1977](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1977)) ([c2622f4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c2622f485b38a6319685a6666796664d5a758f04))
* bump spring-boot-starter-parent from 3.0.6 to 3.1.1 ([#1976](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1976)) ([0600e68](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0600e68ece0ad78f31cc375965b04501a5d7a1dc))
* bump truth from 1.1.4 to 1.1.5 ([#1967](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1967)) ([fb20cd4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fb20cd483fc6c1ca271b9f9f6d2fb4c55f151108))

## [4.5.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.4.0...v4.5.0) (2023-06-15)


### Features

* add google-cloud-compute-spring-starter to starter modules available for preview ([41b5c19](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/41b5c19856e059a193b6fcacf63951218fb17eed))


### Bug Fixes

* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.1.2 ([#1943](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1943)) ([2240e62](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2240e6248fead31c483b2e2027f8d6c3f271fa48))
* Remove misleading Spanner DEBUG log for query elapsed time ([#1954](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1954)) ([51352ad](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/51352adf5f53a9e150cc4113b2a211180ba628c3)), closes [#1945](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1945)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.11.1 to 1.12.0 ([#1947](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1947)) ([b60efaf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b60efaf925e7cf56a8efeb12a910b1d63c581261))
* bump commons-io from 2.12.0 to 2.13.0 ([#1934](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1934)) ([da5c93a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/da5c93a66695580a28a4b8ec75418d7e360ee83a))
* bump libraries-bom from 26.16.0 to 26.17.0 ([#1948](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1948)) ([5beb0e6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5beb0e6133710cd445742aa9a3035affcdc53bde))
* bump micrometer-tracing-bom from 1.1.1 to 1.1.2 ([#1944](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1944)) ([c0bb763](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c0bb763974e7296c0eb5a1a10afb76ead163c9c9))
* bump truth from 1.1.3 to 1.1.4 ([#1942](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1942)) ([79402f5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/79402f59b1c289836c662a45ca751e31588d8214))

## [4.4.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.3.1...v4.4.0) (2023-06-02)


### Features

* allow Spanner and Datastore Transaction Manager in same project ([#1412](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1412)) ([f937b36](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f937b36efb442e5294a357c1a5a14e529aac3fa4))


### Bug Fixes

* additional sample test for both transactionManager work together. ([#1848](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1848)) ([219adb3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/219adb3e18a8153917e6b1355b2cee7e7408feff))
* **deps:** update dependency com.google.api:gapic-generator-java-bom to v2.20.0 ([#1895](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1895)) ([af9b81f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/af9b81f8df40ab0ae6c02ecbefe7c2fa07437042))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.16.0 (main) ([#1918](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1918)) ([4fe4034](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4fe4034b522d380f8290d88b98e3636f11517829))
* **deps:** update spring cloud ([#1901](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1901)) ([0a3827a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0a3827a6e8b9fd05374a2fe99a0990563e5f0241))
* pull-endpoint setting for async subscribers ([#1883](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1883)) ([f0c5d4c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f0c5d4c728d152469847cfba1134129130e16551))


### Dependencies

* bump commons-io from 2.11.0 to 2.12.0 ([#1867](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1867)) ([7fd064a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7fd064a5769b57af4d0ba407d66cd45a97aa5811))
* bump gapic-generator-java-bom.version from 2.20.0 to 2.20.1 ([#1905](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1905)) ([745cdd5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/745cdd5fc0273769663ca1a9a571706cedeefeca))
* bump maven-checkstyle-plugin from 3.2.2 to 3.3.0 ([#1889](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1889)) ([2ab9844](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2ab984489a1c5eebca6091726d13df2473bfe845))
* bump maven-source-plugin from 3.2.1 to 3.3.0 ([#1886](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1886)) ([5937b07](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5937b07cbaffdf0652f47ab2850e8671de5fccdc))
* bump spring-cloud-dependencies from 2022.0.2 to 2022.0.3 ([#1904](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1904)) ([99fcdb5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/99fcdb57a368e233b59e54af1aa5c47573e4108d))

## [4.3.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.3.0...v4.3.1) (2023-05-17)


### Bug Fixes

* add spring-boot-configuration-processor to maven-compiler-plugin configuration ([#1846](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1846)) ([7553d04](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7553d04ea10a879d4a7d799f4c1edf03c69bd65a))
* **deps:** update dependency com.google.api:gapic-generator-java-bom to v2.19.0 ([#1829](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1829)) ([d3b5e4f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d3b5e4f4e0c6d7c760ef291ce4c940fbd6494385))
* **deps:** update dependency com.google.cloud:libraries-bom to v26.15.0 (main) ([#1865](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1865)) ([40303fc](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/40303fc936828bc7800a5bf05e722fcb4e9a4255))
* **deps:** update dependency io.micrometer:micrometer-tracing-bom to v1.1.1 ([#1826](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1826)) ([272d491](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/272d491b3cfb5997629d90c6397f73db537fac73))
* **deps:** update java-cfenv.version to v2.4.2 ([#1807](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1807)) ([7af5fe1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7af5fe1bc0d52b6a764751f0407bfd6bf0207bc7))
* Implementing count with aggregation query ([#1782](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1782)) ([1ee2244](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1ee2244c9b82980948be0a5d3bac38f76857dfe7)), closes [#1781](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1781)
* PubSubPublishCallback handling of success and failure callbacks ([#1800](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1800)) ([b134c92](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b134c9274dee07a453ab9bdc1b08976dc48da39a))
* use core id provider as backup provider ([#1798](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1798)) ([1b04165](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1b04165795b339de4cde5e79343c0da47ee55605))


### Dependencies

* bump error_prone_core from 2.18.0 to 2.19.1 ([#1837](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1837)) ([5424e50](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5424e50874dffab5dc5a8317da3135f020a565bb))
* bump flatten-maven-plugin from 1.4.1 to 1.5.0 ([#1831](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1831)) ([bdd07a6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bdd07a6e85e2421217c7738882c68b1671aef820))
* bump gapic-generator-java-bom from 2.18.0 to 2.19.0 ([#1833](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1833)) ([413f945](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/413f945013a77f0230660d04266e6f174e8539c9))
* bump java-cfenv.version from 2.4.1 to 2.4.2 ([#1813](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1813)) ([c5e13f2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c5e13f2b2f6ab0e4336da90c15845126d114d591))
* bump maven-gpg-plugin from 3.0.1 to 3.1.0 ([#1814](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1814)) ([4e5cf5f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4e5cf5f50c39c11f8979fb2c48cd68835a15a879))
* bump micrometer-tracing-bom from 1.0.4 to 1.1.1 ([#1847](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1847)) ([12b90db](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/12b90db5c1bdbe822b0907549f21bae5f1ba9117))

## [4.3.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.2.0...v4.3.0) (2023-05-04)


### Features

* add support for insert and insertAll in DatastoreOperations ([#1729](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1729)) ([a478264](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a478264935743311ac9bb1d22f95553d317d84a5))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.14.0 (main) ([#1788](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1788)) ([2aa3992](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2aa3992f369c4c77eb18cdbace3d4cbf6b4339ca))
* **deps:** update dependency org.springframework.cloud:spring-cloud-build to v3.1.7 ([#1778](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1778)) ([3ea39c8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3ea39c86e5faf1a7848e270fb9dca722e596eb34))
* profile for jdk17 compiler arguments ([#1775](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1775)) ([277f814](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/277f8144fd98fbad917b5d80dea98c12e10f85c5))

## [4.2.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.1.4...v4.2.0) (2023-04-20)


### Features

* reconfigure `ZipkinAutoConfiguration` ([#1728](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1728)) ([2fe61c4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2fe61c4a6739fd0567a7229a095856bd3f2c2ab0))


### Bug Fixes

* **deps:** update dependency com.google.cloud:libraries-bom to v26.13.0 (main) ([#1755](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1755)) ([56fee5b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/56fee5b2c5d5b92e02c98ec4e6d8c3541a2e40a2))
* **deps:** update spring boot to v3.0.6 (main) ([#1747](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1747)) ([f230b7b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f230b7b34e2e4a062a819af4b46f7c1a92e9b5f1))
* pass `enable-iam-auth=true` to `spring.r2dbc.properties` ([#1715](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1715)) ([d0c4589](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d0c45898d7ae049593041197e258e1201225e4d8))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.11.0 to 1.11.1 ([#1723](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1723)) ([71105d4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/71105d4da18bb301b68c2d8e484709398f763dcd))
* bump flatten-maven-plugin from 1.4.0 to 1.4.1 ([#1678](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1678)) ([5596ac8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5596ac8a80951c958d9e6c78063314508ed41690))
* bump maven-enforcer-plugin from 3.2.1 to 3.3.0 ([#1696](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1696)) ([1ae1c36](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1ae1c36c2d20fad8a14f6704ca4e543f0e0cd22d))
* bump micrometer-tracing-bom from 1.0.3 to 1.0.4 ([#1717](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1717)) ([4246398](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/424639848553e03c84ae87ef4d65a55aa724423c))
* bump spring-cloud-config.version from 4.0.1 to 4.0.2 ([#1681](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1681)) ([b1ebb57](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b1ebb577126063c6faa5c116dcb67a04ea862b7f))
* bump spring-cloud-dependencies from 2022.0.1 to 2022.0.2 ([#1680](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1680)) ([a9480e2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a9480e2ae7d947127bb838e0aaf0864788df495e))
* bump testcontainers-bom from 1.17.6 to 1.18.0 ([#1694](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1694)) ([ab108bf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ab108bf4b5cc9eb82208c580cef47ce0132b1ca6))

## [4.1.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.1.3...v4.1.4) (2023-04-05)


### Bug Fixes

* fix secret manager docs ([#1687](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1687)) ([2b7a744](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2b7a7448f6c11f0bfb6358e7fae97cb4ed4ba787))


### Dependencies

* bump libraries-bom from 26.11.0 to 26.12.0 ([#1695](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1695)) ([4f5598c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4f5598ca6236724f9e7f718e831f3c11759a2317))
* bump maven-resources-plugin from 3.1.0 to 3.3.1 ([#1673](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1673)) ([272c68d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/272c68d436aca2fecd85c3033b367d9b9974d9d7))

## [4.1.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.1.2...v4.1.3) (2023-03-24)


### Dependencies

* bump flatten-maven-plugin from 1.3.0 to 1.4.0 ([#1662](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1662)) ([3b95f12](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3b95f12a3d37a2c5d46a468c3426dbc3e00fa8a9))
* bump libraries-bom from 26.10.0 to 26.11.0 ([#1668](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1668)) ([d3cfb98](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d3cfb987261f349717773ab3807eab4bd6819d5a))
* bump micrometer-tracing-bom from 1.0.2 to 1.0.3 ([#1651](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1651)) ([79f4d4a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/79f4d4aca7a2ce710a5cb1e213cea984ccf77a1d))
* bump spring-boot-dependencies from 3.0.4 to 3.0.5 ([#1669](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1669)) ([3a0d3fb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3a0d3fb0828f548a06fd22a45441fcc0a9140583))
* bump spring-boot-starter-parent from 3.0.4 to 3.0.5 ([#1667](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1667)) ([393fd86](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/393fd86f1f92913ee4eec56bcd413c7d8221c156))

## [4.1.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.1.1...v4.1.2) (2023-03-08)


### Bug Fixes

* provide setters for min/max duration per ack ext ([#1644](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1644)) ([cd3a75c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cd3a75c5cbf6459cbfb46ab72d45b0cca2562109))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.10.0 to 1.11.0 ([#1634](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1634)) ([cf24325](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cf24325fdcbc681bc3544ea8081f45f891ea91fc))
* bump libraries-bom from 26.9.0 to 26.10.0 ([#1647](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1647)) ([33d131b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/33d131bc1e0799693e6b4052fd78f55483e77fac))
* bump spring-boot-dependencies from 3.0.3 to 3.0.4 ([#1642](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1642)) ([a9db5bd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a9db5bdf2b05e35428e897dee1b5c7c3d11ad335))
* bump spring-boot-starter-parent from 3.0.3 to 3.0.4 ([#1641](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1641)) ([7f89063](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7f89063fe3675be2cffcb4f18d34f595123c7012))
* bump spring-cloud-build from 3.1.5 to 3.1.6 ([#1625](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1625)) ([f75a8bf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f75a8bf0f2c87a121fa5091ed6d89b5ca8034f7d))

## [4.1.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.1.0...v4.1.1) (2023-02-24)


### Bug Fixes

* update starter modules in spring-cloud-previews ([abfd34b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/abfd34bd3ae5cf83a5bdc6942a6f42b35d4341a2))


### Documentation

* fix secret-manager docs ([19ffedb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/19ffedbde9c4e7744ce23bf77c4463b09e463477))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.9.0 to 1.10.0 ([#1585](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1585)) ([f046398](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f046398011ec8b84bc9f586de39acdde6a6b2e08))
* bump libraries-bom from 26.7.0 to 26.9.0 ([abfd34b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/abfd34bd3ae5cf83a5bdc6942a6f42b35d4341a2))
* bump maven-javadoc-plugin from 3.4.1 to 3.5.0 ([#1596](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1596)) ([79cf1ba](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/79cf1baf35f3cbf36f5217e29bdfa885d94bac5c))
* bump micrometer-tracing-bom from 1.0.1 to 1.0.2 ([#1595](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1595)) ([f226afc](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f226afcb34cfff3bef85f979c6e78f25db885ed5))
* bump r2dbc-postgresql from 1.0.0.RELEASE to 1.0.1.RELEASE ([#1598](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1598)) ([e6fe51c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e6fe51c3848d1e43772388195bb2fbbc2658b4ca))
* bump spring-boot-dependencies from 3.0.2 to 3.0.3 ([#1612](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1612)) ([8183160](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/818316091c62f1c425c4a1e2cadf97ea40df8e9e))
* bump spring-boot-starter-parent from 3.0.2 to 3.0.3 ([#1614](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1614)) ([cde91e5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cde91e505d6bd2649ff0e2be511b216e52d51d96))

## [4.1.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v4.0.0...v4.1.0) (2023-02-09)


### Features

* add an encoder bean in Trace auto-configuration ([#1568](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1568)) ([deb4239](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/deb4239696360dadfe8ecfcda692adf344938909))
* remove `SecretManagerPropertySourceLocator` ([#1571](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1571)) ([c80d299](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c80d2998ca71ee65118ae51853d093817fc1bf3d))


### Bug Fixes

* create Secret Manager beans even without `spring.config.import=sm://` property ([#1567](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1567)) ([b444fcb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b444fcbbdd57984ac7304263490546219ec41c86))
* update starter modules in spring-cloud-preview ([033e2ee](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/033e2ee4135f0dc9db2edf0bb046296f8d9f34bd))


### Dependencies

* bump libraries-bom from 26.5.0 to 26.7.0 ([033e2ee](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/033e2ee4135f0dc9db2edf0bb046296f8d9f34bd))
* bump maven-enforcer-plugin from 3.1.0 to 3.2.1 ([#1557](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1557)) ([877d271](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/877d2717bf2dbb66ff04660844cd63b248a3c619))

## [4.0.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.2...v4.0.0) (2023-01-30)


### ⚠ BREAKING CHANGES

* This release officially introduces Spring Boot 3.x compatibility. Note that breaking changes occur in this release. For full list of changes, refer to [this](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1287).

### Features

* Additional starters that provide dependencies and auto-configurations for working with corresponding Google Client Libraries are available for preview. ([2a8b103](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2a8b10395058168971a85b9bdc3cea65b32b0b59))
* This release officially introduces Spring Boot 3.x compatibility. Note that breaking changes occur in this release. For full list of changes, refer to [this](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1287). ([0caa9b6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0caa9b6245cb4281b8d8da7ccb9f8fa993b0b9c4))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.8.2 to 1.9.0 ([39f9ba3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/39f9ba3906b25a1d18aa678aa540c0cde78a7b83))
* bump libraries-bom from 26.4.0 to 26.5.0 ([#1532](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1532)) ([d2a7973](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d2a7973b233efd5b64e9f1b79cdf47f11db4acef))
* bump micrometer-tracing-bom from 1.0.0 to 1.0.1 ([875768d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/875768d186d1e8cca75516c011e16b130c48279f))
* bump spring-boot-dependencies from 3.0.0 to 3.0.2 ([1b7ca45](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1b7ca452221268758ac29ff163baab6483c191ed))
* bump spring-boot-starter-parent from 3.0.0 to 3.0.2 ([c9165f8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c9165f82e9747f05387dfa4e000e4214fa12945b))
* bump spring-cloud-config.version from 4.0.0 to 4.0.1 ([#1526](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1526)) ([64fd758](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/64fd758ac41f6ea3cd184e0da93f64d2ce7b8ed5))
* bump spring-cloud-dependencies from 2022.0.0 to 2022.0.1 ([#1527](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1527)) ([668d0a7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/668d0a72e2cec9f0cd8652872c6e00691067097e))

## [3.4.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.1...v3.4.2) (2023-01-18)


### Documentation

* Fixes docs related to keeping a background user thread - pub/sub ([2802b8e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2802b8e4dd22dadb9643efe73bca3615435ac362))

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
