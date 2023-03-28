set -e
download_repos=0
while getopts c:v:i:g:d:p:f:x:z:m: flag
do
    case "${flag}" in
        x) googleapis_commitish=${OPTARG};;
        f) googleapis_folder=${OPTARG};;
    esac
done


cd googleapis

## If $googleapis_folder does not exist, exit
if [ ! -d "$(pwd)/$googleapis_folder" ]
then
  echo "Directory $(pwd)/$googleapis_folder DOES NOT exists."
  exit
fi

git checkout $googleapis_commitish -- $googleapis_folder

# Modify BUILD.bazel file for library
# Additional rule to load
SPRING_RULE_NAME="    \\\"java_gapic_spring_library\\\","
perl -0777 -pi -e "s/(load\((.*?)\"java_gapic_library\",)/\$1\n$SPRING_RULE_NAME/s" $googleapis_folder/BUILD.bazel

# Add java_gapic_spring_library rule, with attrs copied from corresponding java_gapic_library rule
SPRING_RULE_NAME="$(buildozer 'print name' $googleapis_folder/BUILD.bazel:%java_gapic_library)_spring"

buildozer "new java_gapic_spring_library $SPRING_RULE_NAME" $googleapis_folder/BUILD.bazel:__pkg__
# Copy attributes from java_gapic_library rule
buildozer "copy srcs language_java_gapic" $googleapis_folder/BUILD.bazel:$SPRING_RULE_NAME
buildozer "copy grpc_service_config language_java_gapic" $googleapis_folder/BUILD.bazel:$SPRING_RULE_NAME
buildozer "copy gapic_yaml language_java_gapic" $googleapis_folder/BUILD.bazel:$SPRING_RULE_NAME
buildozer "copy service_yaml language_java_gapic" $googleapis_folder/BUILD.bazel:$SPRING_RULE_NAME
buildozer "copy transport language_java_gapic" $googleapis_folder/BUILD.bazel:$SPRING_RULE_NAME
