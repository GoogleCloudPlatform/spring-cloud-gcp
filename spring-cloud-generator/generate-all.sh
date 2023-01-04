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
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  bash $WORKING_DIR/generate-one.sh -c $library_name -v $PROJECT_VERSION -i $artifact_id -g $group_id -p $PROJECT_VERSION -f $googleapis_location -x $googleapis_commitish
done <<< $libraries

###  Uncomment these lines if testing locally  ###
#
# echo "install dependencies locally (for dev envs)"
# cd ../
# # for when previews is a module
# #mvn install -pl '!spring-cloud-previews' -DskipTests
# mvn install -DskipTests
# cd ./spring-cloud-previews


echo "run google-java-format on generated code"
cd ../spring-cloud-previews
./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false

cd ../spring-cloud-generator
rm -rf googleapis
rm -rf gapic-generator-java
