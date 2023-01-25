#!/bin/bash
libraries_bom_version=$(xmllint --xpath "string(//*[local-name()='gcp-libraries-bom.version'])" ../spring-cloud-gcp-dependencies/pom.xml)
gapic_libraries_groupId='com.google.cloud'
gapic_libraries_artifactId='gapic-libraries-bom'
curl -s "https://raw.githubusercontent.com/googleapis/java-cloud-bom/libraries-bom-v$libraries_bom_version/google-cloud-bom/pom.xml" > libraries-bom-pom
monorepo_version=$(xmllint --xpath "string(//*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId']='$gapic_libraries_groupId'][*[local-name()='artifactId']='$gapic_libraries_artifactId']/*[local-name()='version'])" libraries-bom-pom)
rm libraries-bom-pom
echo $monorepo_version
