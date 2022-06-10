## Google Cloud Key Management Service

The [Google Cloud Key Management Service
(KMS)](https://cloud.google.com/kms/docs) allows you to create, import,
and manage cryptographic keys and perform cryptographic operations in a
single centralized cloud service.

Spring Cloud GCP offers a utility template class `KmsTemplate` which
allows you to conveniently encrypt and decrypt binary or text data.

### Dependency Setup

To begin using this library, add the `spring-cloud-gcp-starter-kms`
artifact to your project.

Maven coordinates, using [Spring Cloud GCP
BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>spring-cloud-gcp-starter-kms</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
      implementation("com.google.cloud:spring-cloud-gcp-starter-kms")
    }

### Configuration

The following options may be configured with Spring Cloud GCP KMS
libraries.

|                                             |                                                                                                                                                                |          |                                                                                                                                                                                                                |
| ------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                                        | Description                                                                                                                                                    | Required | Default value                                                                                                                                                                                                  |
| `spring.cloud.gcp.kms.enabled`              | Enables or disables Google Cloud KMS autoconfiguration                                                                                                         | No       | `true`                                                                                                                                                                                                         |
| `spring.cloud.gcp.kms.project-id`           | GCP project ID of the project using Cloud KMS APIs, if different from the one in the [Spring Cloud GCP Core Module](#spring-cloud-gcp-core).                   | No       | Project ID is typically inferred from [`gcloud`](https://cloud.google.com/sdk/gcloud/reference/config/set) configuration.                                                                                      |
| `spring.cloud.gcp.kms.credentials.location` | Credentials file location for authenticating with the Cloud KMS APIs, if different from the ones in the [Spring Cloud GCP Core Module](#spring-cloud-gcp-core) | No       | Inferred from [Application Default Credentials](https://cloud.google.com/docs/authentication/production), typically set by [`gcloud`](https://cloud.google.com/sdk/gcloud/reference/auth/application-default). |

### Basic Usage

Once you have added the `spring-cloud-gcp-starter-kms` to your project,
the autoconfiguration class
`com.google.cloud.spring.autoconfigure.kms.GcpKmsAutoConfiguration` will
be activated for your project.

The `com.google.cloud.spring.kms.KmsTemplate` bean provided by the
autoconfiguration is the entrypoint to using Spring Cloud GCP support
for Google KMS. This class allows you to specify a Cloud KMS key in your
project via a URI string (format described below) and perform
encryption/decryption with it.

The template class automatically validates the CRC32 checksums received
responses from Cloud KMS APIs to verify correctness of the response.

#### Cloud KMS Key ID format

Spring Cloud GCP offers the following key syntax to specify Cloud KMS
keys in your project:

``` 
 1. Shortest form - specify the key by key ring ID, and key ID.
 The project is inferred from the spring.cloud.gcp.kms.project-id if set, otherwise
 the default GCP project (such as using application-default-credentials) is used.
 The location is assumed to be `global`.

 {key-ring-id}/{key-id}

 2. Short form - specify the key by location ID, key ring ID, and key ID.
 The project is inferred from the spring.cloud.gcp.kms.project-id if set, otherwise
 the default GCP project (such as using application-default-credentials) is used.

 {location-id}/{key-ring-id}/{key-id}

 3. Full form - specify project ID, location ID, key ring ID, and key ID

 {project-id}/{location-id}/{key-ring-id}/{key-id}

 4. Long form - specify project ID, location ID, key ring ID, and key ID.
 This format is equivalent to the fully-qualified resource name of a Cloud KMS key.

 projects/{project-id}/locations/{location-id}/keyRings/{key-ring-id}/cryptoKeys/{key-id}
```

### Sample

A [Cloud KMS Sample
Application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-kms-sample)
is provided which demonstrates basic encryption and decryption
operations.
