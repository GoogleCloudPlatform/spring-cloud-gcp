set -o pipefail
set -e
set -x

source ./scripts/generate-steps.sh

#### Configuration of env variables

# SPRING_GENERATOR_DIR - default to working directory
# LIBRARIES_BOM_VERSION - default to parse from spring cloud gcp BOM, on branch of script execution
# MONOREPO_TAG - default to parse given LIBRARIES_BOM_VERSION
# LIBRARY_LIST_PATH - if not provided, generates list at ${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  echo "No SPRING_GENERATOR_DIR override provided, assuming working directory"
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..

# Reset target folder for generated code
cd ${SPRING_GENERATOR_DIR}
echo "executing reset_previews_folder"
reset_previews_folder

# Compute the Spring Cloud GCP project version.
cd ${SPRING_ROOT_DIR}
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

# If not provided, parse libraries-bom version from spring cloud gcp BOM
if [[ -z "$LIBRARIES_BOM_VERSION" ]]; then
  echo "No LIBRARIES_BOM_VERSION override provided, parsing from spring cloud gcp BOM"
  LIBRARIES_BOM_VERSION=$(xmllint --xpath "string(//*[local-name()='gcp-libraries-bom.version'])" ${SPRING_ROOT_DIR}/spring-cloud-gcp-dependencies/pom.xml)
fi

# If not provided, compute monorepo tag from libraries BOM version
if [[ -z "$MONOREPO_TAG" ]]; then
  echo "No MONOREPO_TAG override provided, computing from LIBRARIES_BOM_VERSION: ${LIBRARIES_BOM_VERSION}"
  MONOREPO_TAG="v$(compute_monorepo_version ${LIBRARIES_BOM_VERSION})"
fi

# If not provided, generate and set library list path variable
# find googleapis commitish from monrepo tag
if [[ -z "$LIBRARY_LIST_PATH" ]]; then
  echo "No LIBRARY_LIST_PATH override provided, generating for MONOREPO_TAG: ${MONOREPO_TAG}"
  cd ${SPRING_GENERATOR_DIR}
  generate_libraries_list ${MONOREPO_TAG}
  LIBRARY_LIST_PATH=${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt
fi

# Log environment variables after configuration
echo "Spring root dir: ${SPRING_ROOT_DIR}"
echo "Spring generator dir: ${SPRING_GENERATOR_DIR}"
echo "Project version: ${PROJECT_VERSION}"
echo "Libraries BOM version: ${LIBRARIES_BOM_VERSION}"
echo "Monorepo tag: ${MONOREPO_TAG}"
echo "Library list path: ${LIBRARY_LIST_PATH}"
echo "Googleapis commitish: ${GOOGLEAPIS_COMMITTISH}"

#### Execute prepare and generation steps

cd ${SPRING_GENERATOR_DIR}

echo "executing setup_googleapis"
setup_googleapis ${GOOGLEAPIS_COMMITTISH}

LIBRARIES=$(cat ${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt | tail -n+2)

# For each of the entries in the library list, prepare googleapis folder
echo "looping over libraries to prepare bazel build"
while IFS=, read -r library_name googleapis_location coordinates_version monorepo_folder; do
  echo "preparing protos and bazel rules for $library_name"
  prepare_bazel_build $googleapis_location 2>&1 | tee tmp-output || save_error_info ${SPRING_GENERATOR_DIR} "bazel_prepare_$library_name"
done <<< "${LIBRARIES}"

# Invoke all bazel build targets
echo "invoking bazel_build_all"
cd ${SPRING_GENERATOR_DIR}/googleapis
bazel_build_all
cd ${SPRING_GENERATOR_DIR}

# For each of the entries in the library list, perform post-processing steps
echo "looping over libraries to perform post-processing"
while IFS=, read -r library_name googleapis_location coordinates_version monorepo_folder; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  postprocess_library $artifact_id $group_id $PROJECT_VERSION $googleapis_location $monorepo_folder $MONOREPO_TAG 2>&1 | tee tmp-output || save_error_info ${SPRING_GENERATOR_DIR} "postprocess_$library_name"
done <<< "${LIBRARIES}"

# Clean up downloaded repo and output file
rm tmp-output
rm -rf googleapis

# Format generated code
echo "running formatter on generated code"
run_formatter ${SPRING_ROOT_DIR}/spring-cloud-previews

