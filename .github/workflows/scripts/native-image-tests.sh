#!/bin/bash
# This script executes native image tests based on the MODULE_UNDER_TEST provided.
# If MODULE_UNDER_TEST is 'vision', for example, then it runs tests under spring-cloud-gcp-vision.
# If it is 'vision-sample' then it runs tests under spring-cloud-gcp-samples/spring-cloud-gcp-vision-api-sample
# and spring-cloud-gcp-samples/spring-cloud-gcp-vision-ocr-demo.

set -eo pipefail

# Get git repo root
scriptDir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
cd "${scriptDir}/../../.."

OLD_IFS="$IFS"
IFS='-' read -ra MATRIX_SUBSTRINGS <<< "$MODULE_UNDER_TEST"
IFS="$OLD_IFS"

is_desired_directory() {
  local directory="$1"

  # Exclude Spring Integration, Config Bus, Cloud Stream samples when testing with pubsub-sample
  if [ "$MODULE_UNDER_TEST" == "pubsub-sample" ]  && [[ "$directory" =~ "integration" || "$directory" =~ "stream" ||  "$directory" =~ "bus" ]] ; then
     return 1
  fi

  for substring in "${MATRIX_SUBSTRINGS[@]}"; do
    if [[ ! "$directory" =~ $substring ]]; then
      return 1
    fi
  done
  return 0
}

run_sample_tests () {
  pushd spring-cloud-gcp-samples
  directory_names=$(find -maxdepth 2 -type d -name 'spring-cloud-gcp-*' | sed 's/.\///')
  module_samples=()
  for dir in $directory_names; do
    if is_desired_directory "$dir"; then
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
    mvn package \
      -Pnative -Pnative-sample-config \
      --define skipTests \
      --define org.slf4j.simpleLogger.showDateTime=true \
      --define org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS
    mvn test \
      -Pnative-sample-config -PnativeTest \
      --define notAllModules=true \
      --define maven.javadoc.skip=true \
      --define org.slf4j.simpleLogger.showDateTime=true \
      --define org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS
    popd
    filtered_project_names="$(echo "${filtered_modules[@]}" | sed 's/ /,/g')"
    mvn clean test \
      --activate-profiles native-sample-config,nativeTest \
      --define notAllModules=true \
      --define maven.javadoc.skip=true \
      -pl="${filtered_project_names}" \
      --define org.slf4j.simpleLogger.showDateTime=true \
      --define org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS

  else
    project_names="$(echo "${module_samples[@]}" | sed 's/ /,/g')"
    mvn clean test \
      --activate-profiles native-sample-config,nativeTest \
      --define notAllModules=true \
      --define maven.javadoc.skip=true \
      -pl="${project_names}" \
      --define org.slf4j.simpleLogger.showDateTime=true \
      --define org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS
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
  mvn clean verify \
    -Pspring-native,!default \
    -pl="${project_names}" \
    --define org.slf4j.simpleLogger.showDateTime=true \
    --define org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS
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