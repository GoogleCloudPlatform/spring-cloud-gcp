#!/bin/bash

# This script executes native image tests based on the MODULE_UNDER_TEST provided.
# If MODULE_UNDER_TEST is 'vision', for example, then it runs tests under spring-cloud-gcp-vision.
# If it is 'vision-sample' then it runs tests under spring-cloud-gcp-samples/spring-cloud-gcp-vision-api-sample
# and spring-cloud-gcp-samples/spring-cloud-gcp-vision-ocr-demo.

# Get git repo root
scriptDir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
cd "${scriptDir}/../../.."

run_sample_tests () {
  #  module_name: only cut the last  word after "-"
  module_name=$(echo "$MODULE_UNDER_TEST" | rev | cut -d '-' -f2- |rev)
  directory_names=$(ls 'spring-cloud-gcp-samples')
  module_samples=()
  for dir in $directory_names; do
    if [[ $dir =~ $module_name ]]; then
      module_samples+=("spring-cloud-gcp-samples/$dir")
    fi
  done
  project_names="$(echo "${module_samples[@]}" | sed 's/ /,/g')"
  mvn clean --activate-profiles native-sample-config,nativeTest --define notAllModules=true --define maven.javadoc.skip=true -pl="${project_names}" test
}

run_module_tests() {
  directory_names=$(ls)
  module_samples=()
  for dir in $directory_names; do
    if [[ $dir =~ $MODULE_UNDER_TEST ]] && [[ ! $dir =~ $EXCLUDED_MODULES  ]]; then
       module_samples+=($dir)
    fi
  done
  project_names="$(echo "${module_samples[@]}" | sed 's/ /,/g')"
  mvn clean verify -Pspring-native,!default -pl="${project_names}"
}

if [ -z "$MODULE_UNDER_TEST" ]; then
  echo "Please specify the MODULE_UNDER_TEST."
  exit 1
fi

if [[ "$MODULE_UNDER_TEST" == *"sample" ]]; then
  run_sample_tests
else
  run_module_tests
fi

