#!/bin/bash
# helper script that cleans the changes made by a generate-all.sh call
git checkout HEAD -- ../spring-cloud-gcp-starters
git checkout HEAD -- ../spring-cloud-previews
rm -rdf ../spring-cloud-previews/google*
rm -rdf gapic-generator-java googleapis
