#!/bin/bash

if [ -z "$MODULE_UNDER_TEST" ]; then
  echo "Please specify the MODULE_UNDER_TEST."
  exit 1
fi

if [[ "$MODULE_UNDER_TEST" = "datastore" || "$MODULE_UNDER_TEST" = "firestore" || "$MODULE_UNDER_TEST" = "spanner" ]]; then
  testDirectory="spring-cloud-gcp-data-${MODULE_UNDER_TEST}"
else
  testDirectory="spring-cloud-gcp-${MODULE_UNDER_TEST}"
fi

mvn verify -Pspring-native,!default -pl="${testDirectory}"