set -o pipefail
set -e
set -x

source ./scripts/generate-steps.sh

#### Configuration of env variables

# SPRING_GENERATOR_DIR - default to working directory
# LIBRARIES_BOM_VERSION - default to parse from spring cloud gcp BOM, on branch of script execution
# MONOREPO_TAG - default to parse given LIBRARIES_BOM_VERSION
# LIBRARY_LIST_PATH - default to generate and set to ${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..

# Reset target folder for generated code
echo "executing reset_previews_folder" #debug
reset_previews_folder

# Compute the Spring Cloud GCP project version.
cd ${SPRING_ROOT_DIR}
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

# If not provided, parse libraries-bom version from spring cloud gcp BOM
if [[ -z "$LIBRARIES_BOM_VERSION" ]]; then
  LIBRARIES_BOM_VERSION=$(xmllint --xpath "string(//*[local-name()='gcp-libraries-bom.version'])" ${SPRING_ROOT_DIR}/spring-cloud-gcp-dependencies/pom.xml)
fi

# If not provided, compute monorepo tag from libraries BOM version
if [[ -z "$MONOREPO_TAG" ]]; then
  echo "executing compute_monorepo_tag" #debug
  MONOREPO_TAG="v$(compute_monorepo_version ${LIBRARIES_BOM_VERSION})"
fi

# If not provided, generate and set library list path variable
if [[ -z "$LIBRARY_LIST_PATH" ]]; then
  echo "executing generate_libraries_list" #debug
  generate_libraries_list ${MONOREPO_TAG}
  LIBRARY_LIST_PATH=${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt
fi

# Debug
echo "Spring root dir: ${SPRING_ROOT_DIR}"
echo "Spring generator dir: ${SPRING_GENERATOR_DIR}"
echo "Project version: ${PROJECT_VERSION}"
echo "Libraries BOM version: ${LIBRARIES_BOM_VERSION}"
echo "Monorepo tag: ${MONOREPO_TAG}"
echo "Library list path: ${LIBRARY_LIST_PATH}"

#### Execute prepare and generation steps

cd ${SPRING_GENERATOR_DIR}

echo "executing install_spring_generator" #debug
install_spring_generator
echo "executing setup_googleapis" #debug
setup_googleapis

LIBRARIES=$(cat ${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt | tail -n+2)

# For each of the entries in the library list, prepare googleapis folder
echo "looping over libraries to prepare bazel build" #debug
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "preparing protos and bazel rules for $library_name"
  prepare_bazel_build $googleapis_commitish $googleapis_location 2>&1 | tee tmp-output || save_error_info "bazel_prepare_$library_name"
done <<< "${LIBRARIES}"

# Invoke all bazel build targets
echo "invoking bazel_build_all" #debug
cd ${SPRING_GENERATOR_DIR}/googleapis
bazel_build_all
cd ${SPRING_GENERATOR_DIR}

# For each of the entries in the library list, perform post-processing steps
echo "looping over libraries to perform post-processing" #debug
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  postprocess_library $library_name $artifact_id $group_id $PROJECT_VERSION $googleapis_location $monorepo_folder $googleapis_commitish $MONOREPO_TAG 2>&1 | tee tmp-output || save_error_info "postprocess_$library_name"
done <<< "${LIBRARIES}"

rm tmp-output

echo "running formatter on generated code" #debug
run_formatter ${SPRING_ROOT_DIR}/spring-cloud-previews
