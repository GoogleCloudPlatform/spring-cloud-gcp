#!/bin/bash

# This script executes native image tests based on the MODULE_UNDER_TEST provided.
# If MODULE_UNDER_TEST is 'vision', for example, then it runs tests under spring-cloud-gcp-vision.
# If it is 'vision-sample' then it runs tests under spring-cloud-gcp-samples/spring-cloud-gcp-vision-api-sample
# and spring-cloud-gcp-samples/spring-cloud-gcp-vision-ocr-demo.

set -eo pipefail

# Get git repo root
scriptDir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
cd "${scriptDir}/../../.."

run_sample_tests () {
  pushd spring-cloud-gcp-samples
  module_name=$(echo "$MODULE_UNDER_TEST" | cut -d '-' -f 1)
  directory_names=$(find -maxdepth 2 -type d -name 'spring-cloud-gcp-*' | sed 's/.\///')
  module_samples=()
  for dir in $directory_names; do
    if [[ $dir =~ $module_name ]]; then
      module_samples+=("$dir")
    fi
  done

  # The spring-cloud-gcp-pubsub-stream-functional-sample-test needs the process-aot step to explicitly be triggered
  # before native image tests. The module's application class is not tested through the SpringBootTest annotation
  # so it doesn't get automatically detected by the Spring AOT processor (See https://github.com/spring-projects/spring-boot/issues/35626)
  if [[ ${module_samples[*]} =~ "spring-cloud-gcp-pubsub-stream-functional-sample" ]]; then
    functional_test_directory_names=$(find -maxdepth 2 -type d -name 'spring-cloud-gcp-pubsub-stream-functional-*' | sed 's/\.\///')
    filtered_modules=()
    for sample in "${module_samples[@]}"; do
      if [[ ! $functional_test_directory_names =~ $sample ]]; then
        filtered_modules+=("$sample")
      fi
    done
    pushd spring-cloud-gcp-pubsub-stream-functional-sample/spring-cloud-gcp-pubsub-stream-functional-sample-test
    mvn package -Pnative -Pnative-sample-config -DskipTests
    mvn -Pnative-sample-config -PnativeTest --define notAllModules=true --define maven.javadoc.skip=true test
    popd
    filtered_project_names="$(echo "${filtered_modules[@]}" | sed 's/ /,/g')"
    mvn clean --activate-profiles native-sample-config,nativeTest --define notAllModules=true --define maven.javadoc.skip=true -pl="${filtered_project_names}" test

  else
    project_names="$(echo "${module_samples[@]}" | sed 's/ /,/g')"
    mvn clean --activate-profiles native-sample-config,nativeTest --define notAllModules=true --define maven.javadoc.skip=true -pl="${project_names}" test
  fi
  popd
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

