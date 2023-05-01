#!/bin/bash
set -eov pipefail

if [[ -z "${CREDENTIALS}" ]]; then
  CREDENTIALS=${KOKORO_KEYSTORE_DIR}/73713_docuploader_service_account
fi

## Get into the spring-cloud-gcp repo directory
dir=$(dirname "$0")
pushd $dir/../

# Compute the project version.
PROJECT_VERSION=$(grep "^spring-cloud-gcp:" "./versions.txt" | cut -d: -f3)

# install docuploader package
python3 -m pip install --require-hashes -r .kokoro/requirements.txt

# Build the javadocs
mvn clean javadoc:aggregate -Drelease=true

# Move into generated docs directory
pushd target/site/apidocs/

python3 -m docuploader create-metadata \
     --name spring-cloud-gcp \
     --version ${PROJECT_VERSION} \
     --language java

python3 -m docuploader upload . \
     --credentials ${CREDENTIALS} \
     --staging-bucket docs-staging

popd
popd
