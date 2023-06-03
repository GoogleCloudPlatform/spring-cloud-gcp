#!/bin/bash
set -e

# If not provided, parse libraries-bom version from spring cloud gcp BOM
if [[ -z "$LIBRARIES_BOM_VERSION" ]]; then
  LIBRARIES_BOM_VERSION=$(xmllint --xpath "string(//*[local-name()='gcp-libraries-bom.version'])" spring-cloud-gcp-dependencies/pom.xml
fi

# If not provided, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi

function reset_previews_folder() {
  # cleans the spring-cloud-previews folder and replaces its contents with an
  # empty setup (i.e. empty readme, no libraries, pom without modules)
  cd ${SPRING_GENERATOR_DIR}
  rm -rdf ../spring-cloud-previews
  cp -r spring-cloud-previews-template ../
  mv ../spring-cloud-previews-template ../spring-cloud-previews
}

function compute_monorepo_tag() {
  # Expected argument: $1 = Libraries BOM version
  # From libraries bom version, outputs corresponding google-cloud-java monorepo version
  libraries_bom_version=$1
  gapic_libraries_groupId='com.google.cloud'
  gapic_libraries_artifactId='gapic-libraries-bom'
  curl -s "https://raw.githubusercontent.com/googleapis/java-cloud-bom/v$libraries_bom_version/google-cloud-bom/pom.xml" > libraries-bom-pom
  monorepo_version=$(xmllint --xpath "string(//*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId']='$gapic_libraries_groupId'][*[local-name()='artifactId']='$gapic_libraries_artifactId']/*[local-name()='version'])" libraries-bom-pom)
  rm libraries-bom-pom
  echo $monorepo_version
}

function generate_libraries_list(){
  # Expected argument: $1 = Monorepo version tag (or committish)
  bash ${SPRING_GENERATOR_DIR}/scripts/generate-libraries-list.sh $1
}

reset_previews_folder
MONOREPO_TAG=$(compute_monorepo_tag ${LIBRARIES_BOM_VERSION})
generate_libraries_list ${MONOREPO_TAG}

