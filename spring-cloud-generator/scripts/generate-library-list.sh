#!/bin/bash

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi

while getopts c: flag
do
    case "${flag}" in
        c) commitish=${OPTARG};;
    esac
done
echo "Monorepo tag: $commitish";

if [[ -z "$commitish" ]]; then
  echo "Missing google-cloud-java commitish to checkout"
  exit 1
fi

# download the monorepo, need to loop through metadata there
git clone https://github.com/googleapis/google-cloud-java.git

# switch to the specified release commitish
cd ./google-cloud-java
git checkout $commitish

# read googleapis committish used in hermetic build
googleapis_committish=$(yq -r ".googleapis_commitish" generation_config.yaml)

cd ${SPRING_GENERATOR_DIR}
# start file, always override is present
filename=${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt
echo "# api_shortname, googleapis-folder, distribution_name:version, googleapis_committish, monorepo_folder" > "$filename"

# loop through folders
count=0
for d in ./google-cloud-java/*java-*/; do
  # parse variables from .repo-metadata.json
  language=$(cat $d/.repo-metadata.json | jq -r .language)
  api_shortname=$(cat $d/.repo-metadata.json | jq -r .api_shortname)
  distribution_name=$(cat $d/.repo-metadata.json | jq -r .distribution_name)
  library_type=$(cat $d/.repo-metadata.json | jq -r .library_type)
  release_level=$(cat $d/.repo-metadata.json | jq -r .release_level)
  monorepo_folder=$(basename $d)

  group_id=$(echo $distribution_name | cut -f1 -d:)
  artifact_id=$(echo $distribution_name | cut -f2 -d:)
  #  filter to in-scope libraries
  if [[ $library_type != *GAPIC_AUTO* ]] ; then
    echo "$d: non auto type: $library_type"
    continue
  fi
  if [[ $group_id != "com.google.cloud" ]] ; then
    echo "$d: group_id not in scope: $group_id"
    continue
  fi
  if [[ $release_level != "stable" ]] ; then
    echo "$d: release_level: $release_level"
    continue
  fi
  # checks if library is in the manual modules exclusion list
  if [[ $(cat ${SPRING_GENERATOR_DIR}/scripts/resources/manual_modules_exclusion_list.txt | tail -n+2 | grep $artifact_id | wc -l) -ne 0 ]] ; then
    echo "$artifact_id is already present in manual modules."
    continue
  fi

  # get monorepo-name as pattern ./google-cloud-java/<monorepo_name>/
  monorepo_name=$(echo $d | sed 's#^./google-cloud-java/##' | sed 's#/$##')

  # get folder location source of truth from ".OwlBot.yaml"
  # will only consider the first occurence
  googleapis_path=$(grep -e 'java/gapic-google' $d/.OwlBot.yaml | head -n 1| sed 's#^.*"/##' | sed 's#/[^/]*/.\*-java.*$##')
  repo_folder=$(grep 'java/gapic-google' $d/.OwlBot.yaml -A1| tail -n1 |  sed "s#^.*$monorepo_name/[^/]*/##"| sed 's#"$##')

  # figure out path to look out changes for: v[1-9]
  # taking the latest version that's not alpha/beta
  version_folder=$(find "$d$repo_folder/main/java/com/google/" -type d -name 'v[0-9]' |sort -d -r | head -n 1 | sed "s#^$d##")
  version_number=$(echo $version_folder | sed 's#.*/##')
  googleapis_folder="$googleapis_path/$version_number"

  echo "$api_shortname, $googleapis_folder, $distribution_name, $googleapis_committish, $monorepo_folder" >> $filename
  count=$((count+1))
done
echo "Total in-scope client libraries: $count"

# clean up
rm -rf google-cloud-java/
