#!/bin/bash

MODULE_UNDER_TEST=storage-sample

run_sample_tests () {
   module_name=$(echo "$MODULE_UNDER_TEST" | cut -d '-' -f 1)
    echo "${module_name}"
    directory_names=$(ls spring-cloud-gcp-samples)
    module_samples=()
    for dir in $directory_names; do
      if [[ $dir =~ $module_name ]]; then
        module_samples+=("$dir")
        echo $dir
      fi
    done

    joined_sample_names=$(echo "${module_samples[@]// /,}" | sed 's/ /,/g')
    project_names="spring-cloud-gcp-samples:${joined_sample_names}"

    mvn --batch-mode --activate-profiles native-sample-config,nativeTest --define notAllModules=true --define maven.javadoc.skip=true test
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

