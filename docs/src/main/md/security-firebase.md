## Firebase Authentication

[Firebase Authentication](https://firebase.google.com/products/auth)
provides backend services, easy-to-use SDKs, and ready-made UI libraries
to authenticate users to your [Firebase](https://firebase.google.com/)
app.

The Security Firebase starter uses [Spring Security OAuth 2.0 Resource
Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
functionality to extract user identity from OAuth2 Authorization header.

The Firebase JWT tokens are validated with rules presented
[here](https://firebase.google.com/docs/auth/admin/verify-id-tokens#verify_id_tokens_using_a_third-party_jwt_library).
The following claims are validated automatically:

  - Expiration time: Must be in the future

  - Issued-at time : Must be in the past

  - Audience : Must be the firebase project id

  - Issuer: Must be "https://securetoken.google.com/`<projectId>` "

  - Authentication time : Must be in the past

  - Subject : Must not be empty

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
    <artifactId>spring-cloud-gcp-starter-security-firebase</artifactId>
</dependency>
```

Starter Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-security-firebase")
    }

### Configuration

The following properties are available.

<div class="caution">

Modifying public-keys-endpoint property might be useful for testing, but
the defaults should not be changed in production.

</div>

| Name                                                      | Description                                                       | Required | Default                                                                                    |
| --------------------------------------------------------- | ----------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------ |
| `spring.cloud.gcp.security.firebase.project-id`           | Overrides the Google Cloud project ID specified in the Core module.        | false    |                                                                                            |
| `spring.cloud.gcp.security.firebase.public-keys-endpoint` | Link to Google’s public endpoint containing Firebase public keys. | true     | `https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com` |

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-security-firebase-sample)
is available. This sample app provides simple login page using
[firebase-ui](https://github.com/firebase/firebaseui-web) to fetch the
JWT token.
