#!/bin/bash
while getopts x: flag
do
    case "${flag}" in
        x) googleapis_commitish=${OPTARG};;
    esac
done

if [[ -z $googleapis_commitish ]]; then
  echo 'usage setup-googleapis-rules.sh -x GOOGLEAPIS_COMMITISH'
  exit 1
fi

cd googleapis
git reset --hard $googleapis_commitish

# In googleapis/WORKSPACE, find http_archive() rule with name = "gapic_generator_java",
# and replace with local_repository() rule
LOCAL_REPO="local_repository(\n    name = \\\"gapic_generator_java\\\",\n    path = \\\"..\/gapic-generator-java\/\\\",\n)"
perl -0777 -pi -e "s/http_archive\(\n    name \= \"gapic_generator_java\"(.*?)\)/$LOCAL_REPO/s" WORKSPACE

# In googleapis/WORKSPACE, find maven_install() rule with artifacts = PROTOBUF_MAVEN_ARTIFACTS,
# replace with googleapis-dep-string.txt which adds spring dependencies
perl -0777 -pi -e "s{maven_install\(\n    artifacts = PROTOBUF_MAVEN_ARTIFACTS(.*?)\)}{$(cat ../googleapis-dep-string.txt)}s" WORKSPACE

# In googleapis/WORKSPACE, find maven_install() rule for gapic-generator-java jar, and remove
perl -0777 -pi -e "s{maven_install\(\n    artifacts = \[(.*?)_gapic_generator_java_version(.*?)\](.*?)\)}{}s" WORKSPACE

# In googleapis/WORKSPACE, add back lines for gapic_generator_java_repositories() if not found
if ! grep -q -F "gapic_generator_java_repositories()" WORKSPACE; then
  perl -0777 -pi -e "s{(grpc_java_repositories\(\))}{\$1\n\n$(cat ../googleapis-gapic-string.txt)}s" WORKSPACE
fi


# In googleapis/repository_rules.bzl, add switch for new spring rule
JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@gapic_generator_java\/\/rules_java_gapic:java_gapic_spring.bzl\\\",\n    )"
perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl

cd -
