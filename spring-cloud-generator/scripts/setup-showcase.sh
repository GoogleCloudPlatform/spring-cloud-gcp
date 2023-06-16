#!/bin/bash

# To VERIFY: ./scripts/setup-showcase.sh
# To UPDATE: /scripts/setup-showcase.sh -u
UPDATE=0
while getopts u flag
do
    case "${flag}" in
        u) UPDATE=1;;
    esac
done

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
  # do something
  SHOWCASE_STARTER_DIR=$1

  # Compute the parent project version.
  cd ${SPRING_ROOT_DIR}
  PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
  GAPIC_GENERATOR_JAVA_VERSION=$(./mvnw help:evaluate -Dexpression=gapic-generator-java-bom.version -q -DforceStdout)

  # Install local snapshot jar for spring generator
  # TODO(emmwang): this might need an install of spring-cloud-gcp from root
  cd ${SPRING_GENERATOR_DIR} && mvn install

  # Clone sdk-platform-java (with showcase library)
  git clone https://github.com/googleapis/sdk-platform-java.git
  git checkout "v${GAPIC_GENERATOR_JAVA_VERSION}"

  # Alternative considered: if showcase client library is available on Maven Central,
  # Instead of downloading sdk-platform-java/showcase (for client library, and generation setup),
  # Can instead download googleapis (for generation setup) and gapic-showcase (for protos)

  # Install showcase client libraries locally
  # TODO(emmwang): this might need an install of sdk-platform-java from root
  cd sdk-platform-java/showcase && mvn clean install

  # Modify sdk-platform-java/WORKSPACE adapted from setup-googleapi-rules.sh
  # Add local_repository() rule for spring_cloud_generator package
  # Replace local snapshot maven_install() for gapic_generator_java with spring_cloud_generator
  buildozer 'new local_repository spring_cloud_generator before com_google_api_gax_java' ../WORKSPACE:__pkg__
  buildozer 'set path ".."' ../WORKSPACE:spring_cloud_generator
  buildozer 'delete' ../WORKSPACE:%maven_install
  perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{\$1\n$(cat ../../scripts/resources/googleapis_modification_string.txt)}" ../WORKSPACE

  # Modify sdk-platform-java/showcase/BUILD.bazel - adapted from setup-build-rule.sh
  # Add load("@spring_cloud_generator//:java_gapic_spring.bzl", "java_gapic_spring_library")
  # Add java_gapic_spring_library rule, with attrs copied from corresponding java_gapic_library rule
  buildozer 'new_load @spring_cloud_generator//:java_gapic_spring.bzl java_gapic_spring_library' BUILD.bazel:__pkg__
  GAPIC_RULE_NAME="$(buildozer 'print name' BUILD.bazel:%java_gapic_library)"
  SPRING_RULE_NAME="${GAPIC_RULE_NAME}_spring"
  GAPIC_RULE_FULL="$(buildozer 'print rule' BUILD.bazel:%java_gapic_library)"

  buildozer "new java_gapic_spring_library $SPRING_RULE_NAME" BUILD.bazel:__pkg__
  attrs_array=("srcs" "grpc_service_config" "gapic_yaml" "service_yaml" "transport")
  for attribute in "${attrs_array[@]}"
    do
      echo "$attribute"
      if [[ $GAPIC_RULE_FULL = *"$attribute"* ]] ; then
              buildozer "copy $attribute $GAPIC_RULE_NAME" BUILD.bazel:$SPRING_RULE_NAME
          else
              echo "attribute $attribute not found in java_gapic_library rule, skipping"
          fi
    done

  # Invoke bazel target for generating showcase-spring-starter
  bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 //showcase:showcase_java_gapic_spring

  # Post-process generated modules
  # Unzip _java_gapic_spring-spring.srcjar and copy spring code to outside
  mkdir -p ${SPRING_GENERATOR_DIR}/showcase
  cp ../bazel-bin/showcase/showcase_java_gapic_spring-spring.srcjar ${SPRING_GENERATOR_DIR}/showcase
  cd ${SPRING_GENERATOR_DIR}/showcase
  unzip -o showcase_java_gapic_spring-spring.srcjar -d ${SHOWCASE_STARTER_DIR}/
  rm -rf showcase_java_gapic_spring-spring.srcjar

  # Post-processing for placeholder fields in pom.xml
  sed -i 's/{{client-library-group-id}}/com.google.cloud/' ${SHOWCASE_STARTER_DIR}/pom.xml
  sed -i 's/{{client-library-artifact-id}}/gapic-showcase/' ${SHOWCASE_STARTER_DIR}/pom.xml
  sed -i 's/{{parent-version}}/'"$PROJECT_VERSION"'/' ${SHOWCASE_STARTER_DIR}/pom.xml
  # Add version for showcase
  sed -i '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>0.0.1-SNAPSHOT</version>' ${SHOWCASE_STARTER_DIR}/pom.xml
  # Update relative path to parent pom (different repo structure)
  RELATIVE_PATH="\ \ \ \ <relativePath>..\/..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
  sed -i 's/^ *<relativePath>.*/'"$RELATIVE_PATH"'/g' showcase-spring-starter-generated/pom.xml

  # Run google-java-format on generated code
  cd ${SHOWCASE_STARTER_DIR}
  ./../../../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

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








