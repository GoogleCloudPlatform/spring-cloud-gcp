#!/bin/bash
# This script performs the following sanity checks for the library generation
# workflow (runs in the workflow, after generation):
#
# - checks that the library list used as a source of truth for the generated
# starters is not empty
# - checks that each element of the library list is a non-empty-string
# - checks that the number of rows in the library list is equal to the number of
# autoconfig folders in spring-cloud-previews
# - checks the contents of every stater and confirms they have some basic
# required files such as pom.xml, [Client]AutoConfiguration.java, etc
#
WORKING_DIR=`pwd`
set -xe # TODO remove x

function fail() {
  echo "sanity check failed:"
  echo "  $1"
  exit 1
}
library_list_path=$WORKING_DIR/resources/library_list.txt
# checks that library list was generated and not empty
if [[ $(cat $library_list_path | wc -l) -lt 2 ]]; then fail "library list is empty"; fi

# checks that the contents of each entry in the library list is a string with
# length >= 1
libraries=$(cat $library_list_path | tail -n+2)
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do

  non_empty_check_items=(
    "$library_name"
    "$googleapis_location"
    "$coordinates_version"
    "$googleapis_commitish"
    "$monorepo_folder"
  )
  for column in "${non_empty_check_items[@]}"; do
    if [[ -z $column ]]; then
      echo "$library_name, $googleapis_location, $coordinates_version, $googleapis_commitish, $monorepo_folder"
      fail "the library list entry '$library_name' has an empty required cell - see $library_list_path"
    fi
  done
done <<< $libraries

starters=$(find ../../spring-cloud-previews -maxdepth 1 -name "google-*" -type d -printf "%p\n")
# confirms library_list and generated folders have the same length
lib_list_n_entries=$(cat $library_list_path | tail -n+2 | wc -l)
gen_folders_n_entries=$(printf "$starters" | cut -d' ' -f1- | wc -l)
if [[ $lib_list_n_entries -ne $gen_folders_n_entries ]]; then
  fail "entries in library list and generated folders differ"
fi

# checks the existence of a pom, a *AutoConfiguration.java, *Properties.java in
# each of the generated libraries
while IFS=' ' read -r starter_folder_raw; do

  starter_folder=$(realpath "$starter_folder_raw")
  starter_name=$(basename $starter_folder)
  base_error="generated file check for starter $starter_name"

  # check existence of pom
  if [[ ! -f $starter_folder/pom.xml ]]; then
    fail "$base_error: pom.xml not found"
  fi

  # checks for at least 1 autoconfig file
  if [[ $(find $starter_folder -name "*AutoConfiguration.java" | wc -l) -lt 1 ]]; then
    fail "$base_error: no *AutoConfiguration.java files found"
  fi

  # checks for at least 1 properties file
  if [[ $(find $starter_folder -name "*Properties.java" | wc -l) -lt 1 ]]; then
    fail "$base_error: no *Properties.java files found"
  fi

  # checks for configuration metadata resource
  if [[ $(find $starter_folder -name "additional-spring-configuration-metadata.json" | wc -l) -lt 1 ]]; then
    fail "$base_error: no additional-spring-configuration-metadata.json found"
  fi

  # checks for AutoConfiguration imports file
  if [[ $(find $starter_folder -name "org.springframework.boot.autoconfigure.AutoConfiguration.imports" | wc -l) -lt 1 ]]; then
    fail "$base_error: no org.springframework.boot.autoconfigure.AutoConfiguration.imports file found"
  fi

done <<< $starters


echo "sanity check OK"
