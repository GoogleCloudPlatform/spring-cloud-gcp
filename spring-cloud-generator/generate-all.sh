#!/bin/bash
WORKING_DIR=`pwd`

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
libraries=$(cat $WORKING_DIR/library_list.txt | tail -n+2)
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  # setting pipefail allows to capture the exit code of a command called earlier
  # in a pipe chain. This is necessary since piping to tee will always exit
  # 0 if pipefail is not set
  set -o pipefail
  bash $WORKING_DIR/generate-one.sh \
    -c $library_name \
    -i $artifact_id \
    -g $group_id \
    -p $PROJECT_VERSION \
    -f $googleapis_location \
    -x $googleapis_commitish 2>&1 | tee tmp-generate-one-output || save_error_info $library_name
  set +o pipefail
done <<< $libraries
rm tmp-generate-one-output

echo "run google-java-format on generated code"
cd ../spring-cloud-previews
./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

cd ../spring-cloud-generator
rm -rf googleapis
rm -rf gapic-generator-java
