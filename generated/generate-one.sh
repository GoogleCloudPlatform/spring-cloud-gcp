# start  by experimenting local run.
# note about space consumption: out-of-space testing on cloud shell instance.

# poc with one specified repo - vision

# setup git

## install bazel
#sudo apt-get update
#sudo apt-get install bazel

WORKING_DIR=`pwd`

# Checkout `gapic-generator-java`
git clone https://github.com/googleapis/gapic-generator-java.git
# get googleapis repo
git clone https://github.com/googleapis/googleapis.git


# get into gapic and checkout branch to use
cd gapic-generator-java
git checkout autoconfig-gen-draft2

# # todo: push this branch first
# git checkout write-pom
# go back to previous folder
cd -

ls
cd googleapis
# todo: change to local repo --> gapic

# very not stable change - todo: change to search and replace.
sed -i '274,278d' WORKSPACE

sed -i '274 i local_repository(\n    name = "gapic_generator_java",\n    path = "../gapic-generator-java/",\n)' WORKSPACE

# call bazel target - todo: separate target in future
bazel build //google/cloud/vision/v1:vision_java_gapic

cd -

## copy spring code to outside
cp googleapis/bazel-bin/google/cloud/vision/v1/vision_java_gapic_srcjar-spring.srcjar ./

# unzip spring code
unzip vision_java_gapic_srcjar-spring.srcjar -d vision/
rm -rf vision_java_gapic_srcjar-spring.srcjar

rm -rf googleapis
rm -rf gapic-generator-java