## Kotlin Support

The latest version of the Spring Framework provides first-class support
for Kotlin. For Kotlin users of Spring, the Spring Cloud GCP libraries
work out-of-the-box and are fully interoperable with Kotlin
applications.

For more information on building a Spring application in Kotlin, please
consult the [Spring Kotlin
documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin).

### Prerequisites

Ensure that your Kotlin application is properly set up. Based on your
build system, you will need to include the correct Kotlin build plugin
in your project:

  - [Kotlin Maven
    Plugin](https://kotlinlang.org/docs/reference/using-maven.html)

  - [Kotlin Gradle
    Plugin](https://kotlinlang.org/docs/reference/using-gradle.html)

Depending on your application’s needs, you may need to augment your
build configuration with compiler plugins:

  - [Kotlin Spring
    Plugin](https://kotlinlang.org/docs/reference/compiler-plugins.html#spring-support):
    Makes your Spring configuration classes/members non-final for
    convenience.

  - [Kotlin JPA
    Plugin](https://kotlinlang.org/docs/reference/compiler-plugins.html#jpa-support):
    Enables using JPA in Kotlin applications.

Once your Kotlin project is properly configured, the Spring Cloud GCP
libraries will work within your application without any additional
setup.

### Sample

A [Kotlin sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-kotlin-samples/spring-cloud-gcp-kotlin-app-sample)
is provided to demonstrate a working Maven setup and various Spring
Cloud GCP integrations from within Kotlin.
