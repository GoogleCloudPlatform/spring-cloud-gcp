set -ex

# To VERIFY: ./scripts/generate-showcase.sh
# To UPDATE: /scripts/generate-showcase.sh -u
UPDATE=0
while getopts u flag
do
    case "${flag}" in
        u) UPDATE=1;;
    esac
done

# For reusing bazel setup modifications and post-processing steps
source ./scripts/generate-steps.sh

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..
SHOWCASE_STARTER_OLD_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter
SHOWCASE_STARTER_NEW_DIR=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter-generated

# Verifies newly generated showcase-spring-starter against goldens
#
# $1 - directory containing existing showcase-spring-starter (golden)
# $2 - directory containing newly generated showcase-spring-starter
function verify(){
  OLD_DIR=$1
  NEW_DIR=$2
  SHOWCASE_STARTER_DIFF=$(diff -r ${NEW_DIR}/src/main ${OLD_DIR}/src/main)
  SHOWCASE_STARTER_POM_DIFF=$(diff -r ${NEW_DIR}/pom.xml ${OLD_DIR}/pom.xml)
  if [ "$SHOWCASE_STARTER_DIFF" != "" ] || [ "$SHOWCASE_STARTER_POM_DIFF" != "" ]
  then
      echo "Differences detected in generated showcase starter module: "
      echo "Diff from src/main: "
      echo $SHOWCASE_STARTER_DIFF
      echo "Diff from pom.xml: "
      echo $SHOWCASE_STARTER_POM_DIFF
      exit 1;
  else
      echo "No differences found in showcase-spring-starter"
      rm -r ${NEW_DIR}
  fi
}

# Setup, generation, and post-processing steps for showcase-spring-starter
#
# $1 - target directory for generated starter
function generate_showcase_spring_starter(){
  SHOWCASE_STARTER_DIR=$1

  # Compute the parent project version.
  cd ${SPRING_ROOT_DIR}
  export PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  cd ${SPRING_GENERATOR_DIR}
  GAPIC_GENERATOR_JAVA_VERSION=$(mvn help:evaluate -Dexpression=gapic-generator-java-bom.version -q -DforceStdout)

  if [[ -z "$GAPIC_GENERATOR_JAVA_VERSION" ]]; then
    echo "Missing sdk-platform-java commitish to checkout"
    exit 1
  fi

  # Clone sdk-platform-java (with showcase library)
  if [[ ! -d "./sdk-platform-java" ]]; then
    git clone https://github.com/googleapis/sdk-platform-java.git
  fi
  cd sdk-platform-java && git checkout "v${GAPIC_GENERATOR_JAVA_VERSION}"

  # We will use the generation tools from library_generation
  pushd library_generation/utils
  source utilities.sh
  popd #library_generation/utils

  # Install showcase client libraries locally
  pushd showcase
  # For local development, we cleanup any traces of previous runs
  rm -rdf output
  # mvn clean install
  GAPIC_SHOWCASE_CLIENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

  pushd gapic-showcase
  GAPIC_SHOWCASE_SERVER_VERSION=$(mvn help:evaluate -Dexpression=gapic-showcase.version -q -DforceStdout)
  popd #showcase/gapic-showcase

  # Alternative: if showcase client library is available on Maven Central,
  # Instead of downloading sdk-platform-java/showcase (for client library, and generation setup),
  # Can instead download googleapis (for generation setup) and gapic-showcase (for protos)

  output_folder=$(get_output_folder)
  mkdir "${output_folder}"
  pushd "${output_folder}"
  protoc_version=$(get_protoc_version "${GAPIC_GENERATOR_JAVA_VERSION}")
  os_architecture=$(detect_os_architecture)
  download_protoc "${protoc_version}" "${os_architecture}"

  # We now copy the spring-cloud-generator and gapic-generator-java jar into the output_folder the
  # sdk-platform-java generation scripts work with.
  spring_generator_jar_name="spring-cloud-generator-${PROJECT_VERSION}-jar-with-dependencies.jar"
  cp ~/.m2/repository/com/google/cloud/spring-cloud-generator/"${PROJECT_VERSION}/${spring_generator_jar_name}" \
    "${output_folder}"
  chmod 555 ${output_folder}/*.jar

  # We download gapic-showcase and prepare the protos in output_folder
  sparse_clone https://github.com/googleapis/gapic-showcase.git "schema/google/showcase/v1beta1"
  pushd gapic-showcase
  git checkout "v${GAPIC_SHOWCASE_SERVER_VERSION}"
  cp -r schema "${output_folder}"
  popd #gapic-showcase

  # We download googleapis and prepare the protos in output_folder
  if [[ ! -d "./googleapis" ]]; then
    sparse_clone https://github.com/googleapis/googleapis.git "google/iam/v1 google/cloud/location google/api google/longrunning google/rpc google/type"
  fi
  pushd googleapis
  cp -r google "${output_folder}"
  popd #gapic-showcase

  # Now we call protoc with a series of arguments we obtain from
  # sdk-platform-java's utilities.sh and others that are hardcoded (and stable).
  # Note that --java_gapic_spring_opt uses `get_gapic_opts` which will work
  # since the BUILD rules take similar arguments

  proto_path="schema/google/showcase/v1beta1"
  proto_files=$(find "${proto_path}" -type f  -name "*.proto" | LC_COLLATE=C sort)
  gapic_additional_protos="google/iam/v1/iam_policy.proto google/cloud/location/locations.proto"
  rest_numeric_enums="false"
  transport="grpc+rest"
  gapic_yaml=""
  service_config="schema/google/showcase/v1beta1/showcase_grpc_service_config.json"
  service_yaml="schema/google/showcase/v1beta1/showcase_v1beta1.yaml"
  include_samples="false"

  "${protoc_path}"/protoc \
    "--experimental_allow_proto3_optional" \
    "--plugin=protoc-gen-java_gapic_spring=${SPRING_GENERATOR_DIR}/spring-cloud-generator-wrapper" \
    "--java_gapic_spring_out=${output_folder}/showcase_java_gapic_spring_raw.srcjar.zip" \
    "--java_gapic_spring_opt=$(get_gapic_opts  "${transport}" "${rest_numeric_enums}" "${gapic_yaml}" "${service_config}" "${service_yaml}")" \
    ${proto_files} ${gapic_additional_protos}

  popd #output_folder



  # Post-process generated modules
  copy_and_unzip "${output_folder}/showcase_java_gapic_spring-spring.srcjar" "showcase_java_gapic_spring-spring.srcjar" "${SPRING_GENERATOR_DIR}/showcase" ${SHOWCASE_STARTER_DIR}
  modify_starter_pom ${SHOWCASE_STARTER_DIR}/pom.xml "com.google.cloud" "gapic-showcase" $PROJECT_VERSION

  # Additional pom.xml modifications for showcase starter
  # Add explicit gapic-showcase version
  sed -i'' '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>'"$GAPIC_SHOWCASE_CLIENT_VERSION"'</version>' ${SHOWCASE_STARTER_DIR}/pom.xml
  # Update relative path to parent pom (different repo structure from starters)
  RELATIVE_PATH="\ \ \ \ <relativePath>..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
  sed -i'' 's/^ *<relativePath>.*/'"$RELATIVE_PATH"'/g' ${SHOWCASE_STARTER_DIR}/pom.xml

  # Run google-java-format on generated code
  run_formatter ${SHOWCASE_STARTER_DIR}

  # Remove downloaded repos
  rm -rdf ${SPRING_GENERATOR_DIR}/sdk-platform-java
  rm -rdf gapic-showcase
  popd #showcase
}

if [[ UPDATE -ne 0 ]]; then
    echo "Running script to perform showcase-spring-starter update"
    generate_showcase_spring_starter ${SHOWCASE_STARTER_OLD_DIR}
  else
    echo "Running script to perform showcase-spring-starter verification"
    generate_showcase_spring_starter ${SHOWCASE_STARTER_NEW_DIR}
    verify ${SHOWCASE_STARTER_OLD_DIR} ${SHOWCASE_STARTER_NEW_DIR}
fi
