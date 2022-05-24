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
echo ${PROJECT_VERSION}

# Install docuploader package
python3 -m pip install --upgrade six
python3 -m pip install --upgrade protobuf
python3 -m pip install gcp-docuploader==0.6.2

python3 -m docuploader --version

# Build the javadocs
./mvnw clean javadoc:aggregate -Drelease=true -P docFX
## copy CHANGELOG
#cp CHANGELOG.md target/docfx-yml/history.md

# copy and replace {project-version} documentation
sed "s/{project-version}/${PROJECT_VERSION}/g" docs/src/main/asciidoc/index.md> target/docfx-yml/documentation.md
#cp docs/src/main/md/index.md target/docfx-yml/overview.md

# check change to documentation.md -- remove after verified
head -20 target/docfx-yml/documentation.md

# Move into generated yml directory
pushd target/docfx-yml

### manual changes to generated toc.yml,
# group all javadocs to dir and add documentation to fron

# insert after line starting with
function insertAfter
{
   local file="$1" line="$2" newText="$3"
   sed -i -e "/^$line$/a"$'\\\n'"$newText"$'\n' "$file"
}
# From line 4-2000, add 2 spaces to the front.
sed -i '4,2000 s/^/  /' toc.yml
# Add Javadocs dir
insertAfter toc.yml \
"  items:" "  - name: \"JavaDocs\"\n    items:"
# add documentation.md to tol (after the first `  items:`)
insertAfter toc.yml \
"  items:" "  - name: \"Documentation\"\n    href: \"documentation.md\""

# check change to toc.yml -- remove after verified
head -20 toc.yml

python3 -m docuploader create-metadata \
    --name spring-cloud-gcp-3.x \
    --version ${PROJECT_VERSION} \
    --language java \
    --stem "/java/docs/spring-cloud-gcp/reference"

## try to debug
#python3 -m docuploader upload --help


python3 -m docuploader upload . \
    --credentials ${CREDENTIALS} \
    --staging-bucket ${STAGING_BUCKET_V2}\
    --destination-prefix docfx

popd
popd
