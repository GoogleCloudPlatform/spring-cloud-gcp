#!/bin/bash

# This script updates the version numbers in README.adoc to the latest
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
sed -i "/Spring Framework on Google Cloud 6\./s/[0-9]*\.[0-9]*\.[0-9]*/${latest_v6}/g" README.adoc
sed -i "/Spring Framework on Google Cloud 5\./s/[0-9]*\.[0-9]*\.[0-9]*/${latest_v5}/g" README.adoc
sed -i "/Spring Framework on Google Cloud 4\./s/[0-9]*\.[0-9]*\.[0-9]*/${latest_v4}/g" README.adoc
sed -i "/Spring Framework on Google Cloud 3\./s/[0-9]*\.[0-9]*\.[0-9]*/${latest_v3}/g" README.adoc

echo "README.adoc updated with the latest versions:"
echo "version-6: $latest_v6"
echo "version-5: $latest_v5"
echo "version-4: $latest_v4"
echo "version-3: $latest_v3"
