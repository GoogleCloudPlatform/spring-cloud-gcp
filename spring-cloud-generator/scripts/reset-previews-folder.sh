#!/bin/bash
# cleans the spring-cloud-previews folder and replaces its contents with an
# empty setup (i.e. empty readme, no libraries, pom without modules)
# assumes pwd=spring-cloud-generator (although any folder 1 depth level from
# root may work)
set -e
rm -rdf ../spring-cloud-previews
cp -r spring-cloud-previews-template ../
mv ../spring-cloud-previews-template ../spring-cloud-previews

