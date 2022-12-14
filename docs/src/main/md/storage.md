## Cloud Storage

[Google Cloud Storage](https://cloud.google.com/storage/docs) allows
storing any types of files in single or multiple regions. A Spring Boot
starter is provided to auto-configure the various Storage components.

Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-storage</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-storage")
    }

This starter is also available from [Spring
Initializr](https://start.spring.io/) through the `GCP Storage` entry.

### Using Cloud Storage

The starter automatically configures and registers a `Storage` bean in
the Spring application context. The `Storage` bean
([Javadoc](https://googleapis.dev/java/google-cloud-storage/latest/com/google/cloud/storage/Storage.html))
can be used to list/create/update/delete buckets (a group of objects
with similar permissions and resiliency requirements) and objects.

``` java
@Autowired
private Storage storage;

public void createFile() {
    Bucket bucket = storage.create(BucketInfo.of("my-app-storage-bucket"));

    storage.create(
        BlobInfo.newBuilder("my-app-storage-bucket", "subdirectory/my-file").build(),
            "file contents".getBytes()
    );
}
```

### Cloud Storage Objects As Spring Resources

[Spring
Resources](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html)
are an abstraction for a number of low-level resources, such as file
system files, classpath files, servlet context-relative files, etc.
Spring Framework on Google Cloud adds a new resource type: a Google Cloud Storage (GCS)
object.

The Spring Resource Abstraction for Google Cloud Storage allows GCS
objects to be accessed by their GCS URL using the `@Value` annotation:

``` java
@Value("gs://[YOUR_GCS_BUCKET]/[GCS_FILE_NAME]")
private Resource gcsResource;
```

…​or the Spring application context

``` java
SpringApplication.run(...).getResource("gs://[YOUR_GCS_BUCKET]/[GCS_FILE_NAME]");
```

This creates a `Resource` object that can be used to read the object,
among [other possible
operations](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html#resources-resource).

It is also possible to write to a `Resource`, although a
`WriteableResource` is required.

``` java
@Value("gs://[YOUR_GCS_BUCKET]/[GCS_FILE_NAME]")
private Resource gcsResource;
...
try (OutputStream os = ((WritableResource) gcsResource).getOutputStream()) {
  os.write("foo".getBytes());
}
```

To work with the `Resource` as a Google Cloud Storage resource, cast it
to `GoogleStorageResource`.

If the resource path refers to an object on Google Cloud Storage (as
opposed to a bucket), then the `getBlob` method can be called to obtain
a
[`Blob`](https://github.com/GoogleCloudPlatform/google-cloud-java/blob/main/google-cloud-storage/src/main/java/com/google/cloud/storage/Blob.java).
This type represents a GCS file, which has associated
[metadata](https://cloud.google.com/storage/docs/gsutil/addlhelp/WorkingWithObjectMetadata),
such as content-type, that can be set. The `createSignedUrl` method can
also be used to obtain [signed
URLs](https://cloud.google.com/storage/docs/access-control/signed-urls)
for GCS objects. However, creating signed URLs requires that the
resource was created using service account credentials.

<div class="caution">

As of v2.0.2+, the `GoogleStorageResource.getURL()` method returns the
`Bucket` or `Blob` 's `selfLink` value, rather than attempting to
convert the `URI` a `URL` object that nearly-always threw a
`MalformedURLException`. This value is notably different from
`GoogleStorageResource.getURI()`, which returns the more commonly used
`gs://my-bucket/my-object` identifier. Returning a valid URL is
necessary to support some features in the Spring ecosystem, such as
`spring.resources.static-locations`.

</div>

The Spring Boot Starter for Google Cloud Storage auto-configures the
`Storage` bean required by the `spring-cloud-gcp-storage` module, based
on the `CredentialsProvider` provided by the Spring Framework on Google Cloud Starter.

#### Setting the Content Type

You can set the content-type of Google Cloud Storage files from their
corresponding `Resource` objects:

``` java
((GoogleStorageResource)gcsResource).getBlob().toBuilder().setContentType("text/html").build().update();
```

### Configuration

The Spring Boot Starter for Google Cloud Storage provides the following
configuration options:

|                                                    |                                                                                                                                                                                                      |          |                                                         |
| -------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ------------------------------------------------------- |
| Name                                               | Description                                                                                                                                                                                          | Required | Default value                                           |
| `spring.cloud.gcp.storage.enabled`                 | Enables the GCP storage APIs.                                                                                                                                                                        | No       | `true`                                                  |
| `spring.cloud.gcp.storage.auto-create-files`       | Creates files and buckets on Google Cloud Storage when writes are made to non-existent files                                                                                                         | No       | `true`                                                  |
| `spring.cloud.gcp.storage.credentials.location`    | OAuth2 credentials for authenticating with the Google Cloud Storage API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                                    | No       |                                                         |
| `spring.cloud.gcp.storage.credentials.encoded-key` | Base64-encoded contents of OAuth2 account private key for authenticating with the Google Cloud Storage API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core) | No       |                                                         |
| `spring.cloud.gcp.storage.credentials.scopes`      | [OAuth2 scope](https://developers.google.com/identity/protocols/googlescopes) for Spring Framework on Google Cloud Storage credentials                                                                               | No       | <https://www.googleapis.com/auth/devstorage.read_write> |

### Sample

A [sample
application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-storage-resource-sample)
and a
[codelab](https://codelabs.developers.google.com/codelabs/spring-cloud-gcp-gcs/index.html)
are available.
