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

configs=$(yq '.libraries[]' ./google-cloud-java/generation_config.yaml)
# Properly format the configs as a JSON array
# This includes adding commas between objects and wrapping everything in square brackets
json_array="[ $(echo "$configs" | tr '\n' ' ' | sed 's/} {/}, {/g') ]"

# Parse each object in the JSON array
while IFS= read -r config; do
    # Extract library_name if present, otherwise use api_shortname
    unique_module_name=$(echo "$config" | jq -r '.library_name // .api_shortname')
    # Display the unique module name
    echo "Unique Module Name: $unique_module_name"
    distribution_name=$(echo "$config" | jq -r '.distribution_name // ""')
    if [ -z "$distribution_name" ]; then
      distribution_name="com.google.cloud:google-cloud-${unique_module_name}"
    fi
    echo "Distribution name: $distribution_name"
    library_type=$(echo "$config" | jq -r '.library_type // "GAPIC_AUTO"') # default to GAPIC_AUTO per https://github.com/googleapis/sdk-platform-java/blob/v2.40.0/library_generation/model/library_config.py#L57
    echo "library_type: $library_type"
    release_level=$(echo "$config" | jq -r '.release_level // "preview"') # default to preview per https://github.com/googleapis/sdk-platform-java/blob/v2.40.0/library_generation/model/library_config.py#L58
    echo "release_level: $release_level"
    monorepo_folder="java/${unique_module_name}"
    echo "monorepo folder: $monorepo_folder"
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
    proto_paths_stable=$(echo "$config" | yq -r '.GAPICs[] | select(.proto_path | test("/v[0-9]+$")) | .proto_path')
    echo "proto_paths_stable : $proto_paths_stable"
    proto_paths_latest=$(echo "$proto_paths_stable" | sort -d -r | head -n 1)
    echo "proto_paths_latest : $proto_paths_latest"

  echo "$unique_module_name, $proto_paths_latest, $distribution_name, $monorepo_folder" >> $filename
  count=$((count+1))
done < <(echo "$json_array" | jq -c '.[]')
echo "Total in-scope client libraries: $count"
