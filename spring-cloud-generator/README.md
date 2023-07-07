# Generator - Spring Boot Starters for Google Client Libraries


## Development Workflow

For local development in the `spring-cloud-generator` submodule,
first build `spring-cloud-gcp` from root:

```
mvn clean install -DskipTests
```

### Generation of Spring Boot Starters

Corresponding workflow file: [generateAutoConfigs.yml](/.github/workflows/generateAutoConfigs.yml)

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

Corresponding workflow file: [showcaseTests.yml](/.github/workflows/showcaseTests.yml)

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

To update showcase golden tests locally, run ((from the `spring-cloud-generator` directory):
```
bash scripts/generate-showcase.sh -u
```
* This runs the generator for showcase clients and overwrites the golden module `showcase-spring-starter`.

To execute showcase unit tests locally, run (from the `spring-cloud-generator` directory):
```
cd showcase/showcase-spring-starter && mvn verify
```
* This compiles the generated `showcase-spring-starter` module and runs handwritten unit tests under `showcase-spring-starter/test`.
