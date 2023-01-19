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

pyenv global 3.9.5
python3 --version

# Get into the spring-cloud-gcp repo directory
dir=$(dirname "$0")
pushd $dir/../

# install pandoc for combining md files
sudo apt install -q -y pandoc

# Install docuploader package
python3 -m pip install --require-hashes -r .kokoro/requirements.txt

python3 -m docuploader --version

# change to release version
./mvnw versions:set --batch-mode -DremoveSnapshot -DprocessAllModules

# Compute the project version.
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo ${PROJECT_VERSION}

# Build the javadocs
./mvnw clean javadoc:aggregate -Drelease=true -P docFX

# remove the trailing `-SNAPSHOT` for the current version
sed -i 's/-SNAPSHOT$//' CHANGELOG.md

# print 20 lines to verify
head -20 CHANGELOG.md
# end remove BUILD-SNAPSHOT contents from changelog

# copy CHANGELOG
cp CHANGELOG.md target/docfx-yml/history.md

# combine all doc to documentation.md
sudo pandoc \
  docs/src/main/md/first-page.md          \
  docs/src/main/md/getting-started.md     \
  docs/src/main/md/core.md                \
  docs/src/main/md/storage.md             \
  docs/src/main/md/sql.md                 \
  docs/src/main/md/pubsub.md              \
  docs/src/main/md/spring-integration.md  \
  docs/src/main/md/spring-stream.md       \
  docs/src/main/md/spring-cloud-bus-pubsub.md\
  docs/src/main/md/trace.md               \
  docs/src/main/md/logging.md             \
  docs/src/main/md/metrics.md             \
  docs/src/main/md/spanner.md             \
  docs/src/main/md/datastore.md           \
  docs/src/main/md/firestore.md           \
  docs/src/main/md/memorystore.md         \
  docs/src/main/md/bigquery.md            \
  docs/src/main/md/security-iap.md        \
  docs/src/main/md/vision.md              \
  docs/src/main/md/secretmanager.md       \
  docs/src/main/md/kms.md                 \
  docs/src/main/md/config.md              \
  docs/src/main/md/cloudfoundry.md        \
  docs/src/main/md/kotlin.md              \
  docs/src/main/md/configuration.md       \
  docs/src/main/md/migration-guide-1.x.md \
  docs/src/main/md/migration-guide-3.x.md \
  -t markdown_github -o docs/src/main/md/documentation.md

# copy and replace {project-version} documentation
sed "s/{project-version}/${PROJECT_VERSION}/g" docs/src/main/md/documentation.md > target/docfx-yml/documentation.md
# copy appendix.md
cp docs/src/main/md/appendix.md target/docfx-yml/appendix.md

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
# add changelog to toc
insertAfter toc.yml \
"  items:" "  - name: \"Version history\"\n    href: \"history.md\""
# add documentation.md to toc (after the first `  items:`)
insertAfter toc.yml \
"  items:" "  - name: \"Documentation\"\n    href: \"documentation.md\""

# check change to toc.yml -- remove after verified
head -20 toc.yml

python3 -m docuploader create-metadata \
    --name spring-framework-on-google-cloud \
    --version ${PROJECT_VERSION} \
    --language java \
    --stem "/java/docs/spring/reference"


python3 -m docuploader upload . \
    --credentials ${CREDENTIALS} \
    --staging-bucket ${STAGING_BUCKET_V2}\
    --destination-prefix docfx

popd
popd
