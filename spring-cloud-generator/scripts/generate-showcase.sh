#!/bin/bash

# To VERIFY: ./scripts/generate-showcase.sh
# To UPDATE: /scripts/generate-showcase.sh -u
UPDATE=0
while getopts u flag
do
    case "${flag}" in
        u) UPDATE=1;;
    esac
done

# for reusing bazel setup modifications and post-processing steps
source ./scripts/generate-steps.sh

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..
SHOWCASE_STARTER_OLD_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter
SHOWCASE_STARTER_NEW_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter-generated

# $1 - directory containing existing showcase-spring-starter (golden)
# $2 - directory containing newly generated showcase-spring-starter
function verify(){
  OLD_DIR=$1
  NEW_DIR=$2
  SHOWCASE_STARTER_DIFF=$(diff -r ${NEW_DIR}/src/main ${OLD_DIR}/src/main)
  SHOWCASE_STARTER_POM_DIFF=$(diff -r ${NEW_DIR}/pom.xml ${OLD_DIR}/pom.xml)
  if [ "$SHOWCASE_STARTER_DIFF" != "" ] || [ "$SHOWCASE_STARTER_POM_DIFF" != "" ]
  then
      echo "Differences detected in generated showcase starter module: "
      echo "Diff from src/main: "
      echo $SHOWCASE_STARTER_DIFF
      echo "Diff from pom.xml: "
      echo $SHOWCASE_STARTER_POM_DIFF
      exit 1;
  else
      echo "No differences found in showcase-spring-starter"
      rm -r ${NEW_DIR}
  fi
}

# $1 - target directory for generated starter
function generate_showcase_spring_starter(){
  SHOWCASE_STARTER_DIR=$1

  # Compute the parent project version.
  cd ${SPRING_ROOT_DIR}
  PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
  cd ${SPRING_GENERATOR_DIR}
  GAPIC_GENERATOR_JAVA_VERSION=$(./../mvnw help:evaluate -Dexpression=gapic-generator-java-bom.version -q -DforceStdout)

  # Install local snapshot jar for spring generator
  cd ${SPRING_GENERATOR_DIR} && mvn install

  # Clone sdk-platform-java (with showcase library)
  git clone https://github.com/googleapis/sdk-platform-java.git
  git checkout "v${GAPIC_GENERATOR_JAVA_VERSION}"

  # Alternative considered: if showcase client library is available on Maven Central,
  # Instead of downloading sdk-platform-java/showcase (for client library, and generation setup),
  # Can instead download googleapis (for generation setup) and gapic-showcase (for protos)

  # Install showcase client libraries locally
  cd sdk-platform-java && mvn clean install -B -ntp -DskipTests -Dclirr.skip -Dcheckstyle.skip
  cd showcase && mvn clean install

  # Modify sdk-platform-java/WORKSPACE
  modify_workspace_file "../WORKSPACE" ".." "../../scripts/resources/googleapis_modification_string.txt"
  # Modify sdk-platform-java/showcase/BUILD.bazel
  # Add load("@spring_cloud_generator//:java_gapic_spring.bzl", "java_gapic_spring_library")
  buildozer 'new_load @spring_cloud_generator//:java_gapic_spring.bzl java_gapic_spring_library' BUILD.bazel:__pkg__
  # Add java_gapic_spring_library rule, with attrs copied from corresponding java_gapic_library rule
  modify_build_file "BUILD.bazel"

  # Invoke bazel target for generating showcase-spring-starter
  bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 //showcase:showcase_java_gapic_spring

  # Post-process generated modules
  # Unzip _java_gapic_spring-spring.srcjar and copy spring code to outside
  copy_and_unzip "../bazel-bin/showcase/showcase_java_gapic_spring-spring.srcjar" "showcase_java_gapic_spring-spring.srcjar" "${SPRING_GENERATOR_DIR}/showcase" ${SHOWCASE_STARTER_DIR}
  modify_starter_pom ${SHOWCASE_STARTER_DIR}/pom.xml "com.google.cloud" "gapic-showcase" $PROJECT_VERSION
  # Add version for showcase
  sed -i '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>0.0.1-SNAPSHOT</version>' ${SHOWCASE_STARTER_DIR}/pom.xml
  # Update relative path to parent pom (different repo structure)
  RELATIVE_PATH="\ \ \ \ <relativePath>..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
  sed -i 's/^ *<relativePath>.*/'"$RELATIVE_PATH"'/g' ${SHOWCASE_STARTER_DIR}/pom.xml

  # Run google-java-format on generated code
  run_formatter ${SHOWCASE_STARTER_DIR}

  # Remove downloaded repos
  rm -rf ${SPRING_GENERATOR_DIR}/sdk-platform-java
}

if [[ UPDATE -ne 0 ]]; then
    echo "Running script to perform showcase-spring-starter update"
    generate_showcase_spring_starter ${SHOWCASE_STARTER_OLD_DIR}
  else
    echo "Running script to perform showcase-spring-starter verification"
    generate_showcase_spring_starter ${SHOWCASE_STARTER_NEW_DIR}
    verify ${SHOWCASE_STARTER_OLD_DIR} ${SHOWCASE_STARTER_NEW_DIR}
fi
