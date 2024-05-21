#!/usr/bin/env bash

set -eo pipefail
utilities_script_dir=$(dirname "$(realpath "${BASH_SOURCE[0]}")")

# gets the output folder where all sources and dependencies will be located.
get_output_folder() {
  if [[ $(basename $(pwd)) != "output" ]]; then
    echo "$(pwd)/output" 
  else
    echo $(pwd)
  fi
}

get_protoc_version() {
  local gapic_generator_version=$1
  local protoc_version
  pushd "${output_folder}" > /dev/null
  # get protobuf version from gapic-generator-java-pom-parent/pom.xml
  download_gapic_generator_pom_parent "${gapic_generator_version}"
  protoc_version=$(grep protobuf.version "gapic-generator-java-pom-parent-${gapic_generator_version}.pom" | sed 's/<protobuf\.version>\(.*\)<\/protobuf\.version>/\1/' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | cut -d "." -f2-)
  popd > /dev/null
  echo "${protoc_version}"
}

detect_os_architecture() {
  local os_type
  local os_architecture
  os_type=$(uname -sm)
  case "${os_type}" in
    *"Linux x86_64"*)
      os_architecture="linux-x86_64"
      ;;
    *"Darwin x86_64"*)
      os_architecture="osx-x86_64"
      ;;
    *)
      os_architecture="osx-aarch_64"
      ;;
  esac
  echo "${os_architecture}"
}

download_protoc() {
  local protoc_version=$1
  local os_architecture=$2
  if [ ! -d "protoc-${protoc_version}" ]; then
    # pull proto files and protoc from protobuf repository as maven central
    # doesn't have proto files
    download_from \
    "https://github.com/protocolbuffers/protobuf/releases/download/v${protoc_version}/protoc-${protoc_version}-${os_architecture}.zip" \
    "protoc-${protoc_version}.zip" \
    "GitHub"
    unzip -o -q "protoc-${protoc_version}.zip" -d "protoc-${protoc_version}"
    cp -r "protoc-${protoc_version}/include/google" .
    rm "protoc-${protoc_version}.zip"
  fi

  protoc_path="${output_folder}/protoc-${protoc_version}/bin"
}

# get gapic options from .yaml and .json files from proto_path.
get_gapic_opts() {
  local transport=$1
  local rest_numeric_enums=$2
  local gapic_yaml=$3
  local service_config=$4
  local service_yaml=$5
  if [ "${rest_numeric_enums}" == "true" ]; then
    rest_numeric_enums="rest-numeric-enums"
  else
    rest_numeric_enums=""
  fi
  # If any of the gapic options is empty (default value), try to search for
  # it in proto_path.
  if [[ "${gapic_yaml}" == "" ]]; then
    gapic_yaml=$(find "${proto_path}" -type f -name "*gapic.yaml")
  fi

  if [[ "${service_config}" == "" ]]; then
    service_config=$(find "${proto_path}" -type f -name "*service_config.json")
  fi

  if [[ "${service_yaml}" == "" ]]; then
    service_yaml=$(find "${proto_path}" -maxdepth 1 -type f \( -name "*.yaml" ! -name "*gapic*.yaml" \))
  fi
  echo "transport=${transport},${rest_numeric_enums},grpc-service-config=${service_config},gapic-config=${gapic_yaml},api-service-config=${service_yaml}"
}
