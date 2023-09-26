#!/bin/bash

# Get directory of script
scriptDir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

cd "${scriptDir}/../../.." # git repo root

run_sample_tests () {
   module_name=$(echo "$MODULE_UNDER_TEST" | cut -d '-' -f 1)
    directory_names=$(ls 'spring-cloud-gcp-samples')
    module_samples=()
    for dir in $directory_names; do
      if [[ $dir =~ $module_name ]]; then
        module_samples+=("spring-cloud-gcp-samples/$dir")
      fi
    done

    joined_sample_names=$(echo "${module_samples[@]}" | sed 's/ /,/g')
    project_names="${joined_sample_names}"
    mvn clean --activate-profiles native-sample-config,nativeTest --define notAllModules=true --define maven.javadoc.skip=true -pl="${project_names}" test
}

run_module_tests() {
  if [[ "$MODULE_UNDER_TEST" = "datastore" || "$MODULE_UNDER_TEST" = "firestore" || "$MODULE_UNDER_TEST" = "spanner" ]]; then
    projectName="spring-cloud-gcp-data-${MODULE_UNDER_TEST}"
  else
    projectName="spring-cloud-gcp-${MODULE_UNDER_TEST}"
  fi
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

