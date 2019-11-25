# How to Release to Maven Central

## Snapshots

A commit to the `master` branch will automatically trigger the `prod:cloud-java-frameworks/cloud-spanner-r2dbc/continuous` job that will publish snapshots to Sonatype Snapshots repository. The scripts can be found in the `.kokoro` directory.

## Releases

. Run the `prod:cloud-java-frameworks/cloud-spanner-r2dbc/stage` Kokoro job.

. In the build logs, find the ID of the staging repository. You should see a log line that looks something like this:
```
[INFO]  * Created staging repository with ID "comgooglecloud-1416".
```
The ID in this case is `comgooglecloud-1416`.

. Verify staged artifacts at http://oss.sonatype.org.
(If you don't have access to `com.google.cloud`, please make a request similar to https://issues.sonatype.org/browse/OSSRH-52371[this one] and ask for support from someone who already has access to this group ID.)

. If you want to drop the staged artifacts, run the `prod:cloud-java-frameworks/cloud-spanner-r2dbc/drop` Kokoro job, while providing the staging repository ID as an environment variable like `STAGING_REPOSITORY_ID=comgooglecloud-1416`.

. If you want to release the staged artifacts, run the `prod:cloud-java-frameworks/cloud-spanner-r2dbc/promote` Kokoro job, while providing the staging repository ID as an environment variable like `STAGING_REPOSITORY_ID=comgooglecloud-1416`.

. Verify that the new version has been published to Maven Central by checking https://repo.maven.apache.org/maven2/com/google/cloud/cloud-spanner-r2dbc/[here]. This might take a while.

. https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/releases[Create] a new release on GitHub.

. Increment the project base version. For example, from `1.0.0-SNAPSHOT` to `1.1.0-SNAPSHOT`.
