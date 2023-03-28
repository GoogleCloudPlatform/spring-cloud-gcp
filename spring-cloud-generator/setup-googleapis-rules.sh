#!/bin/bash

cd googleapis

# delete http_archive rule by name "gapic_generator_java"
buildozer 'delete' WORKSPACE:gapic_generator_java

# add local_repository rule with name "gapic_generator_java"
buildozer 'new local_repository gapic_generator_java before com_google_api_gax_java' WORKSPACE:__pkg__

# point path to local repo
buildozer 'set path "../gapic_generator_java"' WORKSPACE:gapic_generator_java

# delete http_archive rule by name "com_google_api_gax_java"
buildozer 'delete' WORKSPACE:com_google_api_gax_java

# add local_repository rule with name "com_google_api_gax_java"
buildozer 'new local_repository com_google_api_gax_java after apic_generator_java' WORKSPACE:__pkg__

# point path to local repo
buildozer 'set path "../gapic_generator_java/gax-java"' WORKSPACE:com_google_api_gax_java

# delete existing maven_install rules
buildozer 'delete' WORKSPACE:%maven_install

# add custom maven_install rules
perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{$(cat ../googleapis-dep-string.txt)}" WORKSPACE

# In googleapis/repository_rules.bzl, add switch for new spring rule
JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@gapic_generator_java\/\/rules_java_gapic:java_gapic_spring.bzl\\\",\n    )"
perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl

cd -
