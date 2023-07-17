# Generator - Spring Boot Starters for Google Client Libraries


## Development Workflow

For local development in the `spring-cloud-generator` submodule,
first build `spring-cloud-gcp` from root:

```
mvn clean install -DskipTests
```

### Unit and Golden Testing for the Generator

To execute unit and golden tests for the generator, run (from the `spring-cloud-generator` directory):
```
# All tests
mvn test

# Single test
mvn test -Dtest=SpringAutoConfigClassComposerTest

# Update golden files
mvn test -DupdateUnitGoldens
```

### Generation of Spring Boot Starters

#### Running Github Actions Workflow:
Corresponding workflow file: [generateAutoConfigs.yaml](/.github/workflows/generateAutoConfigs.yaml)

Using github actions, the following command can run the end-to-end generation workflow in `generateAutoConfigs.yaml`
against a development branch (e.g. `<my-branch>`):

```
gh workflow run generateAutoConfigs.yml --ref <my-branch> \
-f branch_name=<my-branch> -f forked_repo=none
```
* The equivalent of this command can also be triggered through the github UI.
  * `--ref <my-branch>` specifies that the workflow run should be triggered from `<my-branch>` 
  (e.g. to test changes under development in workflow file and script).
  * `-f branch_name=<my-branch>` specifies that `<my-branch>` should be the branch to parse libraries-bom version from, 
  and push generated code changes to.
* Upon successful completion, corresponding changes to generated code (if any)
  will be pushed to `<my-branch>` in a commit authored by `cloud-java-bot`.

#### Running Script (locally): 

Script: [generate.sh](scripts/generate.sh)

Requirements (to run commands below):
* Java 17
* [bazelisk](https://github.com/bazelbuild/bazelisk)
* [buildozer](https://github.com/bazelbuild/buildtools/tree/master/buildozer)
* [jq](https://jqlang.github.io/jq/download/)
* [xmllint](https://gnome.pages.gitlab.gnome.org/libxml2/xmllint.html)
* For MacOS, set up [gnu-sed](https://formulae.brew.sh/formula/gnu-sed) to use as `sed`

To execute the generation process locally, run (from the `spring-cloud-generator` directory):
```
bash scripts/generate.sh
```

### Showcase Testing for the Generator

Corresponding workflow file: [showcaseTests.yaml](/.github/workflows/showcaseTests.yaml)

Script: [generate-showcase.sh](scripts/generate-showcase.sh)

Requirements (to run commands below):
* Java 17
* [bazelisk](https://github.com/bazelbuild/bazelisk)
* [buildozer](https://github.com/bazelbuild/buildtools/tree/master/buildozer)
* For MacOS, set up [gnu-sed](https://formulae.brew.sh/formula/gnu-sed) to use as `sed`

To execute showcase golden tests locally, run (from the `spring-cloud-generator` directory):
```
bash scripts/generate-showcase.sh
```
* This runs the generator for showcase clients and compares its output against the expected golden `showcase-spring-starter` module. 

To update showcase golden tests locally, run (from the `spring-cloud-generator` directory):
```
bash scripts/generate-showcase.sh -u
```
* This runs the generator for showcase clients and overwrites the golden module `showcase-spring-starter`.

To execute showcase unit tests locally, run (from the `spring-cloud-generator` directory):
```
cd showcase/showcase-spring-starter && mvn verify
```
* This compiles the generated `showcase-spring-starter` module and runs handwritten unit tests under `showcase-spring-starter/test`.
