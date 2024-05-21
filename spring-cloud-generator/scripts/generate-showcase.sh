set -ex

# To verify: ./scripts/generate-showcase.sh
# To update: /scripts/generate-showcase.sh -u
update="false"
while getopts u flag
do
    case "${flag}" in
        u) update="true";;
    esac
done

# For reusing bazel setup modifications and post-processing steps
source ./scripts/generate-steps.sh

# If not set, assume working directory is spring-cloud-generator
if [[ -z "${SPRING_GENERATOR_DIR}" ]]; then
  SPRING_GENERATOR_DIR=$(pwd)
fi
spring_root_dir=${SPRING_GENERATOR_DIR}/..
showcase_starter_old_dir=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter
showcase_starter_new_dir=${SPRING_GENERATOR_DIR}/showcase/showcase-spring-starter-generated

# Verifies newly generated showcase-spring-starter against goldens
#
# $1 - directory containing existing showcase-spring-starter (golden)
# $2 - directory containing newly generated showcase-spring-starter
function verify(){
  old_dir=$1
  new_dir=$2
  showcase_starter_diff=$(diff -r ${new_dir}/src/main ${old_dir}/src/main)
  showcase_starter_pom_diff=$(diff -r ${new_dir}/pom.xml ${old_dir}/pom.xml)
  if [ "${showcase_starter_diff}" != "" ] || [ "${showcase_starter_pom_diff}" != "" ]
  then
      echo "Differences detected in generated showcase starter module: "
      echo "Diff from src/main: "
      echo "${showcase_starter_diff}"
      echo "Diff from pom.xml: "
      echo "${showcase_starter_pom_diff}"
      exit 1;
  else
      echo "No differences found in showcase-spring-starter"
      rm -r "${new_dir}"
  fi
}

# Setup, generation, and post-processing steps for showcase-spring-starter
#
# $1 - target directory for generated starter
function generate_showcase_spring_starter(){
  showcase_starter_dir=$1

  # Compute the parent project version.
  pushd "${spring_root_dir}"
  export project_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  cd "${SPRING_GENERATOR_DIR}"
  gapic_generator_java_version=$(mvn help:evaluate -Dexpression=gapic-generator-java-bom.version -q -DforceStdout)

  if [[ -z "${gapic_generator_java_version}" ]]; then
    echo "Missing sdk-platform-java commitish to checkout"
    exit 1
  fi

  # Clone sdk-platform-java (with showcase library)
  if [[ ! -d "./sdk-platform-java" ]]; then
    git clone https://github.com/googleapis/sdk-platform-java.git
  fi
  pushd sdk-platform-java
  git checkout "v${gapic_generator_java_version}"

  # Install showcase client libraries locally
  pushd showcase
  # For local development, we cleanup any traces of previous runs
  rm -rdf output
  mvn clean install
  gapic_showcase_client_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

  pushd gapic-showcase
  gapic_showcase_server_version=$(mvn help:evaluate -Dexpression=gapic-showcase.version -q -DforceStdout)
  popd #showcase/gapic-showcase

  output_folder=$(get_output_folder)
  mkdir "${output_folder}"
  pushd "${output_folder}"
  protoc_version=$(get_protoc_version "${gapic_generator_java_version}")
  os_architecture=$(detect_os_architecture)
  download_protoc "${protoc_version}" "${os_architecture}"

  # We now copy the spring-cloud-generator jar with dependencies
  # into the output_folder the sdk-platform-java generation
  # scripts work with.
  spring_generator_jar_name="spring-cloud-generator-${project_version}-jar-with-dependencies.jar"
  cp ~/.m2/repository/com/google/cloud/spring-cloud-generator/"${project_version}/${spring_generator_jar_name}" \
    "${output_folder}"
  chmod 555 ${output_folder}/*.jar

  # We download gapic-showcase and prepare the protos in output_folder
  sparse_clone https://github.com/googleapis/gapic-showcase.git "schema/google/showcase/v1beta1"
  pushd gapic-showcase
  git checkout "v${gapic_showcase_server_version}"
  cp -r schema "${output_folder}"
  popd #gapic-showcase

  # We download googleapis and prepare the protos in output_folder
  if [[ ! -d "./googleapis" ]]; then
    sparse_clone https://github.com/googleapis/googleapis.git "google/iam/v1 google/cloud/location google/api google/longrunning google/rpc google/type"
  fi
  pushd googleapis
  cp -r google "${output_folder}"
  popd #googleapis

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
  output_srcjar_zip_name="showcase_java_gapic_spring_raw.srcjar.zip"

  "${protoc_path}"/protoc \
    "--experimental_allow_proto3_optional" \
    "--plugin=protoc-gen-java_gapic_spring=${SPRING_GENERATOR_DIR}/spring-cloud-generator-wrapper" \
    "--java_gapic_spring_out=${output_folder}/${output_srcjar_zip_name}" \
    "--java_gapic_spring_opt=$(get_gapic_opts  "${transport}" "${rest_numeric_enums}" "${gapic_yaml}" "${service_config}" "${service_yaml}")" \
    ${proto_files} ${gapic_additional_protos} # Do not quote because this variable should not be treated as one long string.




  # Post-process generated modules
  unzip "${output_srcjar_zip_name}"
  copy_and_unzip "${output_folder}/temp-codegen-spring.srcjar" "temp-codegen-spring.srcjar" "${SPRING_GENERATOR_DIR}/showcase" ${showcase_starter_dir}
  modify_starter_pom "${showcase_starter_dir}/pom.xml" "com.google.cloud" "gapic-showcase" "${project_version}"

  popd #output_folder

  # Additional pom.xml modifications for showcase starter
  # Add explicit gapic-showcase version
  sed -i'' '/^ *<artifactId>gapic-showcase<\/artifactId>*/a \ \ \ \ \ \ <version>'"${gapic_showcase_client_version}"'</version>' "${showcase_starter_dir}/pom.xml"
  # Update relative path to parent pom (different repo structure from starters)
  relative_path="\ \ \ \ <relativePath>..\/..\/..\/spring-cloud-gcp-starters\/pom.xml<\/relativePath>"
  sed -i'' 's/^ *<relativePath>.*/'"${relative_path}"'/g' ${showcase_starter_dir}/pom.xml

  # Run google-java-format on generated code
  run_formatter "${showcase_starter_dir}"

  # Remove downloaded repos
  popd #showcase
  popd #sdk-platform-java
  rm -rdf ${SPRING_GENERATOR_DIR}/sdk-platform-java
  rm -rdf gapic-showcase
  popd #spring_root_dir
}

if [[ "${update}" == "true" ]]; then
    echo "Running script to perform showcase-spring-starter update"
    generate_showcase_spring_starter ${showcase_starter_old_dir}
  else
    echo "Running script to perform showcase-spring-starter verification"
    generate_showcase_spring_starter ${showcase_starter_new_dir}
    verify ${showcase_starter_old_dir} ${showcase_starter_new_dir}
fi
