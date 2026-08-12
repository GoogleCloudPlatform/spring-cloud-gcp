#!/bin/bash

set -ex

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

cd ${SPRING_GENERATOR_DIR}
# start file, always override is present
filename=${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt
echo "# library_name, googleapis_location, coordinates_version, monorepo_folder" > "$filename"

# loop through configs for the monorepo (google-cloud-java)
# Note that this logic will not work for non-cloud APIs
count=0

library_names=$(yq eval '.libraries[].name' ./google-cloud-java/librarian.yaml)

for name in $library_names; do
  metadata_file="./google-cloud-java/java-${name}/.repo-metadata.json"
  if [[ ! -f "$metadata_file" ]]; then
    echo "Warning: metadata file missing for $name: $metadata_file"
    continue
  fi

  config=$(cat "$metadata_file")
  unique_module_name=$(echo "$config" | jq -r '.repo_short' | sed 's/^java-//')
  echo "Unique Module Name: $unique_module_name"
  
  distribution_name=$(echo "$config" | jq -r '.distribution_name // ""')
  if [ -z "$distribution_name" ]; then
    distribution_name="com.google.cloud:google-cloud-${unique_module_name}"
  fi
  echo "Distribution name: $distribution_name"

  library_type=$(echo "$config" | jq -r '.library_type // "GAPIC_AUTO"')
  echo "library_type: $library_type"

  release_level=$(echo "$config" | jq -r '.release_level // "preview"')
  echo "release_level: $release_level"

  monorepo_folder="java-${unique_module_name}"
  echo "monorepo folder: $monorepo_folder"

  group_id=$(echo $distribution_name | cut -f1 -d:)
  artifact_id=$(echo $distribution_name | cut -f2 -d:)

  # filter to in-scope libraries
  if [[ $library_type != *GAPIC_AUTO* ]] ; then
    echo "$name: non auto type: $library_type"
    continue
  fi
  if [[ $group_id != "com.google.cloud" ]] ; then
    echo "$name: group_id not in scope: $group_id"
    continue
  fi
  if [[ $release_level != "stable" ]] ; then
    echo "$name: release_level: $release_level"
    continue
  fi

  # checks if library is in the manual modules exclusion list
  if [[ $(cat ${SPRING_GENERATOR_DIR}/scripts/resources/manual_modules_exclusion_list.txt | tail -n+2 | grep $artifact_id | wc -l) -ne 0 ]] ; then
    echo "$artifact_id is already present in manual modules."
    continue
  fi

  # Parse proto paths from librarian.yaml
  apis=$(yq eval '.libraries[] | select(.name == "'$name'") | .apis[].path' ./google-cloud-java/librarian.yaml)
  proto_paths_stable=$(echo "$apis" | grep -E '/v[0-9]+$')
  echo "proto_paths_stable : $proto_paths_stable"
  
  proto_paths_latest=$(echo "$proto_paths_stable" | sort -d -r | head -n 1)
  echo "proto_paths_latest : $proto_paths_latest"

  if [ -z "$proto_paths_latest" ]; then
    echo "Warning: no stable proto paths found for $name"
    continue
  fi

  echo "$unique_module_name, $proto_paths_latest, $distribution_name, $monorepo_folder" >> $filename
  count=$((count+1))
done
echo "Total in-scope client libraries: $count"
