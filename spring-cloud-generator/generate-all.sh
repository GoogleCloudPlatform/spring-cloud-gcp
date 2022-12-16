#!/bin/bash

WORKING_DIR=`pwd`

cd ../
# Compute the project version.
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
cd spring-cloud-generator

# runs generate-one.sh for each entry in library_list.txt
# repos are downloaded once before all generation jobs and then removed

bash download-repos.sh
libraries=$(cat $WORKING_DIR/library_list.txt | tail -n+2)
while IFS=, read -r library_name googleapis_location coordinates_version committish; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  version=$(echo $coordinates_version | cut -f3 -d:)
  bash $WORKING_DIR/generate-one.sh -c $library_name -v $version -i $artifact_id -g $group_id -p $PROJECT_VERSION
done <<< $libraries


echo "run google-java-format on generated code"
cd ../spring-cloud-previews
./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

cd ../spring-cloud-generator
rm -rf googleapis
rm -rf gapic-generator-java

