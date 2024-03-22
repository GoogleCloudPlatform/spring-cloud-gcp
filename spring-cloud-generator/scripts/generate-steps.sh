#!/bin/bash

# This script contains helper functions for the generation process (generate.sh),
# including setup and post-processing steps
# Some helpers are also used by the generation process for showcase testing (generate-showcase.sh)

# Cleans the spring-cloud-previews folder and replaces its contents with an
# empty setup (i.e. empty readme, no libraries, pom without modules)
# Assumes current working directory is spring-cloud-generator
function reset_previews_folder() {
  rm -rdf ../spring-cloud-previews
  cp -r spring-cloud-previews-template ../
  mv ../spring-cloud-previews-template ../spring-cloud-previews
}

# From libraries BOM version, outputs corresponding google-cloud-java monorepo version (e.g. 1.13.0)
#
# $1 - Libraries BOM version (e.g. 26.17.0)
function compute_monorepo_version() {
  libraries_bom_version=$1
  gapic_libraries_groupId='com.google.cloud'
  gapic_libraries_artifactId='gapic-libraries-bom'
  curl -s "https://raw.githubusercontent.com/googleapis/java-cloud-bom/v$libraries_bom_version/google-cloud-bom/pom.xml" > libraries-bom-pom
  monorepo_version=$(xmllint --xpath "string(//*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId']='$gapic_libraries_groupId'][*[local-name()='artifactId']='$gapic_libraries_artifactId']/*[local-name()='version'])" libraries-bom-pom)
  rm libraries-bom-pom
  echo $monorepo_version
}

# Generate list of in-scope libraries to use (as static data source to generate modules from)
# Uses heuristic approach to parse googleapis commitish and other metadata from google-cloud-java
# See generate-library-list.sh and https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/1390 for details
# Assumes current working directory is spring-cloud-generator
#
# $1 - Monorepo version tag (or committish)
function generate_libraries_list(){
  monorepo_commitish=$1
  git clone https://github.com/googleapis/google-cloud-java.git
  pushd google-cloud-java || { echo "Failure: google-cloud-java folder does not exists."; exit 1; }
  # read googleapis committish used in hermetic build
  GOOGLEAPIS_COMMITTISH=$(yq -r ".googleapis_commitish" generation_config.yaml)
  popd || { echo "Failure in popd."; exit 1; }

  bash scripts/generate-library-list.sh -c $monorepo_commitish
  rm -rf google-cloud-java/
}

# When bazel prepare, build, or post-processing step fails, stores the captured stdout and stderr to a file
# with the name of the failed step (and client library name, if applicable)
#
# $1 - parent directory under which to create failed-library-generations directory
# $2 - failed step identifier (e.g. bazel_build_all, bazel_prepare_accessapproval)
function save_error_info () {
  parent_directory=$1
  step_identifier=$2
  mkdir -p ${parent_directory}/failed-library-generations
  cp tmp-output ${parent_directory}/failed-library-generations/${step_identifier}
}

# Clone googleapis repository, checkout commitish
# and makes bazel workspace modifications required for generation
#
# $1 - googleapis commitish
function setup_googleapis(){
  googleapis_commitish=$1
  git clone https://github.com/googleapis/googleapis.git
  cd googleapis
  git checkout ${googleapis_commitish}
  modify_workspace_file "WORKSPACE" "../../spring-cloud-generator" "../scripts/resources/googleapis_modification_string.txt"
  # In repository_rules.bzl, add switch for new spring rule
  JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@spring_cloud_generator\/\/:java_gapic_spring.bzl\\\",\n    )"
  perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl
}

# Parameterized function that handles modifications to the bazel WORKSPACE file,
# Used by both generate.sh and generate-showcase.sh
#
# $1 - path to WORKSPACE file
# $2 - path (e.g. relative from WORKSPACE file) to generator, for use in local_repository rule
# $3 - path (e.g. relative from WORKSPACE file) to googleapis_modification_string.txt
function modify_workspace_file(){
  path_to_workspace=$1
  path_to_generator=$2
  path_to_modification_string=$3
  # add local_repository rule with name "spring_cloud_generator"
  buildozer 'new local_repository spring_cloud_generator before gapic_generator_java' ${path_to_workspace}:__pkg__
  # point path to local repo
  buildozer "set path ${path_to_generator}" ${path_to_workspace}:spring_cloud_generator
  # delete existing maven_install rules
  buildozer 'delete' ${path_to_workspace}:%maven_install
  # add custom maven_install rules
  perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{\$1\n$(cat ${path_to_modification_string})}" ${path_to_workspace}
}

# For individual library, set up bazel rules
#
# $1 - googleapis folder (e.g. google/cloud/accessapproval/v1)
function prepare_bazel_build(){
  googleapis_folder=$1

  # If googleapis folder does not exist, exit
  if [ ! -d "$(pwd)/${googleapis_folder}" ]
  then
    echo "Directory $(pwd)/${googleapis_folder} does not exist."
    exit
  fi

  # Modify BUILD.bazel file for library
  # Additional rule to load
  SPRING_RULE_NAME="    \\\"java_gapic_spring_library\\\","
  perl -0777 -pi -e "s/(load\((.*?)\"java_gapic_library\",)/\$1\n${SPRING_RULE_NAME}/s" ${googleapis_folder}/BUILD.bazel

  modify_build_file ${googleapis_folder}/BUILD.bazel
}

# Parameterized function that handles modifications to the BUILD.bazel file,
# Used by both generate.sh and generate-showcase.sh
#
# $1 - path to BUILD.bazel
function modify_build_file(){
  path_to_build_file=$1

  # Add java_gapic_spring_library rule, with attrs copied from corresponding java_gapic_library rule
  GAPIC_RULE_NAME="$(buildozer 'print name' ${path_to_build_file}:%java_gapic_library)"
  SPRING_RULE_NAME="${GAPIC_RULE_NAME}_spring"
  GAPIC_RULE_FULL="$(buildozer 'print rule' ${path_to_build_file}:%java_gapic_library)"

  buildozer "new java_gapic_spring_library $SPRING_RULE_NAME" ${path_to_build_file}:__pkg__

  # Copy attributes from java_gapic_library rule
  attrs_array=("srcs" "grpc_service_config" "gapic_yaml" "service_yaml" "transport")
  for attribute in "${attrs_array[@]}"
    do
      echo "$attribute"
      if [[ $GAPIC_RULE_FULL = *"$attribute"* ]] ; then
              buildozer "copy $attribute $GAPIC_RULE_NAME" ${path_to_build_file}:$SPRING_RULE_NAME
          else
              echo "attribute $attribute not found in java_gapic_library rule, skipping"
      fi
    done
}

# Fetch all `*java_gapic_spring` build rules and build them at once
function bazel_build_all(){
  bazelisk query "attr(name, '.*java_gapic_spring', //...)" \
    | xargs bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 2>&1 \
    | tee tmp-output || save_error_info "bazel_build"
}

# Add module to aggregator POM in the appropriate location, with check for existence.
#
# $1 - path-to-pom-file (e.g. path/to/pom.xml)
# $2 - starter-artifact-id (e.g. google-cloud-accessapproval-spring-starter)
function add_module_to_aggregator_pom() {
  path_to_pom=$1
  starter_artifactid=$2
  xmllint --debug --nsclean --xpath  "//*[local-name()='module']/text()" ${path_to_pom} \
    | sort | uniq | grep -q ${starter_artifactid} || module_list_is_empty=1
  found_library_in_pom=$?
  if [[ found_library_in_pom -eq 0 ]] && [[ $module_list_is_empty -ne 1 ]]; then
    echo "module ${starter_artifactid} already found in ${path_to_pom} modules"
  else
    echo "adding module ${starter_artifactid} to pom"
    sed -i'' "/^[[:space:]]*<modules>/a\ \ \ \ <module>"${starter_artifactid}"</module>" ${path_to_pom}
  fi
}

# Add line to spring-cloud-previews/README.md,
# containing the table of available starters and corresponding libraries
#
# $1 - monorepo folder (e.g. java-accessapproval)
# $2 - monorepo-commitish (e.g. v1.13.0)
# $3 - starter-artifact-id (e.g. google-cloud-accessapproval-spring-starter)
function add_line_to_readme() {
  monorepo_folder=$1
  monorepo_commitish=$2
  starter_artifactid=$3
  # check for existence and write line to spring-cloud-previews/README.md
  # format |client library name|starter maven artifact|
  echo -e "|[${monorepo_folder}](https://github.com/googleapis/google-cloud-java/blob/${monorepo_commitish}/${monorepo_folder}/README.md)|com.google.cloud:${starter_artifactid}|" >> README.md
  {(grep -vw "|.*:.*|" README.md);(grep "|.*:.*|" README.md| sort | uniq)} > tmpfile && mv tmpfile README.md
}

# Perform post-processing steps for one library's generated starter module:
#    Copies and unzips generated code from bazel output
#    Substitutes values into generated starter pom.xml
#    Add module to aggregator pom (add_module_to_pom)
#    Add row to README table (add_line_to_readme)
# Assumes current working directory is spring-cloud-generator
#
# $1 - client library artifact id (e.g. google-cloud-accessapproval)
# $2 - client library group id (e.g. com.google.cloud)
# $3 - parent (spring-cloud-gcp-starters) version  (e.g. 4.5.0)
# $4 - googleapis folder (e.g. google/cloud/accessapproval/v1)
# $5 - monorepo folder (e.g. java-accessapproval)
# $6 - monorepo commitish (e.g. v1.13.0)
function postprocess_library() {
  client_lib_artifactid=$1
  client_lib_groupid=$2
  parent_version=$3
  googleapis_folder=$4
  monorepo_folder=$5
  monorepo_commitish=$6
  starter_artifactid="${client_lib_artifactid}-spring-starter"

  # sometimes the rule name doesnt have the same prefix as $client_lib_name
  # we use this perl command capture the correct prefix
  rule_prefix=$(cat googleapis/$googleapis_folder/BUILD.bazel | grep _java_gapic_spring | perl -lane 'print m/"(.*)_java_gapic_spring/')

  # copy generated code and unzip
  pushd googleapis
  bazel_bin_location=$(bazelisk info bazel-bin)
  popd
  cp ${bazel_bin_location}/${googleapis_folder}/"${rule_prefix}"_java_gapic_spring-spring.srcjar ../spring-cloud-previews
  cd ../spring-cloud-previews
  unzip -o "$rule_prefix"_java_gapic_spring-spring.srcjar -d "$starter_artifactid"/
  rm -rf "$rule_prefix"_java_gapic_spring-spring.srcjar

  modify_starter_pom "$starter_artifactid"/pom.xml $client_lib_groupid $client_lib_artifactid $parent_version
  add_module_to_aggregator_pom pom.xml $starter_artifactid
  add_line_to_readme $monorepo_folder $monorepo_commitish $starter_artifactid
}

# Parameterized function that unzips _java_gapic_spring-spring.srcjar
# and copies generated spring code to a module outside
# Used by both generate.sh and generate-showcase.sh
#
# $1 - path to srcjar
# $2 - name of srcjar
# $3 - path to directory containing all generated modules
# $4 - name of generated module
function copy_and_unzip() {
  path_to_srcjar=$1
  srcjar_name=$2
  path_to_destination=$3
  module_name=$4

  cp ${path_to_srcjar} ${path_to_destination}
  cd ${path_to_destination}
  unzip -o ${srcjar_name} -d ${module_name}/
  rm -rf ${srcjar_name}
}

# Parameterized function that handles post-processing of generated starter pom.xml,
# Used by both generate.sh and generate-showcase.sh
#
# $1 - path to pom.xml
# $2 - client_lib_groupid
# $3 - client_lib_artifactid
# $4 - parent_version
function modify_starter_pom() {
  path_to_pom=$1
  client_lib_groupid=$2
  client_lib_artifactid=$3
  parent_version=$4
  # modifications to pom.xml
  sed -i'' 's/{{client-library-group-id}}/'"$client_lib_groupid"'/' ${path_to_pom}
  sed -i'' 's/{{client-library-artifact-id}}/'"$client_lib_artifactid"'/' ${path_to_pom}
  sed -i'' 's/{{parent-version}}/'"$parent_version"'/' ${path_to_pom}
}

# Run auto-formatter on generated code
#
# $1 - location to run formatter from (e.g. spring-cloud-previews)
function run_formatter(){
  from_directory=$1
  cd $from_directory
  mvn com.coveo:fmt-maven-plugin:format -Dfmt.skip=false
}
