#!/bin/bash

# cleans the spring-cloud-previews folder and replaces its contents with an
# empty setup (i.e. empty readme, no libraries, pom without modules)
function reset_previews_folder() {
  cd ${SPRING_GENERATOR_DIR}
  rm -rdf ../spring-cloud-previews
  cp -r spring-cloud-previews-template ../
  mv ../spring-cloud-previews-template ../spring-cloud-previews
}

# Expected argument: $1 = Libraries BOM version
# From libraries bom version, outputs corresponding google-cloud-java monorepo version
function compute_monorepo_version() {
  libraries_bom_version=$1
  gapic_libraries_groupId='com.google.cloud'
  gapic_libraries_artifactId='gapic-libraries-bom'
  curl -s "https://raw.githubusercontent.com/googleapis/java-cloud-bom/v$libraries_bom_version/google-cloud-bom/pom.xml" > libraries-bom-pom
  monorepo_version=$(xmllint --xpath "string(//*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId']='$gapic_libraries_groupId'][*[local-name()='artifactId']='$gapic_libraries_artifactId']/*[local-name()='version'])" libraries-bom-pom)
  rm libraries-bom-pom
  echo $monorepo_version
}

# Expected argument: $1 = Monorepo version tag (or committish)
function generate_libraries_list(){
  cd ${SPRING_GENERATOR_DIR}
  bash scripts/generate-library-list.sh $1
}

# When bazel prepare, build, or post-processing step fails, stores the captured stdout and stderr to a file
# with the name of the failed step (and client library name, if applicable)
# args 1 - failed step identifier
function save_error_info () {
  mkdir -p ${SPRING_GENERATOR_DIR}/failed-library-generations
  cp tmp-output ${SPRING_GENERATOR_DIR}/failed-library-generations/$1
}

# Install local snapshot jar for spring generator
function install_spring_generator(){
  cd ${SPRING_GENERATOR_DIR} && mvn clean install
}

# Clone googleapis repository and make modifications required for generation
function setup_googleapis(){
  git clone https://github.com/googleapis/googleapis.git
  cd googleapis

  # Make local modifications for generation
  # add local_repository rule with name "spring_cloud_generator"
  buildozer 'new local_repository spring_cloud_generator before gapic_generator_java' WORKSPACE:__pkg__
  # point path to local repo
  buildozer 'set path "../../spring-cloud-generator"' WORKSPACE:spring_cloud_generator
  # delete existing maven_install rules
  buildozer 'delete' WORKSPACE:%maven_install
  # add custom maven_install rules
  perl -pi -e "s{(^_gapic_generator_java_version[^\n]*)}{\$1\n$(cat ../scripts/resources/googleapis_modification_string.txt)}" WORKSPACE
  # In repository_rules.bzl, add switch for new spring rule
  JAVA_SPRING_SWITCH="    rules[\\\"java_gapic_spring_library\\\"] = _switch(\n        java and grpc and gapic,\n        \\\"\@spring_cloud_generator\/\/:java_gapic_spring.bzl\\\",\n    )"
  perl -0777 -pi -e "s/(rules\[\"java_gapic_library\"\] \= _switch\((.*?)\))/\$1\n$JAVA_SPRING_SWITCH/s" repository_rules.bzl
}

# For individual library, checkout versioned protos folder and set up bazel rules
# $1 googleapis commitish
# $2 googleapis folder
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

# Fetches all `*java_gapic_spring` build rules and build them at once
function bazel_build_all(){
  bazelisk query "attr(name, '.*java_gapic_spring', //...)" \
    | xargs bazelisk build --tool_java_language_version=17 --tool_java_runtime_version=remotejdk_17 2>&1 \
    | tee tmp-output || save_error_info "bazel_build"
}

# add module after line with pattern, check for existence.
# args: 1 -  path-to-pom-file; 2 - starter-artifact-id
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

# args: 1 - monorepo-folder, 2 - monorepo-commitish, 3 - starter-artifact-id
function add_line_to_readme() {
    # TODO(emmwang): fix this function
    echo "monorepo folder: $1"
    echo "monorepo commitish: $2"
    echo "starter artifact id: $3"
    # check for existence and write line to spring-cloud-previews/README.md
    # format |client library name|starter maven artifact|
    echo -e "|[$1](https://github.com/googleapis/google-cloud-java/blob/$2/$1/README.md)|com.google.cloud:$3|" >> README.md
    {(grep -vw ".*:.*" README.md);(grep ".*:.*" README.md| sort | uniq)} > tmpfile && mv tmpfile README.md
}

function postprocess_library() {
  client_lib_name=$1
  client_lib_artifactid=$2
  client_lib_groupid=$3
  parent_version=$4
  googleapis_folder=$5
  monorepo_folder=$6
  googleapis_commitish=$7
  monorepo_tag=$8
  starter_artifactid="$client_lib_artifactid-spring-starter"

  echo "monorepo folder: $monorepo_folder"
  echo "monorepo commitish: $monorepo_commitish"
  echo "starter artifact id: $starter_artifactid"

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

# $1 - location of module to run formatter on (e.g. spring-cloud-previews)
function run_formatter(){
  cd $1
  ./../mvnw com.coveo:fmt-maven-plugin:format -Dfmt.skip=false
}