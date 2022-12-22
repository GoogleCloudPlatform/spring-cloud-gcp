#!/bin/bash
git checkout HEAD -- ../spring-cloud-gcp-starters
git checkout HEAD -- ../spring-cloud-previews
rm -rdf ../spring-cloud-previews/google*
rm -rdf gapic-generator-java googleapis
