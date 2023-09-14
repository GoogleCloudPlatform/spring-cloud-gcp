#!/bin/bash

if [ -z "$MODULE_TO_TEST" ]; then
  echo "Please specify the MODULE_TO_TEST."
  exit 1
fi

case ${MODULE_TO_TEST} in
  vision)
    pl="spring-cloud-gcp-vision"
    ;;
  storage)
    pl="spring-cloud-gcp-storage"
    ;;
  spanner)
     pl="spring-cloud-gcp-spanner"
    ;;
  *)
    pl=""
    ;;
esac

mvn verify -Pspring-native,!default -pl="${pl}"