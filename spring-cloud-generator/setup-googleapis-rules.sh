#!/bin/bash

cd googleapis

# add local_repository rule with name "google_cloud_spring_generator"
buildozer 'new local_repository google_cloud_spring_generator before gapic_generator_java' WORKSPACE:__pkg__
# point path to local repo
buildozer 'set path "../google-cloud-spring-generator"' WORKSPACE:google_cloud_spring_generator

# delete existing maven_install rules
buildozer 'delete' WORKSPACE:%maven_install

# In googleapis/WORKSPACE, find maven_install() rule for gapic-generator-java jar, and remove
# perl -0777 -pi -e "s{maven_install\(\n    artifacts = \[(.*?)_gapic_generator_java_version(.*?)\](.*?)\)}{}s" WORKSPACE

# add custom maven_install rules
perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{\$1\n$(cat ../googleapis-dep-string.txt)}" WORKSPACE

# In googleapis/repository_rules.bzl, add switch for new spring rule
JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@google_cloud_spring_generator\/\/:java_gapic_spring.bzl\\\",\n    )"
perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl


# remove empty package() call added from using target __pkg__
buildozer 'delete' WORKSPACE:%package

cd -
