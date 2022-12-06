#!/bin/bash

#cmd line:: ./generate-library-list.sh -c 9ff8aa652e195965037706a3455a8e8aa318be5c

while getopts c: flag
do
    case "${flag}" in
        c) commitish=${OPTARG};;
    esac
done
echo "monorepo commitish to checkout: $commitish";


# install jq for json parsing if not already installed
sudo apt-get -y install jq

# download the monorepo, need to loop through metadata there
git clone https://github.com/googleapis/google-cloud-java.git

# get googleapis repo to compute googleapis_folder
git clone https://github.com/googleapis/googleapis.git
git checkout f88ca86

# switch to the specified release commitish
cd ./google-cloud-java
if [ -z "$commitish" ];
  then echo "No commitish provided, using HEAD.";
  else git checkout $commitish;
fi

cd -

# start file, always override is present
filename=./library_list.txt
echo "# api_shortname, googleapis-folder, distribution_name:version" > $filename

count=0
for d in ./google-cloud-java/*java-*/; do
  # parse variables from .repo-metadata.json
  language=$(cat $d/.repo-metadata.json | jq -r .language)
  api_shortname=$(cat $d/.repo-metadata.json | jq -r .api_shortname)
  distribution_name=$(cat $d/.repo-metadata.json | jq -r .distribution_name)
  library_type=$(cat $d/.repo-metadata.json | jq -r .library_type)
  transport=$(cat $d/.repo-metadata.json | jq -r .transport)
  release_level=$(cat $d/.repo-metadata.json | jq -r .release_level)

  group_id=$(echo $distribution_name | cut -f1 -d:)
  artifact_id=$(echo $distribution_name | cut -f2 -d:)
  if [[ $library_type != *GAPIC_AUTO* ]] ; then
    echo "$d: non auto type: $library_type"
    continue
  fi
  if [[ $transport != *grpc* ]] ; then
    echo "$d: transport type not in scope: $transport"
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

  # get version from versions.txt
  # this changed in https://github.com/googleapis/google-cloud-java/pull/8755/
  module_versions=$(sed -n "/^${artifact_id}:/p" ./google-cloud-java/versions.txt)

#  module=$(echo "$module_versions" | cut -f1 -d:)
  released_version=$(echo "$module_versions" | cut -f2 -d':')
#  current_version=$(echo "$module_versions" | cut -f3 -d:)

  # heuristic approach to compute googleapis path: uses first directory in
  # googleapis matching "**/api_shortname/v[0-9]" (may bring any version)
  googleapis_path=$(find googleapis/google -type d -name 'v[0-9]' | grep $api_shortname/v | head -n1 | cut -d'/' -f2-)
  if [[ -z "$googleapis_path" ]] ; then
    echo "$api_shortname" not found in googleapis
    continue
  fi

  # infer googleapis folder from api_shortname:
  # this is not reliable needs manual verification,
  # will replace with metadata from google-cloud-java/artifact
  googleapis_folder="//$googleapis_path"
  echo "$api_shortname, $googleapis_folder, $distribution_name:$released_version" >> $filename
  count=$((count+1))
done
echo "Total in-scope client libraries: $count"

rm -rf google-cloud-java/
rm -rf googleapis/
