#!/bin/bash


# start  by experimenting local run.
# note about space consumption: out-of-space testing on cloud shell instance.

# poc with one specified repo - vision
#cmd line:: ./generate-one.sh -c vision -v 3.1.2 -i google-cloud-vision -g com.google.cloud

# by default, do not download repos
download_repos=0
while getopts c:v:i:g:d: flag
do
    case "${flag}" in
        c) client_lib_name=${OPTARG};;
        v) version=${OPTARG};;
        i) client_lib_artifactid=${OPTARG};;
        g) client_lib_groupid=${OPTARG};;
        d) download_repos=1;;
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


if [[ $download_repos -eq 1 ]]; then
  bash download-repos.sh
fi

# call bazel target - todo: separate target in future
cd googleapis
bazel build //google/cloud/$client_lib_name/v1:"$client_lib_name"_java_gapic

cd -

## copy spring code to outside
mkdir -p ../generated
cp googleapis/bazel-bin/google/cloud/$client_lib_name/v1/"$client_lib_name"_java_gapic_srcjar-spring.srcjar ../generated

# unzip spring code
cd ../generated
unzip -o "$client_lib_name"_java_gapic_srcjar-spring.srcjar -d "$client_lib_name"/
rm -rf "$client_lib_name"_java_gapic_srcjar-spring.srcjar

# override versions & names in pom.xml
cat "$client_lib_name"/pom.xml

sed -i 's/{{client-library-group-id}}/'"$client_lib_groupid"'/' "$client_lib_name"/pom.xml
sed -i 's/{{client-library-artifact-id}}/'"$client_lib_artifactid"'/' "$client_lib_name"/pom.xml
sed -i 's/{{client-library-version}}/'"$version"'/' "$client_lib_name"/pom.xml
sed -i 's/{{starter-version}}/0.0.1-SNAPSHOT/' "$client_lib_name"/pom.xml

# add module to parent, adds after the `<modules>` line, checks for existence
xmllint --debug --nsclean --xpath  "//*[local-name()='module']/text()" pom.xml | sort | uniq | grep -q $client_lib_name
found_library_in_pom=$?
if [[ found_library_in_pom -eq 0 ]]; then
  echo "module $client_lib_name already found in pom modules"
else
  echo "adding module $client_lib_name to pom"
  sed -i "/^  <modules>/a\ \ \ \ <module>"$client_lib_name"</module>" pom.xml
fi


# remove downloaded repos
cd ../generator
if [[ $download_repos -eq 1 ]]; then
  rm -rf googleapis
  rm -rf gapic-generator-java
fi
