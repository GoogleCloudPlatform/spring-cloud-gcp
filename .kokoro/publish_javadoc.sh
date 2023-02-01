#!/bin/bash
set -eov pipefail

if [[ -z "${CREDENTIALS}" ]]; then
  CREDENTIALS=${KOKORO_KEYSTORE_DIR}/73713_docuploader_service_account
fi

# Get into the spring-cloud-gcp repo directory
dir=$(dirname "$0")
pushd $dir/../

# Compute the project version.
PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

python3 --version

# install docuploader package
python3 -m pip install --require-hashes -r .kokoro/requirements.txt


python3 -m docuploader create-metadata --help

# Build the javadocs
#mvn clean javadoc:aggregate -Drelease=true
#
### Move into generated docs directory
#pushd target/site/apidocs/
#
#python3 -m docuploader create-metadata \
#     --name spring-cloud-gcp \
#     --version ${PROJECT_VERSION} \
#     --language java
#
#python3 -m docuploader upload . \
#     --credentials ${CREDENTIALS} \
#     --staging-bucket docs-staging

popd
popd
