set -o pipefail
set -e

#### Configuration of env variables

# If not set, assume working directory is spring-cloud-generator
if [[ -z "$SPRING_GENERATOR_DIR" ]]; then
  SPRING_GENERATOR_DIR=`pwd`
fi
SPRING_ROOT_DIR=${SPRING_GENERATOR_DIR}/..

# If not provided, parse libraries-bom version from spring cloud gcp BOM
if [[ -z "$LIBRARIES_BOM_VERSION" ]]; then
  LIBRARIES_BOM_VERSION=$(xmllint --xpath "string(//*[local-name()='gcp-libraries-bom.version'])" spring-cloud-gcp-dependencies/pom.xml
fi

# Compute the Spring Cloud GCP project version.
cd ${SPRING_ROOT_DIR}
PROJECT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

#### Execute prepare and generation steps

cd ${SPRING_GENERATOR_DIR}
source ./scripts/generate-steps.sh

reset_previews_folder
MONOREPO_TAG=$(compute_monorepo_tag ${LIBRARIES_BOM_VERSION})
generate_libraries_list ${MONOREPO_TAG}
install_spring_generator
setup_googleapis

LIBRARIES=$(cat ${SPRING_GENERATOR_DIR}/scripts/resources/library_list.txt | tail -n+2)

# For each of the entries in the library list, prepare googleapis folder
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "preparing protos and bazel rules for $library_name"
  prepare_bazel_build $googleapis_location $googleapis_commitish 2>&1 | tee tmp-output || save_error_info "bazel_build"
done <<< "${LIBRARIES}"

# Invoke all bazel build targets
cd ${SPRING_GENERATOR_DIR}/googleapis
bazel_build_all
cd ${SPRING_GENERATOR_DIR}

# For each of the entries in the library list, perform post-processing steps
while IFS=, read -r library_name googleapis_location coordinates_version googleapis_commitish monorepo_folder; do
  echo "processing library $library_name"
  group_id=$(echo $coordinates_version | cut -f1 -d:)
  artifact_id=$(echo $coordinates_version | cut -f2 -d:)
  postprocess_library $library_name $artifact_id $group_id $PROJECT_VERSION $googleapis_location $monorepo_folder $googleapis_commitish $monorepo_commitish 2>&1 | tee tmp-output || save_error_info "postprocess_$library_name"
done <<< "${LIBRARIES}"
