## Cloud Runtime Configuration API

<div class="warning">

The Google Cloud Runtime Configuration service is in **Beta** status,
and is only available in snapshot and milestone versions of the project.
It’s also not available in the Spring Framework on Google Cloud BOM, unlike other
modules.

</div>

Spring Framework on Google Cloud makes it possible to use the [Google Runtime
Configuration
API](https://cloud.google.com/deployment-manager/runtime-configurator/reference/rest/)
as a [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)
server to remotely store your application configuration data.

The Spring Framework on Google Cloud Config support is provided via its own Spring Boot
starter. It enables the use of the Google Runtime Configuration API as a
source for Spring Boot configuration properties.

Maven coordinates:

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-config</artifactId>
    <version>{project-version}</version>
</dependency>
```

Gradle coordinates:

    dependencies {
        compile group: 'org.springframework.cloud',
        name: 'spring-cloud-gcp-starter-config',
        version: '{project-version}'
    }

### Configuration

The following parameters are configurable in Spring Framework on Google Cloud Config:

|                                                   |                                                                                                                       |          |                                                                                                   |
| ------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------------- |
| Name                                              | Description                                                                                                           | Required | Default value                                                                                     |
| `spring.cloud.gcp.config.enabled`                 | Enables the Config client                                                                                             | No       | `false`                                                                                           |
| `spring.cloud.gcp.config.name`                    | Name of your application                                                                                              | No       | Value of the `spring.application.name` property. If none, `application`                           |
| `spring.cloud.gcp.config.profile`                 | Active profile                                                                                                        | No       | Value of the `spring.profiles.active` property. If more than a single profile, last one is chosen |
| `spring.cloud.gcp.config.timeout-millis`          | Timeout in milliseconds for connecting to the Google Runtime Configuration API                                        | No       | `60000`                                                                                           |
| `spring.cloud.gcp.config.project-id`              | Google Cloud project ID where the Google Runtime Configuration API is hosted                                                   | No       |                                                                                                   |
| `spring.cloud.gcp.config.credentials.location`    | OAuth2 credentials for authenticating with the Google Runtime Configuration API                                       | No       |                                                                                                   |
| `spring.cloud.gcp.config.credentials.encoded-key` | Base64-encoded OAuth2 credentials for authenticating with the Google Runtime Configuration API                        | No       |                                                                                                   |
| `spring.cloud.gcp.config.credentials.scopes`      | [OAuth2 scope](https://developers.google.com/identity/protocols/googlescopes) for Spring Framework on Google Cloud Config credentials | No       | <https://www.googleapis.com/auth/cloudruntimeconfig>                                              |

<div class="note">

These properties should be specified in a
[`bootstrap.yml`/`bootstrap.properties`](https://cloud.spring.io/spring-cloud-static/spring-cloud.html#_the_bootstrap_application_context)
file, rather than the usual `applications.yml`/`application.properties`.

</div>

<div class="note">

Core properties, as described in [Spring Framework on Google Cloud Core
Module](#spring-framework-on-google-cloud-core), do not apply to Spring Framework on Google Cloud
Config.

</div>

### Quick start

1.  Create a configuration in the Google Runtime Configuration API that
    is called `${spring.application.name}_${spring.profiles.active}`. In
    other words, if `spring.application.name` is `myapp` and
    `spring.profiles.active` is `prod`, the configuration should be
    called `myapp_prod`.
    
    In order to do that, you should have the [Google Cloud
    SDK](https://cloud.google.com/sdk/) installed, own a Google Cloud
    Project and run the following command:
    
        gcloud init # if this is your first Google Cloud SDK run.
        gcloud beta runtime-config configs create myapp_prod
        gcloud beta runtime-config configs variables set myapp.queue-size 25 --config-name myapp_prod

2.  Configure your `bootstrap.properties` file with your application’s
    configuration data:
    
        spring.application.name=myapp
        spring.profiles.active=prod

3.  Add the `@ConfigurationProperties` annotation to a Spring-managed
    bean:
    
        @Component
        @ConfigurationProperties("myapp")
        public class SampleConfig {
        
          private int queueSize;
        
          public int getQueueSize() {
            return this.queueSize;
          }
        
          public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
          }
        }

When your Spring application starts, the `queueSize` field value will be
set to 25 for the above `SampleConfig` bean.

### Refreshing the configuration at runtime

[Spring
Cloud](https://cloud.spring.io/spring-cloud-static/docs/1.0.x/spring-cloud.html#_endpoints)
provides support to have configuration parameters be reloadable with the
POST request to `/actuator/refresh` endpoint.

1.  Add the Spring Boot Actuator dependency:
    
    Maven coordinates:
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    
    Gradle coordinates:
    
        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-actuator")
        }

2.  Add `@RefreshScope` to your Spring configuration class to have
    parameters be reloadable at runtime.

3.  Add `management.endpoints.web.exposure.include=refresh` to your
    `application.properties` to allow unrestricted access to
    `/actuator/refresh`.

4.  Update a property with `gcloud`:
    
        $ gcloud beta runtime-config configs variables set \
          myapp.queue_size 200 \
          --config-name myapp_prod

5.  Send a POST request to the refresh endpoint:
    
        $ curl -XPOST https://myapp.host.com/actuator/refresh

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-config-sample)
and a
[codelab](https://codelabs.developers.google.com/codelabs/cloud-spring-runtime-config/index.html)
are available.
