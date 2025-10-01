#!/bin/bash

# This script updates the version variables in README.adoc to the latest
# released versions from Maven Central.

set -e

# Fetch the list of versions from Maven Central
versions=$(curl -s https://repo.maven.apache.org/maven2/com/google/cloud/spring-cloud-gcp/maven-metadata.xml | grep '<version>' | sed -e 's/.*<version>//' -e 's/<\/version>.*//' | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -V)

# Find the latest version for each major version
latest_v6=$(echo "$versions" | grep '^6\.' | tail -n 1)
latest_v5=$(echo "$versions" | grep '^5\.' | tail -n 1)
latest_v4=$(echo "$versions" | grep '^4\.' | tail -n 1)
latest_v3=$(echo "$versions" | grep '^3\.' | tail -n 1)

# Update the README.adoc file
sed -i "s/:version-6: .*/:version-6: $latest_v6/" README.adoc
sed -i "s/:version-5: .*/:version-5: $latest_v5/" README.adoc
sed -i "s/:version-4: .*/:version-4: $latest_v4/" README.adoc
sed -i "s/:version-3: .*/:version-3: $latest_v3/" README.adoc

echo "README.adoc updated with the latest versions:"
echo "version-6: $latest_v6"
echo "version-5: $latest_v5"
echo "version-4: $latest_v4"
echo "version-3: $latest_v3"
