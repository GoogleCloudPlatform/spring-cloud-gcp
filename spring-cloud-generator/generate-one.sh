#!/bin/bash

# start  by experimenting local run.
# note about space consumption: out-of-space testing on cloud shell instance.

# poc with one specified repo - vision
#cmd line:: ./generate-one.sh -c vision -v 3.1.2 -i google-cloud-vision -g com.google.cloud -p 3.5.0-SNAPSHOT -d 1

set -x

# by default, do not download repos
download_repos=0
while getopts c:v:i:g:d:p:f:x: flag
do
    case "${flag}" in
        c) client_lib_name=${OPTARG};;
        v) version=${OPTARG};;
        i) client_lib_artifactid=${OPTARG};;
        g) client_lib_groupid=${OPTARG};;
        p) parent_version=${OPTARG};;
        f) googleapis_folder=${OPTARG};;
        x) googleapis_commitish=${OPTARG};;
        d) download_repos=1;;
    esac
done
echo "Client Library Name: $client_lib_name";
echo "Client Library Version: $version";
echo "Client Library GroupId: $client_lib_groupid";
echo "Client Library ArtifactId: $client_lib_artifactid";
echo "Parent Pom Version: $parent_version";
echo "Googleapis Folder: $googleapis_folder";
echo "Googleapis Commitish: $googleapis_comittish";

starter_artifactid="$client_lib_artifactid-spring-starter"

# setup git

## install bazel - commented out as running on local already with bazel
#sudo apt-get update
#sudo apt-get install bazel

WORKING_DIR=`pwd`


if [[ $download_repos -eq 1 ]]; then
  bash download-repos.sh
fi

cd googleapis
git reset --hard $googleapis_comittish
# tell bazelisk to use bazel version 4.2.2
echo '4.2.2' > .bazelversion
# In googleapis/WORKSPACE, find http_archive() rule with name = "gapic_generator_java",
# and replace with local_repository() rule
LOCAL_REPO="local_repository(\n    name = \\\"gapic_generator_java\\\",\n    path = \\\"..\/gapic-generator-java\/\\\",\n)"
perl -0777 -pi -e "s/http_archive\(\n    name \= \"gapic_generator_java\"(.*?)\)/$LOCAL_REPO/s" WORKSPACE

# In googleapis/WORKSPACE, find maven_install() rule with artifacts = PROTOBUF_MAVEN_ARTIFACTS,
# replace with googleapis-dep-string.txt which adds spring dependencies
perl -0777 -pi -e "s{maven_install\(\n(.*?)artifacts = PROTOBUF_MAVEN_ARTIFACTS(.*?)\)}{$(cat ../googleapis-dep-string.txt)}s" WORKSPACE

# In googleapis/repository_rules.bzl, add switch for new spring rule
JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@gapic_generator_java\/\/rules_java_gapic:java_gapic_spring.bzl\\\",\n    )"
perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl

## If $googleapis_folder does not exist, exit 
if [ ! -d "$googleapis_folder" ]
then
  echo "Directory $googleapis_folder DOES NOT exists."
  exit
fi

# Modify BUILD.bazel file for library
# Additional rule to load
SPRING_RULE_NAME="    \\\"java_gapic_spring_library\\\","
perl -0777 -pi -e "s/(load\((.*?)\"java_gapic_library\",)/\$1\n$SPRING_RULE_NAME/s" $googleapis_folder/BUILD.bazel
# Duplicate java_gapic_library rule definition
perl -0777 -pi -e "s/(java_gapic_library\((.*?)\))/\$1\n\n\$1/s" $googleapis_folder/BUILD.bazel
# Update rule name to java_gapic_spring_library
perl -0777 -pi -e "s/(java_gapic_library\()/java_gapic_spring_library\(/s" $googleapis_folder/BUILD.bazel
# Update name argument to have _spring appended
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)name = \"(.*?)\")/java_gapic_spring_library\(\$2name = \"\$3_spring\"/s" $googleapis_folder/BUILD.bazel
# todo: better way to remove the following unused arguments?
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    test_deps = \[(.*?)\](.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    deps = \[(.*?)\](.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    rest_numeric_enums = (.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel

# sometimes the rule name doesnt have the same prefix as $client_lib_name
# we use this perl command capture the correct prefix
rule_prefix=$(cat $googleapis_folder/BUILD.bazel | grep _java_gapic_spring | perl -lane 'print m/"(.*)_java_gapic_spring/')

echo "CALL BAZEL TARGET"
# call bazel target
bazelisk build //$googleapis_folder:"$rule_prefix"_java_gapic_spring || exit

cd -

## copy spring code to outside
mkdir -p ../spring-cloud-previews
cp googleapis/bazel-bin/$googleapis_folder/"$rule_prefix"_java_gapic_spring-spring.srcjar ../spring-cloud-previews

# unzip spring code
cd ../spring-cloud-previews
unzip -o "$rule_prefix"_java_gapic_spring-spring.srcjar -d "$starter_artifactid"/
rm -rf "$rule_prefix"_java_gapic_spring-spring.srcjar

# override versions & names in pom.xml

sed -i 's/{{client-library-group-id}}/'"$client_lib_groupid"'/' "$starter_artifactid"/pom.xml
sed -i 's/{{client-library-artifact-id}}/'"$client_lib_artifactid"'/' "$starter_artifactid"/pom.xml
sed -i 's/{{client-library-version}}/'"$version"'/' "$starter_artifactid"/pom.xml
sed -i 's/{{parent-version}}/'"$parent_version"'/' "$starter_artifactid"/pom.xml


# add module after line with pattern, check for existence. Also add readme line if $3 is set to 1.
# args: 1 -  path-to-pom-file; 2 - string-pattern; 3 - 1 if need to generate readme line
add_module_to_pom () {
  xmllint --debug --nsclean --xpath  "//*[local-name()='module']/text()" $1 | sort | uniq | grep -q $starter_artifactid
  found_library_in_pom=$?
  if [[ found_library_in_pom -eq 0 ]]; then
    echo "module $starter_artifactid already found in $1 modules"
  else
    echo "adding module $starter_artifactid to pom"
    sed -i "/$2/a\ \ \ \ <module>"$starter_artifactid"</module>" $1
    if [[ ${3:-1} -eq 1 ]]; then
      # also write to spring-cloud-previews/README.md
      # format |name|distribution name|
      echo -e "|$client_lib_name|com.google.cloud:$starter_artifactid|" >> README.md
      {(grep -vw ".*:.*" README.md);(grep ".*:.*" README.md| sort | uniq)} > tmpfile && mv tmpfile README.md
    fi
  fi
}

add_module_to_pom pom.xml "^  <modules>" 1
add_module_to_pom ../spring-cloud-preview/pom.xml "^[[:space:]]*<!--  preview modules  -->"

# remove downloaded repos
cd ../spring-cloud-generator
if [[ $download_repos -eq 1 ]]; then
  rm -rf googleapis
  rm -rf gapic-generator-java
fi

set +x
