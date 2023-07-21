#!/bin/bash
set -e

# To VERIFY: ./scripts/generate-showcase.sh
# To UPDATE: /scripts/generate-showcase.sh -u
UPDATE=0
while getopts u flag
do
    case "${flag}" in
        u) UPDATE=1;;
    esac
done

# For reusing bazel setup modifications and post-processing steps
source ./scripts/generate-steps.sh

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..
SHOWCASE_STARTER_OLD_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter
SHOWCASE_STARTER_NEW_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter-generated

# Verifies newly generated showcase-spring-starter against goldens
#
# $1 - directory containing existing showcase-spring-starter (golden)
# $2 - directory containing newly generated showcase-spring-starter
function verify(){
  OLD_DIR=$1
  NEW_DIR=$2

  SHOWCASE_STARTER_DIFF=$(diff -q -r ${NEW_DIR}/src/main ${OLD_DIR}/src/main)
  echo "the showcase starter diff is ${SHOWCASE_STARTER_DIFF}"

  SHOWCASE_STARTER_POM_DIFF=$(diff -q -r ${NEW_DIR}/pom.xml ${OLD_DIR}/pom.xml)
  echo "the showcase starter pom diff is ${SHOWCASE_STARTER_POM_DIFF}"

  if [ "$SHOWCASE_STARTER_DIFF" != "" ] || [ "$SHOWCASE_STARTER_POM_DIFF" != "" ]
  then
      echo "entering the if block of verify() function"
      echo "Differences detected in generated showcase starter module: "
      echo "Diff from src/main: "
      echo $SHOWCASE_STARTER_DIFF
      echo "Diff from pom.xml: "
      echo $SHOWCASE_STARTER_POM_DIFF
      exit 1;
  else
      echo "entering the else block of verify() function"
      echo "No differences found in showcase-spring-starter"
      rm -r ${NEW_DIR}
  fi
}

# Setup, generation, and post-processing steps for showcase-spring-starter
#
# $1 - target directory for generated starter
function generate_showcase_spring_starter(){
  SHOWCASE_STARTER_DIR=$1

  # Compute the parent project version.
  cd ${SPRING_ROOT_DIR}
  PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  cd ${SPRING_GENERATOR_DIR}
  GAPIC_GENERATOR_JAVA_VERSION=$(mvn help:evaluate -Dexpression=gapic-generator-java-bom.version -q -DforceStdout)

  if [[ -z "$GAPIC_GENERATOR_JAVA_VERSION" ]]; then
    echo "Missing sdk-platform-java commitish to checkout"
    exit 1
  fi

  # Clone sdk-platform-java (with showcase library)
  git clone https://github.com/googleapis/sdk-platform-java.git
  cd sdk-platform-java && git checkout "v${GAPIC_GENERATOR_JAVA_VERSION}"

  # Install showcase client libraries locally
  cd showcase && mvn clean install
  GAPIC_SHOWCASE_CLIENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

  # Alternative: if showcase client library is available on Maven Central,
  # Instead of downloading sdk-platform-java/showcase (for client library, and generation setup),
  # Can instead download googleapis (for generation setup) and gapic-showcase (for protos)

  # Modify sdk-platform-java/WORKSPACE
  modify_workspace_file "../WORKSPACE" ".." "../../scripts/resources/googleapis_modification_string.txt"
  # Modify sdk-platform-java/showcase/BUILD.bazel
  buildozer 'new_load @spring_cloud_generator//:java_gapic_spring.bzl java_gapic_spring_library' BUILD.bazel:__pkg__
  modify_build_file "BUILD.bazel"

  # Invoke bazel target for generating showcase-spring-starter
  bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 //showcase:showcase_java_gapic_spring

  # Post-process generated modules
  copy_and_unzip "../bazel-bin/showcase/showcase_java_gapic_spring-spring.srcjar" "showcase_java_gapic_spring-spring.srcjar" "${SPRING_GENERATOR_DIR}/showcase" ${SHOWCASE_STARTER_DIR}
  modify_starter_pom ${SHOWCASE_STARTER_DIR}/pom.xml "com.google.cloud" "gapic-showcase" $PROJECT_VERSION

  # Additional pom.xml modifications for showcase starter
  # Add explicit gapic-showcase version
  sed -i'' '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>'"$GAPIC_SHOWCASE_CLIENT_VERSION"'</version>' ${SHOWCASE_STARTER_DIR}/pom.xml
  # Update relative path to parent pom (different repo structure from starters)
  RELATIVE_PATH="\ \ \ \ <relativePath>..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
  sed -i'' 's/^ *<relativePath>.*/'"$RELATIVE_PATH"'/g' ${SHOWCASE_STARTER_DIR}/pom.xml

  # Run google-java-format on generated code
  run_formatter ${SHOWCASE_STARTER_DIR}
  echo "the command run_formatter ${SHOWCASE_STARTER_DIR} - has run succesfully."
  # Remove downloaded repos
  rm -rf ${SPRING_GENERATOR_DIR}/sdk-platform-java
  echo "the command [rm -rf ${SPRING_GENERATOR_DIR}/sdk-platform-java] - has run succesfully."
}

if [[ UPDATE -ne 0 ]]; then
    echo "Running script to perform showcase-spring-starter update"
    generate_showcase_spring_starter ${SHOWCASE_STARTER_OLD_DIR}
    echo "the command for showcase-spring-starter update - has run succesfully."
  else
    echo "Running script to perform showcase-spring-starter verification"

    generate_showcase_spring_starter ${SHOWCASE_STARTER_NEW_DIR}
    echo "the command for showcase-spring-starter verification - has run succesfully."

    verify ${SHOWCASE_STARTER_OLD_DIR} ${SHOWCASE_STARTER_NEW_DIR}
    echo "the command [verify ${SHOWCASE_STARTER_OLD_DIR} ${SHOWCASE_STARTER_NEW_DIR}] - has run succesfully."
fi