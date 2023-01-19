#!/bin/bash
sanity_check_file_ids=(
  "generate-all"
  "generate-library-list"
  "compute-monorepo-tag"
)

check_file_exists() {
  if [ -e "$1" ]; then
    echo "Sanity check file OK: $1"
  else
    echo "Missing sanity check file: $1"
    exit 1
  fi
}

for file in "${sanity_check_file_ids[@]}"; do
  check_file_exists "run-sanity-check/$file-started"
  check_file_exists "run-sanity-check/$file-finished"
done
