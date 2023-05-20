#!/bin/bash
# setting pipefail allows to capture the exit code of a command called earlier
# in a pipe chain. This is necessary since piping to tee will always exit
# 0 if pipefail is not set
set -o pipefail
set -e
WORKING_DIR=`pwd` # spring-cloud-generator

while getopts m: flag
do
    case "${flag}" in
        m) monorepo_commitish=${OPTARG};;
    esac
done
echo "Monorepo commitish: $monorepo_commitish";

cd ../
# Compute the project version.
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
cd ${WORKING_DIR}

# When generate-one.sh fails, stores the captured stdout and stderr to a file
# with the name of the client library
# args 1 - library name;
save_error_info () {
  mkdir -p ${WORKING_DIR}/failed-library-generations
  cp tmp-output ${WORKING_DIR}/failed-library-generations/$1
}

# runs generate-one.sh for each entry in library_list.txt
# repos are downloaded once before all generation jobs and then removed

git clone https://github.com/googleapis/googleapis.git
bash ${WORKING_DIR}/scripts/setup-googleapis-rules.sh
libraries=$(cat ${WORKING_DIR}/scripts/resources/library_list.txt | tail -n+2)


# modifies the BUILD files of each of the entries in the library list
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "preparing bazel rules for $library_name"
  bash ${WORKING_DIR}/scripts/setup-build-rule.sh \
    -f $googleapis_location \
    -x $googleapis_commitish 2>&1 | tee tmp-output || save_error_info "bazel_build"

done <<< $libraries


# install local snapshot jar for spring generator
cd ${WORKING_DIR} && mvn install

# fetches all `*java_gapic_spring` build rules and build them at once
cd googleapis
bazelisk query "attr(name, '.*java_gapic_spring', //...)" \
  | xargs bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 2>&1 \
  | tee tmp-output || save_error_info "bazel_build"

cd ${WORKING_DIR}

while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  bash ${WORKING_DIR}/scripts/generate-one.sh \
    -c $library_name \
    -i $artifact_id \
    -g $group_id \
    -p $PROJECT_VERSION \
    -f $googleapis_location \
    -m $monorepo_folder \
    -x $googleapis_commitish \
    -z $monorepo_commitish 2>&1 | tee tmp-output || save_error_info "GENERATE_ONE_$library_name"
  set +o pipefail
done <<< $libraries
rm tmp-output

echo "run google-java-format on generated code"
cd ../spring-cloud-previews
./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

cd ${WORKING_DIR}
rm -rf googleapis
