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
# Duplicate java_gapic_library rule definition
perl -0777 -pi -e "s/(java_gapic_library\((.*?)\))/\$1\n\n\$1/s" $googleapis_folder/BUILD.bazel
# Update rule name to java_gapic_spring_library
perl -0777 -pi -e "s/(java_gapic_library\()/java_gapic_spring_library\(/s" $googleapis_folder/BUILD.bazel
# Update name argument to have _spring appended
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)name = \"(.*?)\")/java_gapic_spring_library\(\$2name = \"\$3_spring\"/s" $googleapis_folder/BUILD.bazel
# todo: better way to remove the following unused arguments?
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    test_deps = \[(.*?)\](.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    deps = \[(.*?)\](.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel
perl -0777 -pi -e "s/(java_gapic_spring_library\((.*?)(\n    rest_numeric_enums = (.*?),))/java_gapic_spring_library\(\$2/s" $googleapis_folder/BUILD.bazel

