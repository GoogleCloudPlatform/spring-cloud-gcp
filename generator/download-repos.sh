#!/bin/bash

# get googleapis repo
git clone https://github.com/googleapis/googleapis.git

# Prepare `gapic-generator-java` with Spring generation ability.
# If keeping a copy in this repo, this is not needed.
# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get into gapic and checkout branch to use
cd gapic-generator-java
git checkout c51d51bc495ea1284087b66c369dedf741dd4824
# go back to previous folder
cd -


cd googleapis
# fix googleapis committish for test/dev purpose
git checkout f88ca86
# todo: change to local repo --> gapic
# very not stable change - todo: change to search and replace.

# In googleapis/WORKSPACE, find http_archive() rule with name = "gapic_generator_java",
# and replace with local_repository() rule
LOCAL_REPO="local_repository(\n    name = \\\"gapic_generator_java\\\",\n    path = \\\"..\/gapic-generator-java\/\\\",\n)"
perl -0777 -pi -e "s/http_archive\(\n    name \= \"gapic_generator_java\"(.*?)\)/$LOCAL_REPO/s" WORKSPACE

cd -

