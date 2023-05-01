#!/bin/bash
# setting pipefail allows to capture the exit code of a command called earlier
# in a pipe chain. This is necessary since piping to tee will always exit
# 0 if pipefail is not set
set -o pipefail
set -e
WORKING_DIR=`pwd`

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
cd spring-cloud-generator

# When generate-one.sh fails, stores the captured stdout and stderr to a file
# with the name of the client library
# args 1 - library name;
save_error_info () {
  mkdir -p failed-library-generations
  mv tmp-generate-one-output failed-library-generations/$1
}

# runs generate-one.sh for each entry in library_list.txt
# repos are downloaded once before all generation jobs and then removed

bash download-repos.sh
bash setup-googleapis-rules.sh
libraries=$(cat $WORKING_DIR/library_list.txt | tail -n+2)


# modifies the BUILD files of each of the entries in the library list
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "preparing bazel rules for $library_name"
  bash $WORKING_DIR/setup-build-rule.sh \
    -f $googleapis_location \
    -x $googleapis_commitish 2>&1 | tee tmp-output || save_error_info "bazel_build"

done <<< $libraries

# fetches all `*java_gapic_spring` build rules and build them at once
cd googleapis
bazelisk query "attr(name, '.*java_gapic_spring', //...)" | xargs bazelisk build 2>&1 \
  | tee tmp-output || save_error_info "bazel_build"

cd -

while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  bash $WORKING_DIR/generate-one.sh \
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

cd ../spring-cloud-generator
rm -rf googleapis
rm -rf gapic-generator-java
