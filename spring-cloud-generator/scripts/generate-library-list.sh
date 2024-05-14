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

cd ${SPRING_GENERATOR_DIR}
# start file, always override is present
filename=${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt
echo "# api_shortname, googleapis-folder, distribution_name:version, monorepo_folder" > "$filename"

# loop through configs for the monorepo (google-cloud-java)
# Note that this logic will not work for non-cloud APIs
count=0
configs=$(jq '.libraries[]' ./google-cloud-java/generate_config.yaml)
for config_str in $configs; do
  # parse variables from generation_config.yaml
  config=$(echo "$config_str" | yq e '.') # Parse config_str back to YAML format
  unique_module_name=$(echo "$config" | jq -r '.library_name // .api_shortname')
  # Determine distribution_name
  distribution_name=$(echo "$config" | jq -r '.distribution_name' // "")
  if [ -z "$distribution_name" ]; then
    distribution_name="com.google.cloud:google-cloud-${unique_module_name}"
  fi
  library_type=$(echo "$config" | jq -r '.library_type // "GAPIC_AUTO"') # default to GAPIC_AUTO per https://github.com/googleapis/sdk-platform-java/blob/v2.40.0/library_generation/model/library_config.py#L57
  release_level=$(echo "$config" | jq -r '.release_level // "preview"') # default to preview per https://github.com/googleapis/sdk-platform-java/blob/v2.40.0/library_generation/model/library_config.py#L58
  monorepo_folder="java/${unique_module_name}"

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

  # parse proto path from generation_config.yaml, find by unique_module_name, which will match library_name if it exists, or api_shortname if library_name does not exist
  # then sort and keep latest stable version
  library=$(yq -r '.libraries[] | select(.library_name == "'"$unique_module_name"'" or .api_shortname == "'"$unique_module_name"'" )' ./google-cloud-java/generation_config.yaml)
  proto_paths_stable=$(echo "$library" | yq -r '.GAPICs[] | select(.proto_path | test("/v[0-9]+$")) | .proto_path')
  proto_paths_latest=$(echo "$proto_paths_stable" | sort -d -r | head -n 1)

  echo "$api_shortname, $proto_paths_latest, $distribution_name, $monorepo_folder" >> $filename
  count=$((count+1))
done
echo "Total in-scope client libraries: $count"
