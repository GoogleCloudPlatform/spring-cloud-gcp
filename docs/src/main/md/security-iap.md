## Cloud IAP

[Cloud Identity-Aware Proxy (IAP)](https://cloud.google.com/iap/)
provides a security layer over applications deployed to Google Cloud.

The IAP starter uses [Spring Security OAuth 2.0 Resource
Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
functionality to automatically extract user identity from the
proxy-injected `x-goog-iap-jwt-assertion` HTTP header.

The following claims are validated automatically:

  - Issue time

  - Expiration time

  - Issuer

  - Audience

The *audience* (`"aud"` claim) validation string is automatically
determined when the application is running on App Engine Standard or App
Engine Flexible. This functionality relies on Cloud Resource Manager API
to retrieve project details, so the following setup is needed:

  - Enable Cloud Resource Manager API in [Google Cloud
    Console](https://console.developers.google.com/apis/api/cloudresourcemanager.googleapis.com).

  - Make sure your application has `resourcemanager.projects.get`
    permission.

App Engine automatic *audience* determination can be overridden by using
`spring.cloud.gcp.security.iap.audience` property. It supports multiple
allowable audiences by providing a comma-delimited list.

For Compute Engine or Kubernetes Engine
`spring.cloud.gcp.security.iap.audience` property **must** be provided,
as the *audience* string depends on the specific Backend Services setup
and cannot be inferred automatically. To determine the *audience* value,
follow directions in IAP [Verify the JWT
payload](https://cloud.google.com/iap/docs/signed-headers-howto#verify_the_jwt_payload)
guide. If `spring.cloud.gcp.security.iap.audience` is not provided, the
application will fail to start the following message:

    No qualifying bean of type 'com.google.cloud.spring.security.iap.AudienceProvider' available.

<div class="note">

If you create a custom
[`WebSecurityConfigurerAdapter`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.html),
enable extracting user identity by adding
`.oauth2ResourceServer().jwt()` configuration to the
[`HttpSecurity`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)
object. If no custom
[`WebSecurityConfigurerAdapter`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.html)
is present, nothing needs to be done because Spring Boot will add this
customization by default.

</div>

Starter Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-security-iap</artifactId>
</dependency>
```

Starter Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-security-iap")
    }

### Configuration

The following properties are available.

<div class="caution">

Modifying registry, algorithm, and header properties might be useful for
testing, but the defaults should not be changed in production.

</div>

| Name                                      | Description                                      | Required                             | Default                                             |
| ----------------------------------------- | ------------------------------------------------ | ------------------------------------ | --------------------------------------------------- |
| `spring.cloud.gcp.security.iap.registry`  | Link to JWK public key registry.                 | true                                 | `https://www.gstatic.com/iap/verify/public_key-jwk` |
| `spring.cloud.gcp.security.iap.algorithm` | Encryption algorithm used to sign the JWK token. | true                                 | `ES256`                                             |
| `spring.cloud.gcp.security.iap.header`    | Header from which to extract the JWK key.        | true                                 | `x-goog-iap-jwt-assertion`                          |
| `spring.cloud.gcp.security.iap.issuer`    | JWK issuer to verify.                            | true                                 | `https://cloud.google.com/iap`                      |
| `spring.cloud.gcp.security.iap.audience`  | Custom JWK audience to verify.                   | false on App Engine; true on GCE/GKE |                                                     |

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-security-iap-sample)
is available.
