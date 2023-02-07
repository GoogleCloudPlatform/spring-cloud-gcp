#!/bin/bash
WORKING_DIR=`pwd`

function fail() {
  echo "sanity check failed at $1"
  #exit 1
}

# checks that library list was generated and not empty
if [[ $(cat library_list.txt | wc -l) -lt 2 ]]; then fail "library list length"; fi

# checks that the contents of each entry in the library list is a string with
# length >= 1
libraries=$(cat $WORKING_DIR/library_list.txt | tail -n+2)
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
      fail "'library list column content check'"
    fi
  done
done <<< $libraries
