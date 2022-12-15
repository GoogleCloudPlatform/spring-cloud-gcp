# How to Contribute

We'd love to accept your patches and contributions to this project. There are just a few small guidelines you need to
follow.

## Contributor License Agreement

Contributions to this project must be accompanied by a Contributor License Agreement. You (or your employer) retain the
copyright to your contribution; this simply gives us permission to use and redistribute your contributions as part of
the project. Head over to <https://cla.developers.google.com/> to see your current agreements on file or to sign a new
one.

You generally only need to submit a CLA once, so if you've already submitted one
(even if it was for a different project), you probably don't need to do it again.

## Building the Project

The first step to contributing is to fork the repository and clone it onto your machine.

1. Ensure you have Java version 8 or later installed on your machine.

2. Try running some tests.
    - Run `./mvnw clean test` in the root directory of the project to run the tests.The `./mvnw` is a
      self-contained [Maven](https://maven.apache.org/) wrapper that allows you to build the project without having
      Maven installed on your local machine.
    - You can run the tests of a specific module by using the `-f` flag like
      this: `./mvnw clean test -f spring-cloud-gcp-pubsub`

3. (Optional) Install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/). The Google Cloud SDK a set of tools
   that you can use to manage resources and applications hosted on Google Cloud.

    - For our purposes, it contains the `gcloud` command line tool which allows you to specify a GCP account and project
      with which you can run our integration tests and sample applications on your machine.

        1. Run `gcloud auth application-default login` to log into
           your [Google Cloud account](https://console.cloud.google.com).

        2. Run `gcloud config set project [YOUR_PROJECT_ID]` to set the GCP project ID you wish to use.

        3. Verify your settings using `gcloud config list`. This will display the account settings that will be used to
           authenticate with Google Cloud on your machine.

NOTE: These methods are recommended for local development only and not for production use. It is recommended to
use [Service Accounts](https://cloud.google.com/iam/docs/service-accounts#whats_next) for authentication in production
applications.

## Code reviews

All submissions, including submissions by project members, require review. We use GitHub pull requests for this purpose.
Consult
[GitHub Help](https://help.github.com/articles/about-pull-requests/) for more information on using pull requests.

### Typical Contribution Cycle

1. Identify an existing [issue](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues) to associate with your
   proposed change, or file a new issue.
2. Fork the repository, develop and test your code changes.
3. Commit your changes with meaningful
   messages ([commit message best practices](https://chris.beams.io/posts/git-commit/))
4. Ensure that your code adheres to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
5. Ensure your code has an appropriate set of unit tests which all pass.
6. If you haven’t already done so, sign a [Contributor License Agreement](https://cla.developers.google.com/).
7. Create a [pull request](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pulls) with the proposed code change,
   to be reviewed by a member of the team.

## Code Style

When submitting code, please follow
[Java Google code style](https://google.github.io/styleguide/javaguide.html) guidelines. If you are developing with
Eclipse or Intellij, the easiest way is to import the Java style configurations
found [here](https://github.com/google/styleguide).

You may find the [google-java-format](https://github.com/google/google-java-format) tool helpful for formatting your
code. It is also available as an  [IntelliJ plugin](https://plugins.jetbrains.com/plugin/8527-google-java-format)
or [Eclipse plugin](https://github.com/google/google-java-format#eclipse).

## Community Guidelines

This project follows [Google's Open Source Community Guidelines](https://opensource.google.com/conduct/).
