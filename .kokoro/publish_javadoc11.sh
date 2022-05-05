#!/bin/bash
set -eov pipefail

if [[ -z "${CREDENTIALS}" ]]; then
  CREDENTIALS=${KOKORO_KEYSTORE_DIR}/73713_docuploader_service_account
fi

if [[ -z "${STAGING_BUCKET_V2}" ]]; then
  echo "Need to set STAGING_BUCKET_V2 environment variable"
  exit 1
fi

# switch to java 11
sudo update-java-alternatives --set java-1.11.0-openjdk-amd64
$JAVA_HOME/bin/javac -version
export JAVA_HOME=/usr/lib/jvm/java-1.11.0-openjdk-amd64
$JAVA_HOME/bin/javac -version

pyenv global 3.7.2

# Get into the spring-cloud-gcp repo directory
dir=$(dirname "$0")
pushd $dir/../

# change to release version
./mvnw versions:set --batch-mode -DremoveSnapshot -DprocessAllModules

# Compute the project version.
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

# Install docuploader package
python3 -m pip install gcp-docuploader

# Build the javadocs
./mvnw clean javadoc:aggregate -Drelease=true -P docFX

# Move into generated yml directory
pushd target/docfx-yml

python3 -m docuploader create-metadata \
    --name spring-cloud-gcp \
    --version ${PROJECT_VERSION} \
    --language java

python3 -m docuploader upload . \
    --credentials ${CREDENTIALS} \
    --staging-bucket ${STAGING_BUCKET_V2}
    --destination-prefix docfx

popd
popd
