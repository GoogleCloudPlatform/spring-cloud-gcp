#!/bin/bash
# get googleapis repo
git clone https://github.com/googleapis/googleapis.git
# Prepare `gapic-generator-java` with Spring generation ability.
# If keeping a copy in this repo, this is not needed.
# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get into gapic and checkout branch to use
cd gapic-generator-java
git checkout spring-retry-patch
git pull origin spring-retry-patch
# go back to previous folder
cd -
