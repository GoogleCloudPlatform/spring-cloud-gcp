#!/bin/bash


# start  by experimenting local run.
# note about space consumption: out-of-space testing on cloud shell instance.

# poc with one specified repo - vision
#cmd line:: ./generate-one.sh -c vision -v 3.1.2 -i google-cloud-vision -g com.google.cloud

while getopts c:v:i:g: flag
do
    case "${flag}" in
        c) client_lib_name=${OPTARG};;
        v) version=${OPTARG};;
        i) client_lib_artifactid=${OPTARG};;
        g) client_lib_groupid=${OPTARG};;
    esac
done
echo "Client Library Name: $client_lib_name";
echo "Client Library Version: $version";
echo "Client Library GroupId: $client_lib_groupid";
echo "Client Library ArtifactId: $client_lib_artifactid";

# setup git

## install bazel - commented out as running on local already with bazel
#sudo apt-get update
#sudo apt-get install bazel

WORKING_DIR=`pwd`


# get googleapis repo
git clone https://github.com/googleapis/googleapis.git

# Prepare `gapic-generator-java` with Spring generation ability.
# If keeping a copy in this repo, this is not needed.
# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get into gapic and checkout branch to use
cd gapic-generator-java
#git checkout autoconfig-gen-draft2
git checkout write-pom
# go back to previous folder
cd -


cd googleapis
# todo: change to local repo --> gapic
# very not stable change - todo: change to search and replace.
sed -i '274,278d' WORKSPACE

sed -i '274 i local_repository(\n    name = "gapic_generator_java",\n    path = "../gapic-generator-java/",\n)' WORKSPACE

# call bazel target - todo: separate target in future
bazel build //google/cloud/$client_lib_name/v1:"$client_lib_name"_java_gapic

cd -

## copy spring code to outside
cp googleapis/bazel-bin/google/cloud/$client_lib_name/v1/"$client_lib_name"_java_gapic_srcjar-spring.srcjar ./

# unzip spring code
unzip "$client_lib_name"_java_gapic_srcjar-spring.srcjar -d "$client_lib_name"/
rm -rf "$client_lib_name"_java_gapic_srcjar-spring.srcjar

# override versions & names in pom.xml
cat "$client_lib_name"/pom.xml

sed -i 's/{{client-library-group-id}}/'"$client_lib_groupid"'/' "$client_lib_name"/pom.xml
sed -i 's/{{client-library-artifact-id}}/'"$client_lib_artifactid"'/' "$client_lib_name"/pom.xml
sed -i 's/{{client-library-version}}/'"$version"'/' "$client_lib_name"/pom.xml
sed -i 's/{{starter-version}}/0.0.1-SNAPSHOT/' "$client_lib_name"/pom.xml

rm -rf googleapis
rm -rf gapic-generator-java