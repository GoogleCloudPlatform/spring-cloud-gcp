#!/bin/bash

# Cleans the spring-cloud-previews folder and replaces its contents with an
# empty setup (i.e. empty readme, no libraries, pom without modules)
function reset_previews_folder() {
  cd ${SPRING_GENERATOR_DIR}
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
#
# $1 - Monorepo version tag (or committish)
function generate_libraries_list(){
  cd ${SPRING_GENERATOR_DIR}
  bash scripts/generate-library-list.sh $1
}

# When bazel prepare, build, or post-processing step fails, stores the captured stdout and stderr to a file
# with the name of the failed step (and client library name, if applicable)
#
# $1 - failed step identifier (e.g. bazel_build_all, bazel_prepare_accessapproval)
function save_error_info () {
  mkdir -p ${SPRING_GENERATOR_DIR}/failed-library-generations
  cp tmp-output ${SPRING_GENERATOR_DIR}/failed-library-generations/$1
}

# Install local snapshot jar for spring generator
function install_spring_generator(){
  cd ${SPRING_GENERATOR_DIR} && mvn clean install
}

# Clone googleapis repository, and makes bazel workspace modifications required for generation
function setup_googleapis(){
  git clone https://github.com/googleapis/googleapis.git
  cd googleapis
  # Add local_repository rule with name "spring_cloud_generator"
  buildozer 'new local_repository spring_cloud_generator before gapic_generator_java' WORKSPACE:__pkg__
  # Point path to local repo
  buildozer 'set path "../../spring-cloud-generator"' WORKSPACE:spring_cloud_generator
  # Delete existing maven_install rules
  buildozer 'delete' WORKSPACE:%maven_install
  # Add custom maven_install rules
  perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{\$1\n$(cat ../scripts/resources/googleapis_modification_string.txt)}" WORKSPACE
  # In repository_rules.bzl, add switch for new spring rule
  JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@spring_cloud_generator\/\/:java_gapic_spring.bzl\\\",\n    )"
  perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl
}

# For individual library, checkout versioned protos folder and set up bazel rules
#
# $1 - googleapis commitish
# $2 - googleapis folder (e.g. google/cloud/accessapproval/v1)
function prepare_bazel_build(){
  GOOGLEAPIS_COMMITISH=$1
  GOOGLEAPIS_FOLDER=$2

  # If googleapis folder does not exist, exit
  if [ ! -d "$(pwd)/${GOOGLEAPIS_FOLDER}" ]
  then
    echo "Directory $(pwd)/${GOOGLEAPIS_FOLDER} does not exist."
    exit
  fi

  git checkout ${GOOGLEAPIS_COMMITISH} -- ${GOOGLEAPIS_FOLDER}

  # Modify BUILD.bazel file for library
  # Additional rule to load
  SPRING_RULE_NAME="    \\\"java_gapic_spring_library\\\","
  perl -0777 -pi -e "s/(load\((.*?)\"java_gapic_library\",)/\$1\n${SPRING_RULE_NAME}/s" ${GOOGLEAPIS_FOLDER}/BUILD.bazel

  # Add java_gapic_spring_library rule, with attrs copied from corresponding java_gapic_library rule
  GAPIC_RULE_NAME="$(buildozer 'print name' ${GOOGLEAPIS_FOLDER}/BUILD.bazel:%java_gapic_library)"
  SPRING_RULE_NAME="${GAPIC_RULE_NAME}_spring"
  GAPIC_RULE_FULL="$(buildozer 'print rule' ${GOOGLEAPIS_FOLDER}/BUILD.bazel:%java_gapic_library)"

  buildozer "new java_gapic_spring_library $SPRING_RULE_NAME" ${GOOGLEAPIS_FOLDER}/BUILD.bazel:__pkg__

  # Copy attributes from java_gapic_library rule
  attrs_array=("srcs" "grpc_service_config" "gapic_yaml" "service_yaml" "transport")
  for attribute in "${attrs_array[@]}"
    do
      echo "$attribute"
      if [[ $GAPIC_RULE_FULL = *"$attribute"* ]] ; then
              buildozer "copy $attribute $GAPIC_RULE_NAME" ${GOOGLEAPIS_FOLDER}/BUILD.bazel:$SPRING_RULE_NAME
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
function add_module_to_pom () {
  xmllint --debug --nsclean --xpath  "//*[local-name()='module']/text()" $1 \
    | sort | uniq | grep -q $2 || module_list_is_empty=1
  found_library_in_pom=$?
  if [[ found_library_in_pom -eq 0 ]] && [[ $module_list_is_empty -ne 1 ]]; then
    echo "module $2 already found in $1 modules"
  else
    echo "adding module $2 to pom"
    sed -i "/^[[:space:]]*<modules>/a\ \ \ \ <module>"$2"</module>" $1
  fi
}

# Add line to spring-cloud-previews/README.md,
# containing the table of available starters and corresponding libraries
#
# $1 - monorepo folder (e.g. java-accessapproval)
# $2 - monorepo-commitish (e.g. v1.13.0)
# $3 - starter-artifact-id (e.g. google-cloud-accessapproval-spring-starter)
function add_line_to_readme() {
    # check for existence and write line to spring-cloud-previews/README.md
    # format |client library name|starter maven artifact|
    echo -e "|[$1](https://github.com/googleapis/google-cloud-java/blob/$2/$1/README.md)|com.google.cloud:$3|" >> README.md
    {(grep -vw "|.*:.*|" README.md);(grep "|.*:.*|" README.md| sort | uniq)} > tmpfile && mv tmpfile README.md
}

# Perform post-processing steps for one library's generated starter module:
#    Copies and unzips generated code from bazel output
#    Substitutes values into generated starter pom.xml
#    Add module to aggregator pom (add_module_to_pom)
#    Add row to README table (add_line_to_readme)
#
# $1 - client library artifact id (e.g. google-cloud-accessapproval)
# $2 - client library group id (e.g. com.google.cloud)
# $3 - parent (spring-cloud-gcp-starters) version  (e.g. 4.5.0)
# $4 - googleapis folder (e.g. google/cloud/accessapproval/v1)
# $5 - monorepo folder (e.g. java-accessapproval)
# $6 - googleapis commitish
# $7 - monorepo commitish (e.g. v1.13.0)
function postprocess_library() {
  client_lib_artifactid=$1
  client_lib_groupid=$2
  parent_version=$3
  googleapis_folder=$4
  monorepo_folder=$5
  googleapis_commitish=$6
  monorepo_commitish=$7
  starter_artifactid="$client_lib_artifactid-spring-starter"

  cd ${SPRING_GENERATOR_DIR}

  # sometimes the rule name doesnt have the same prefix as $client_lib_name
  # we use this perl command capture the correct prefix
  rule_prefix=$(cat googleapis/$googleapis_folder/BUILD.bazel | grep _java_gapic_spring | perl -lane 'print m/"(.*)_java_gapic_spring/')

  # copy generated code and unzip
  cp googleapis/bazel-bin/$googleapis_folder/"$rule_prefix"_java_gapic_spring-spring.srcjar ../spring-cloud-previews
  cd ../spring-cloud-previews
  unzip -o "$rule_prefix"_java_gapic_spring-spring.srcjar -d "$starter_artifactid"/
  rm -rf "$rule_prefix"_java_gapic_spring-spring.srcjar

  # modifications to pom.xml
  sed -i 's/{{client-library-group-id}}/'"$client_lib_groupid"'/' "$starter_artifactid"/pom.xml
  sed -i 's/{{client-library-artifact-id}}/'"$client_lib_artifactid"'/' "$starter_artifactid"/pom.xml
  sed -i 's/{{parent-version}}/'"$parent_version"'/' "$starter_artifactid"/pom.xml

  add_module_to_pom pom.xml $starter_artifactid
  add_line_to_readme $monorepo_folder $monorepo_commitish $starter_artifactid
}


# Run auto-formatter on generated code
#
# $1 - location to run formatter from (e.g. spring-cloud-previews)
function run_formatter(){
  cd $1
  ./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false
}