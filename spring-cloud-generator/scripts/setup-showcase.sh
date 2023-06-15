#!/bin/bash

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..

# Compute the parent project version.
cd ${SPRING_ROOT_DIR}
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

#Alternative: clone googleapis and gapic-showcase instead of sdk-platform-java
#git clone https://github.com/googleapis/googleapis.git
#git clone https://github.com/googleapis/gapic-showcase.git

# Install local snapshot jar for spring generator
# TODO(emmwang): this might need an install of spring-cloud-gcp from root
cd ${SPRING_GENERATOR_DIR} && mvn install

# Clone sdk-platform-java with showcase library for testing
git clone https://github.com/googleapis/sdk-platform-java.git
# TODO(emmwang): Find corresponding committish/version tag to checkout
# git checkout ${GAPIC_GENERATOR_JAVA_VERSION}

# Install showcase client libraries locally
# TODO(emmwang): this might need an install of sdk-platform-java from root
cd sdk-platform-java/showcase && mvn clean install

# Modify sdk-platform-java/WORKSPACE 0 adapted from setup-googleapi-rules.sh
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

bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 //showcase:showcase_java_gapic_spring

# Post-process generated modules - adapted from generate-one.sh
# Copy and unzip _java_gapic_spring-spring.srcjar
## copy spring code to outside
mkdir -p ${SPRING_GENERATOR_DIR}/showcase
cp ../bazel-bin/showcase/showcase_java_gapic_spring-spring.srcjar ${SPRING_GENERATOR_DIR}/showcase
cd ${SPRING_GENERATOR_DIR}/showcase
unzip -o showcase_java_gapic_spring-spring.srcjar -d showcase-spring-starter/
rm -rf showcase_java_gapic_spring-spring.srcjar

# Post-processing for placeholder fields in pom.xml
sed -i 's/{{client-library-group-id}}/com.google.cloud/' showcase-spring-starter/pom.xml
sed -i 's/{{client-library-artifact-id}}/gapic-showcase/' showcase-spring-starter/pom.xml
sed -i 's/{{parent-version}}/'"$PROJECT_VERSION"'/' showcase-spring-starter/pom.xml
# Add version for showcase
sed -i '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>0.0.1-SNAPSHOT</version>' showcase-spring-starter/pom.xml
# Update relative path to parent pom (different repo structure)
RELATIVE_PATH="\ \ \ \ <relativePath>..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
sed -i 's/^ *<relativePath>.*/'"$RELATIVE_PATH"'/g' showcase-spring-starter/pom.xml

# Run google-java-format on generated code
cd showcase-spring-starter
./../../../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

# Remove downloaded repos
cd ${SPRING_GENERATOR_DIR}
rm -rf sdk-platform-java

