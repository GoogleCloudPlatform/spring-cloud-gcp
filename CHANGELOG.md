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

## [3.7.8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.7...v3.7.8) (2024-02-13)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.31.0 to 26.32.0 ([#2590](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2590)) ([7069a47](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7069a47afbbe74d5dee84b8ca5b82dd6f768e75f))

## [3.7.7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.6...v3.7.7) (2024-02-05)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.30.0 to 26.31.0 ([#2566](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2566)) ([f224e72](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f224e722df70922784912d364fe82ed557b8fa5e))
* bump com.google.cloud.tools:appengine-maven-plugin ([#2525](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2525)) ([728a21a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/728a21abf9c703d8d1dc9ce7a89cb1b69bebae40))
* bump com.google.cloud.tools:appengine-maven-plugin ([#2557](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2557)) ([c24a550](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c24a5509084de0d0f1116b71339baec03698251d))
* bump com.google.errorprone:error_prone_core from 2.23.0 to 2.24.1 ([#2504](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2504)) ([a940de9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a940de9268ea9e078852ffecd03c5921d8e22961))
* bump io.asyncer:r2dbc-mysql from 0.9.6 to 0.9.7 ([#2511](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2511)) ([7afbaf5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7afbaf5ea24564722391b0740d4a31e73e108f29))
* bump org.apache.maven.plugins:maven-compiler-plugin ([#2497](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2497)) ([611d5b6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/611d5b6f0a5a3b44c713e4edd15a3cbdca09b8ca))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2400](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2400)) ([692a50a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/692a50a7ad72c50196a51b8d497b94df0256ef15))
* bump org.codehaus.mojo:flatten-maven-plugin from 1.5.0 to 1.6.0 ([#2522](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2522)) ([5f3f0ba](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5f3f0bacdc8b7f69eec63c17cabf39c0890b8fa2))
* bump org.springframework.cloud:spring-cloud-dependencies ([#2485](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2485)) ([517cca8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/517cca8b17ff1732ec438ff0b5d38e5145d9fc94))
* bump org.testcontainers:testcontainers-bom from 1.19.3 to 1.19.4 ([#2553](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2553)) ([ba0553e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ba0553e5f94b207875fe38d3e5e61f28a70669a3))
* bump spring-cloud-config.version from 3.1.7 to 3.1.8 ([#2483](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2483)) ([98e09b1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/98e09b1a3ffeb0fa469847a9f6892b98fdc337e7))
* bump zipkin-gcp.version from 1.0.4 to 1.1.1 ([#2506](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2506)) ([726d74f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/726d74ff76427e5801b7394289ef7a0e9b690003))

## [3.7.6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.5...v3.7.6) (2024-01-17)


### Bug Fixes

* **3.x:** delegate to all parent logging options ([4653003](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/465300393fe78449ae55c441e326a7920db7bc60))
* **3.x:** GcpFirestoreEmulatorAutoConfiguration constructs invalid document parent ([#2429](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2429)) ([30f7b7a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/30f7b7a776bd75415a6ff214f110ad087757073d))


### Dependencies

* bump com.google.cloud:libraries-bom from 26.28.0 to 26.30.0 ([#2514](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2514)) ([664d878](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/664d8780e971eba9367c2d08360f5d02c66540cb))

## [3.7.5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.4...v3.7.5) (2023-12-08)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.27.0 to 26.28.0 ([#2412](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2412)) ([676befb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/676befb908ffc52c7d75a75bceed1c80faf49984))
* bump commons-io:commons-io from 2.15.0 to 2.15.1 ([#2392](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2392)) ([1f43db8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1f43db8545c7edb92ba3a2b98f51fb7cbc2b226c))
* bump org.codehaus.mojo:build-helper-maven-plugin ([#2382](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2382)) ([5bcdf2b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5bcdf2bbc0dbcd2b0ba0424d8c4cf61b597ab78e))
* bump org.springframework.boot:spring-boot-dependencies ([#2376](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2376)) ([5ff72d3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5ff72d3305f5c578f0ce1cde10b3cd4d2bde2dba))
* bump org.springframework.boot:spring-boot-starter-parent ([#2375](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2375)) ([a048059](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a048059f5cb09dd969b7dd380ebae1df14e4a7e4))
* bump org.springframework.shell:spring-shell-starter ([#2388](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2388)) ([0f57631](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0f57631d3a0e41e6756b3095cf904d1fd9b55970))
* bump org.testcontainers:testcontainers-bom from 1.19.1 to 1.19.3 ([#2369](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2369)) ([c5b03aa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c5b03aaa481df91d72fbfb1b1ddda228a7b8d41f))

## [3.7.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.3...v3.7.4) (2023-11-09)


### Bug Fixes

* **3.x:** Ensure topic exists in consumer only if autoCreateResources is true ([#2294](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2294)) ([892666e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/892666ef106c58b3c11e37cbb08dc8ebf6394314))


### Dependencies

* bump com.google.cloud:cloud-spanner-r2dbc from 1.2.2 to 1.3.0 ([#2315](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2315)) ([4be1da6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4be1da6ab8ca5b1c8d595b13e7324354380c920b))
* bump com.google.cloud:libraries-bom from 26.26.0 to 26.27.0 ([#2334](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2334)) ([0d8b746](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0d8b74694334159075d8980350110b7c72a09afa))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2328](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2328)) ([8b08dd9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8b08dd9f18ec89a990518a357c0ee1640d743ce1))

## [3.7.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.2...v3.7.3) (2023-10-27)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.14.0 to 1.14.1 ([#2242](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2242)) ([90a9b35](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/90a9b35ae631972e28e94e99223fc72c34406488))
* bump com.google.cloud:libraries-bom from 26.25.0 to 26.26.0 ([#2298](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2298)) ([b9d95cf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b9d95cf035f6d0823629818f06bd02ef72408a25))
* bump com.google.cloud.tools:appengine-maven-plugin ([#2224](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2224)) ([63a9032](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/63a903298b5c9a6910b568199c062f7875cc40a1))
* bump com.google.errorprone:error_prone_core from 2.22.0 to 2.23.0 ([#2269](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2269)) ([15afe6e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/15afe6e3e573343d798d4501a7afcf4efbeef17f))
* bump commons-io:commons-io from 2.14.0 to 2.15.0 ([#2293](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2293)) ([ec14f61](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ec14f6186dfcbedc12e436bbbdce8e92ee9fd381))
* bump io.asyncer:r2dbc-mysql from 0.9.4 to 0.9.6 ([#2248](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2248)) ([a420d9a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a420d9a91c6f07fc79f2d538df8a98ac5baf21dc))
* bump org.apache.maven.plugins:maven-checkstyle-plugin ([#2289](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2289)) ([7b7c7c7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7b7c7c77caa030bfc7e645f6f09fb285b35a22f2))
* bump org.jacoco:jacoco-maven-plugin from 0.8.10 to 0.8.11 ([#2256](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2256)) ([c0d95aa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c0d95aa1c2664b1a78c59c3fc175886f987e2713))
* bump org.springframework.boot:spring-boot-dependencies ([#2276](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2276)) ([0b9856c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0b9856cd4a08e9043baa26af10c2eda6f078fa77))
* bump org.springframework.boot:spring-boot-starter-parent ([#2277](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2277)) ([d9a8b51](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d9a8b51702a2a8974e5bcff41be6dbb6bd00fe19))
* bump org.springframework.shell:spring-shell-starter ([#2225](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2225)) ([c45c296](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c45c296c6560f486e5e11b1444e30bddb0884977))

## [3.7.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.1...v3.7.2) (2023-10-16)


### Bug Fixes

* allow minimal permissions for consumer destination use ([#2236](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2236)) ([df82bd0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/df82bd08f19528d1569aaec61c4bbec0b5afbe9f))
* remove unnecessary topic name comparison on subscription ([#2239](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2239)) ([#2247](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2247)) ([5d5dbc7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5d5dbc74776df17190ffa7e04a638869af1fafc1))


### Dependencies

* bump com.google.cloud:libraries-bom from 26.24.0 to 26.25.0 ([#2251](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2251)) ([f812439](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f812439224369068ea2e39b451477a8c7061a9ec))

## [3.7.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.7.0...v3.7.1) (2023-10-03)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.23.0 to 26.24.0 ([#2219](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2219)) ([4f88efb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4f88efbabfde7fa38032d62b4e1c9c986262c9c3))
* bump com.google.errorprone:error_prone_core from 2.21.1 to 2.22.0 ([#2199](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2199)) ([46426e1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/46426e1ba7325ac67e0f8632ab242a1809c29fa6))
* bump commons-io:commons-io from 2.13.0 to 2.14.0 ([#2212](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2212)) ([43ebec1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/43ebec168b9a0a6fa8c80134494793f89cdb7bda))
* bump org.springframework.boot:spring-boot-dependencies ([#2198](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2198)) ([0759a32](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0759a32a74192d2084c365b3505e09ee7bd6f135))
* bump org.springframework.boot:spring-boot-starter-parent ([#2200](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2200)) ([1b6e07d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1b6e07d0bb048f13b6f1d4ff8e916eeaf2977aac))
* bump org.testcontainers:testcontainers-bom from 1.19.0 to 1.19.1 ([#2218](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2218)) ([fbcd1fa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fbcd1fa4d16656f55027d175f751fd5af3e060c3))

## [3.7.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.6.3...v3.7.0) (2023-09-20)


### Features

* **3.x:** add support for Firestore database id configuration ([#2184](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2184)) ([0c4ff8c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0c4ff8c4a2d093f45e0c200cfbac4e18f7d7067a))


### Bug Fixes

* **3.x:** Ensure proper merging of Binder default props [#2179](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2179)) ([27150e7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/27150e76db681902c25390a7ff0b2c1d6be61ccd))
* Firestore updateTime extraction after commit ([#2168](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2168)) ([46ab69d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/46ab69d2fc3d6f4a73c52053ef47e74ccb3d1493))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.13.1 to 1.14.0 ([#2162](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2162)) ([354ccf3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/354ccf3c98446825890db4756ee94a3e585940a1))
* bump com.google.cloud:libraries-bom from 26.22.0 to 26.23.0 ([#2167](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2167)) ([2d809bb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2d809bb518db33b52dad34a04d9e00644ca38aee))
* bump io.asyncer:r2dbc-mysql from 0.9.3 to 0.9.4 ([#2180](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2180)) ([200d4a0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/200d4a03f6f84ce3f250cb83116d7a4dc0679bea))
* bump java-cfenv.version from 2.4.2 to 2.5.0 ([#2130](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2130)) ([23e2f44](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/23e2f447588c7fb84aa7715ff5d47e6c5e6bd98d))
* bump org.apache.maven.plugins:maven-enforcer-plugin ([#2156](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2156)) ([7406ca5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7406ca507653055b58c4b64167279eac7c49d9a0))
* bump org.apache.maven.plugins:maven-javadoc-plugin ([#2172](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2172)) ([d677538](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/d67753804701937dfa175fccce49b47f6565f70a))
* bump org.projectlombok:lombok from 1.18.28 to 1.18.30 ([#2187](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2187)) ([7708bec](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7708bec85510ed672139f8c4e0e8c743b7a34259))
* bump org.springframework.boot:spring-boot-dependencies ([#2141](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2141)) ([b7d8aba](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b7d8abaed03dba41e52ace8fdd03bf1040f4230e))
* bump org.springframework.boot:spring-boot-starter-parent ([#2140](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2140)) ([7160770](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7160770a9c9fc317a7b13ab59da44a5d28b2d2af))
* bump org.testcontainers:testcontainers-bom from 1.18.3 to 1.19.0 ([#2132](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2132)) ([43d41db](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/43d41db58ff59b1094fd1f7f5532057e3bb6abd9))

## [3.6.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.6.2...v3.6.3) (2023-08-10)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.21.0 to 26.22.0 ([#2111](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2111)) ([8729b92](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8729b92bf28832c39e7a2b732198c4fa9424fd21))
* bump com.google.errorprone:error_prone_core from 2.20.0 to 2.21.1 ([#2105](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2105)) ([578a3d6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/578a3d604df7e333c201d6791357950bb3f1013a))

## [3.6.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.6.1...v3.6.2) (2023-08-01)


### Dependencies

* bump com.google.cloud:libraries-bom from 26.20.0 to 26.21.0 ([#2089](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2089)) ([2de34e5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2de34e5dd62ba69c528d6580d4b86f52abfefb23))

## [3.6.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.6.0...v3.6.1) (2023-07-27)


### Dependencies

* **3.x:** stop forcing Guava version ([#2070](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2070)) ([bf22ebe](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bf22ebed949131f24ff59c08a6fb8d7b686c855a))
* bump com.google.cloud:libraries-bom from 26.19.0 to 26.20.0 ([#2073](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2073)) ([e8d2c90](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e8d2c90899668d2e371e05452dd5b9f2eee7dc95))
* bump org.springframework.shell:spring-shell-starter ([#2063](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2063)) ([caedd75](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/caedd75b4de0bdd82a0b2c5af0f50433016b6356))

## [3.6.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.5...v3.6.0) (2023-07-26)


### Features

* migrate Cloud Spanner Spring Data R2DBC ([#1971](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1971)) ([f9303f0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f9303f0e6d977594cbf3f740ee7c9ed9afd78c6a))


### Dependencies

* bump org.springframework.boot:spring-boot-dependencies ([#2039](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2039)) ([e2a6549](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e2a6549aaf4034fe5ad170ba9d9c4ce2036b8fe6))
* bump org.springframework.boot:spring-boot-starter-parent ([#2038](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2038)) ([b27f0c7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b27f0c7741a19a052427d5794ff56c65d6c26c44))
* bump spring-cloud-config.version from 3.1.6 to 3.1.7 ([#1993](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1993)) ([bbb7b9f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bbb7b9f5bcc9ff638a81556cb7b2701633c0bfd0))
* bump spring-cloud-dependencies from 2021.0.7 to 2021.0.8 ([#1992](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1992)) ([f4a87b5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f4a87b536031b95e733744b1caf1f5d79100ffef))
* Manually bumping `cloud-sql-socket-factory.version` from `1.12.0` to `1.13.1` ([#2052](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2052)) ([e6bd1ec](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e6bd1ec72ba5ce4aa862aebcad7e45cb7d68a987))

## [3.5.5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.4...v3.5.5) (2023-07-19)


### Dependencies

* bump libraries-bom from 26.18.0 to 26.19.0 ([#2034](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/2034)) ([c32ca1f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c32ca1f1f1517540cf7efc4ad1d96dbe70e94ef6))

## [3.5.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.3...v3.5.4) (2023-06-28)


### Dependencies

* bump error_prone_core from 2.19.1 to 2.20.0 ([#1970](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1970)) ([0f3cc54](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0f3cc54b7d1aa87aa9b5bd118a6d47ab031ae330))
* bump libraries-bom from 26.17.0 to 26.18.0 ([#1986](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1986)) ([cec1a15](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/cec1a15171e3c812abb0829beac9372906d68836))
* bump spring-boot-dependencies from 2.7.12 to 2.7.13 ([#1973](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1973)) ([e71ee92](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e71ee92fd5b2a5e04fe7457e374dd7f365c6dc73))
* bump spring-boot-starter-parent from 2.7.12 to 2.7.13 ([#1974](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1974)) ([152fe0b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/152fe0b244f436894ed6f7323ee422a88e31fcdb))
* bump spring-shell-starter from 2.1.10 to 2.1.11 ([#1984](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1984)) ([0ecd59a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0ecd59a652476ce08ee1192bca9238b4d43514e2))

## [3.5.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.2...v3.5.3) (2023-06-15)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.11.1 to 1.12.0 ([#1950](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1950)) ([dcafef2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dcafef2abd2f2287ad196bac79a865bff8dee8d8))
* bump commons-io from 2.11.0 to 2.13.0 ([#1935](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1935)) ([f3114de](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f3114de9b64a518d62966195cf58313b87cb9f51))
* bump libraries-bom from 26.16.0 to 26.17.0 ([#1949](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1949)) ([41cde8c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/41cde8c47313cf855b6ddd8700b6630b9fcf61cb))

## [3.5.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.1...v3.5.2) (2023-06-02)


### Bug Fixes

* pull-endpoint setting for async subscribers (backport of [#1883](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1883)) [#1892](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1892) ([a5dccd7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a5dccd75e289d6146e28204e39dfca0ed57ca9e1))


### Dependencies

* bump libraries-bom from 26.15.0 to 26.16.0 ([#1919](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1919)) ([3003aeb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3003aebe6ab90ce641a5fe42a3a283d6cd131e7e))
* bump lombok from 1.18.26 to 1.18.28 ([#1898](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1898)) ([4889859](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/48898596948b2833a68e6289112decad463a5972))
* bump maven-checkstyle-plugin from 3.2.2 to 3.3.0 ([#1890](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1890)) ([397783c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/397783c2f7f1bb674f7971ee04a2aeadcc802267))
* bump maven-source-plugin from 3.2.1 to 3.3.0 ([#1887](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1887)) ([69dd414](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/69dd414a7231d703ba9fe3f554d66cb556d91c77))
* bump spring-boot-dependencies from 2.7.11 to 2.7.12 ([#1878](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1878)) ([4d1e0e3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4d1e0e3b9f2d469ea59e213fd86aab0b662d3b96))
* bump spring-boot-starter-parent from 2.7.11 to 2.7.12 ([#1879](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1879)) ([9269a94](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9269a9445563ac9e7ee2b7dff5bb37f74c3d9218))
* bump spring-shell-starter from 2.1.9 to 2.1.10 ([#1906](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1906)) ([ed16005](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ed16005a914059982fe5015ba17e8f7854a269b9))
* bump testcontainers-bom from 1.18.1 to 1.18.3 ([#1915](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1915)) ([ca8846f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/ca8846fe9c656b1519213bd85cd231d9a4e0119c))

## [3.5.1](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.5.0...v3.5.1) (2023-05-16)


### Dependencies

* bump build-helper-maven-plugin from 3.3.0 to 3.4.0 ([#1845](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1845)) ([0afe6aa](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/0afe6aa995889bd117396bf680f4254b6473f864))
* bump error_prone_core from 2.18.0 to 2.19.1 ([#1840](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1840)) ([24944df](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/24944df9a8cc29bf281868ade9487969c6a15db8))
* bump flatten-maven-plugin from 1.4.1 to 1.5.0 ([#1834](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1834)) ([08b2fe5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/08b2fe55c8f5faa1f7f1ba283e5d6d4c826b5520))
* bump java-cfenv.version from 2.4.1 to 2.4.2 ([#1805](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1805)) ([c2a9b66](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c2a9b662f903eb2de0b2bee121307c9ef279da05))
* bump libraries-bom from 26.14.0 to 26.15.0 ([#1858](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1858)) ([089fc97](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/089fc97b28f5bd99de3b0793e7ef0d9a91631fa7))
* bump maven-deploy-plugin from 3.0.0 to 3.1.1 ([#1820](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1820)) ([54616a7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/54616a740f35b1e724f96b0ee13564d1fd3b5e2d))
* bump maven-gpg-plugin from 3.0.1 to 3.1.0 ([#1815](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1815)) ([015af4d](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/015af4dfb07b1d55e62cc61cb529f8430a1ac08c))
* bump spring-shell-starter from 2.1.8 to 2.1.9 ([#1821](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1821)) ([1265d85](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1265d8557ae121dcb38a7f2d874946d0417b0674))
* bump testcontainers-bom from 1.18.0 to 1.18.1 ([#1841](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1841)) ([007c036](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/007c036cbf4131b61e031fcbbc8980ebf47f1746))

## [3.5.0](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.9...v3.5.0) (2023-05-05)


### Features

* add support for insert and insertAll in DatastoreOperations ([#1729](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1729)) ([#1766](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1766)) ([420bb0e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/420bb0e6163688697602d69ebcf84735a19c6f78))


### Dependencies

* bump jacoco-maven-plugin from 0.8.8 to 0.8.10 ([#1772](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1772)) ([5e37371](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5e3737180697bc7fd7fa480571b35ab6214d7769))
* bump libraries-bom from 26.13.0 to 26.14.0 ([#1787](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1787)) ([3fb0093](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3fb009349e5800250c105ef2bf131bb9e31f0aeb))
* bump maven-checkstyle-plugin from 3.2.1 to 3.2.2 ([#1760](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1760)) ([7c58d4c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/7c58d4c1511742540abeaaee2477a35e109c471c))
* bump spring-cloud-build from 3.1.6 to 3.1.7 ([#1784](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1784)) ([276100e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/276100e76b3c06c4f058ff55000effe2fd70eee1))
* bump spring-cloud-dependencies from 2021.0.6 to 2021.0.7 ([#1779](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1779)) ([e04a18b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e04a18b658a9f8ce13419616c95f53ad77237b4e))

## [3.4.9](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.8...v3.4.9) (2023-04-20)


### Bug Fixes

* enable-iam-r2dbc in `3.x` ([#1726](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1726)) ([13f64f8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/13f64f82f37c36828e22a34a3d913ac0141ab5ef))


### Dependencies

* bump cloud-sql-socket-factory.version from 1.11.0 to 1.11.1 ([#1724](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1724)) ([82b3bed](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/82b3bed25fe55115796822711a6c97c22530244a))
* bump flatten-maven-plugin from 1.4.0 to 1.4.1 ([#1679](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1679)) ([16c85a5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/16c85a53cb563359fb579a02fd3871e6b574cb5a))
* bump libraries-bom from 26.12.0 to 26.13.0 ([#1757](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1757)) ([295c75a](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/295c75a662690adf3d9edabeaedfb249fa9715d9))
* bump maven-enforcer-plugin from 3.2.1 to 3.3.0 ([#1699](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1699)) ([a4d646c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a4d646c36ee7f692ee928fd062c35b8e7c9eb101))
* bump spring-boot-dependencies from 2.7.10 to 2.7.11 ([#1750](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1750)) ([1244b1c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/1244b1c4ea0fc8f826edf5ee3ef6eb9d00ef7a27))
* bump spring-boot-starter-parent from 2.7.10 to 2.7.11 ([#1748](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1748)) ([4f513bf](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/4f513bfa42a4337c9452268de9231c1c82c9e536))
* bump spring-shell-starter from 2.1.7 to 2.1.8 ([#1725](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1725)) ([c3556a5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/c3556a52850ed2be04b6421deb86359240ad8c78))
* bump testcontainers-bom from 1.17.6 to 1.18.0 ([#1697](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1697)) ([bd906d5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/bd906d5db638226a17d0acf17c6c2ef2f5684b2d))

## [3.4.8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.7...v3.4.8) (2023-04-05)


### Dependencies

* bump libraries-bom from 26.11.0 to 26.12.0 ([#1698](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1698)) ([416a843](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/416a8431fdf6259ca661faead4ef18d8eb14b003))

## [3.4.7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.6...v3.4.7) (2023-03-24)


### Dependencies

* bump flatten-maven-plugin from 1.3.0 to 1.4.0 ([#1663](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1663)) ([b5beca2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/b5beca218a565ff0d689f1ada84eaec10240244b))
* bump libraries-bom from 26.10.0 to 26.11.0 ([#1671](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1671)) ([609fa42](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/609fa42a4d5d294b38a3e83fad7010eaaddf66a3))
* bump spring-boot-dependencies from 2.7.9 to 2.7.10 ([#1670](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1670)) ([96df29e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/96df29e65ec5b97e55be26ec8b949d89666ba236))
* bump spring-boot-starter-parent from 2.7.9 to 2.7.10 ([#1672](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1672)) ([6b95f93](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6b95f933f7850888f37aaa96ddd8f116be000fb1))

## [3.4.6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.5...v3.4.6) (2023-03-08)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.10.0 to 1.11.0 ([#1635](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1635)) ([2d101ad](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2d101ad77e2f57022207e43ef1af70c7299e85c0))
* bump libraries-bom from 26.9.0 to 26.10.0 ([#1648](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1648)) ([dea6d1f](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/dea6d1fb8f0a2d384a9a2d4a934e11a190c4b04a))
* bump maven-compiler-plugin from 3.10.1 to 3.11.0 ([#1633](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1633)) ([94ad677](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/94ad67703b6d05e96e8f5ba4ab87a0eeb3f9627a))
* bump spring-cloud-build from 3.1.5 to 3.1.6 ([#1626](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1626)) ([8dd7c47](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/8dd7c47982d1421287aa28ca1184ba8ee61c4eae))
* bump spring-cloud-config.version from 3.1.5 to 3.1.6 ([#1623](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1623)) ([e52da8b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e52da8b3d4db4d8aef153a15e9c04bcdbfadf4f7))
* bump spring-cloud-dependencies from 2021.0.5 to 2021.0.6 ([#1624](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1624)) ([9411fb8](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/9411fb8fa2fbb4ff9953476ebad4a4df6d070ab1))
* bump spring-shell-starter from 2.1.6 to 2.1.7 ([#1636](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1636)) ([f5133dd](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/f5133dd8d7f27a78c1976c3de0afce79c5f21e5f))

## [3.4.5](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.4...v3.4.5) (2023-02-23)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.9.0 to 1.10.0 ([#1584](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1584)) ([68ce4af](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/68ce4afd1ed2d1bfacdf98a4231a358cca62380e))
* bump libraries-bom from 26.7.0 to 26.9.0 ([#1613](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1613)) ([3e1ca3b](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/3e1ca3bbc3d496c2ae50ec5638887faf988b6d8d))
* bump maven-javadoc-plugin from 3.4.1 to 3.5.0 ([#1597](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1597)) ([95aeac7](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/95aeac7bbb34fd03b8ab97d2c7b074d8779e38f1))
* bump spring-boot-dependencies from 2.7.8 to 2.7.9 ([#1611](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1611)) ([e9e1ba3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/e9e1ba302e0a93e110321e7d172d5d12dd914180))
* bump spring-boot-starter-parent from 2.7.8 to 2.7.9 ([#1615](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1615)) ([6332c92](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/6332c920b427cc2c9989ff3dce96d967d240e0f6))

## [3.4.4](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.3...v3.4.4) (2023-02-09)


### Dependencies

* bump libraries-bom from 26.5.0 to 26.7.0 ([#1581](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1581)) ([a751122](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/a7511223033c588e8ee745150cd9e51558d9edb9))
* bump lombok from 1.18.24 to 1.18.26 ([#1566](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1566)) ([1851717](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/18517173f7242f22c7b942c354813c510d793839))
* bump maven-enforcer-plugin from 3.1.0 to 3.2.1 ([#1558](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1558)) ([432379c](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/432379c142f01adc13b557c260300f9c7d7050d7))

## [3.4.3](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.2...v3.4.3) (2023-01-27)


### Dependencies

* bump cloud-sql-socket-factory.version from 1.8.2 to 1.9.0 ([42cda22](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/42cda22ddf20f8875e1c0f01a6e93bf609a5e0a8))
* bump libraries-bom from 26.4.0 to 26.5.0 ([#1530](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1530)) ([fafd2c6](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/fafd2c60beef72a3d15ff7840ef4533a52a06900))
* bump spring-boot-dependencies from 2.7.7 to 2.7.8 ([63ea9eb](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/63ea9ebb309e0e1d298305b95f2ae537102955f2))
* bump spring-boot-starter-parent from 2.7.7 to 2.7.8 ([5b799ed](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/5b799ed12083d4b187c7d1af504d4d4be1c92269))
* bump spring-shell-starter from 2.1.5 to 2.1.6 ([30786be](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/30786beda811c1a1a7e84c3e73d01af73d16125c))

## [3.4.2](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/compare/v3.4.1...v3.4.2) (2023-01-18)


### Documentation

* Fixes docs related to keeping a background user thread - pub/sub ([2802b8e](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/commit/2802b8e4dd22dadb9643efe73bca3615435ac362))

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
