#!/bin/bash

# get googleapis repo
git clone https://github.com/googleapis/googleapis.git

# Prepare `gapic-generator-java` with Spring generation ability.
# If keeping a copy in this repo, this is not needed.
# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get into gapic and checkout branch to use
cd gapic-generator-java
# git checkout replace-shared-w-autoconfig
git checkout pom-changes
git pull origin pom-changes
# go back to previous folder
cd -


cd googleapis
# fix googleapis committish for test/dev purpose
# git checkout f88ca86
# todo: change to local repo --> gapic
# very not stable change - todo: change to search and replace.

# In googleapis/WORKSPACE, find http_archive() rule with name = "gapic_generator_java",
# and replace with local_repository() rule
# LOCAL_REPO="local_repository(\n    name = \\\"gapic_generator_java\\\",\n    path = \\\"..\/gapic-generator-java\/\\\",\n)"
# perl -0777 -pi -e "s/http_archive\(\n    name \= \"gapic_generator_java\"(.*?)\)/$LOCAL_REPO/s" WORKSPACE

# In googleapis/WORKSPACE, find maven_install() rule with artifacts = PROTOBUF_MAVEN_ARTIFACTS,
# replace with googleapis-dep-string.txt which adds spring dependencies
# perl -0777 -pi -e "s{maven_install\(\n(.*?)artifacts = PROTOBUF_MAVEN_ARTIFACTS(.*?)\)}{$(cat ../googleapis-dep-string.txt)}s" WORKSPACE

# In googleapis/repository_rules.bzl, add switch for new spring rule
# JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@gapic_generator_java\/\/rules_java_gapic:java_gapic_spring.bzl\\\",\n    )"
# perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl

cd -

