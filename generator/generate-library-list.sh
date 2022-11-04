#!/bin/bash

#cmd line:: ./generate-library-list.sh -c b2a5f7207e9f9b2f933e75ef4fd44973a234326f

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

# switch to the specified release commitish
cd ./google-cloud-java
git checkout $commitish
cd -

# start file, always override is present
filename=./library_list.txt
echo "# api_shortname, distribution_name:version, library_type, transport, group_id, release_level" > $filename

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
  module_versions=$(sed -n '4p' $d/versions.txt)
#  module=$(echo "$module_versions" | cut -f1 -d:)
#  released_version=$(echo "$module_versions" | cut -f2 -d:)
  current_version=$(echo "$module_versions" | cut -f3 -d:)
  echo "$api_shortname, $distribution_name:$current_version, $library_type, $transport, $group_id, $release_level" >> $filename
  count=$((count+1))
done
echo "Total in-scope client libraries: $count"

rm -rf google-cloud-java/
