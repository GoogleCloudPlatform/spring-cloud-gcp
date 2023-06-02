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
