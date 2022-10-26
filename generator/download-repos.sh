#!/bin/bash

# get googleapis repo
git clone https://github.com/googleapis/googleapis.git

# Prepare `gapic-generator-java` with Spring generation ability.
# If keeping a copy in this repo, this is not needed.
# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get into gapic and checkout branch to use
cd gapic-generator-java
git checkout autoconfig-gen-draft2
# go back to previous folder
cd -


cd googleapis
# fix googleapis committish for test/dev purpose
git checkout f88ca86
# todo: change to local repo --> gapic
# very not stable change - todo: change to search and replace.
sed -i '274,278d' WORKSPACE

sed -i '274 i local_repository(\n    name = "gapic_generator_java",\n    path = "../gapic-generator-java/",\n)' WORKSPACE

cd -

