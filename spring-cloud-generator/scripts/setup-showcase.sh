#!/bin/bash

WORKING_DIR=`pwd` # spring-cloud-generator

#Alternative: clone googleapis and gapic-showcase instead of sdk-platform-java
#git clone https://github.com/googleapis/googleapis.git
#git clone https://github.com/googleapis/gapic-showcase.git

# install local snapshot jar for spring generator
cd ${WORKING_DIR} && mvn install

# Clone sdk-platform-java with showcase library for testing
git clone https://github.com/googleapis/sdk-platform-java.git
# TODO: Find corresponding committish/version tag to checkout
git checkout ${GAPIC_GENERATOR_JAVA_VERSION}

# Install showcase client libraries locally
cd sdk-platform-java/showcase && mvn clean install

# TODO: Modify WORKSPACE
# Add local_repository() rule for spring_cloud_generator package
# Replace local snapshot maven_install() for gapic_generator_java with spring_cloud_generator

# TODO: Modify BUILD.bazel
# Add load("@spring_cloud_generator//:java_gapic_spring.bzl", "java_gapic_spring_library")
# Add java_gapic_spring_library rule, with arguments copied from java_gapic_spring rule

bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 //showcase:showcase_java_gapic_spring

# TODO: Post-process generated modules
# Copy and unzip _java_gapic_spring-spring.srcjar
# Post-processing for placeholder fields in pom.xml

