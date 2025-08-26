/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.parametermanager;

import com.google.cloud.parametermanager.v1.ParameterFormat;

/**
 * Describes supported operations that one can perform on the Parameter Manager API.
 *
 * <p>For some methods you may specify the parameter from GCP Parameter Manager by URI string. The
 * following parameter URI syntax is supported:
 *
 * <p>1. Long form - specify the project ID, location ID, parameter ID, and version ID
 * pm@projects/{project-id}/locations/{location-id}/parameters/{parameter-id}/versions/{version-id}
 *
 * <p>2. Long form - specify location ID, parameter ID, and version ID
 * pm@locations/{location-id}/parameters/{parameter-id}/versions/{version-id}
 *
 * <p>3. Short form - specify project ID, location ID, parameter ID, and version ID
 * pm@{project-id}/{location-id}/{parameter-id}/{version-id}
 *
 * <p>4. Shortest form - specify locationID, parameter ID and version ID, use default GCP project
 * configured pm@{location-id}/{parameter-id}/{version-id}
 */
public interface ParameterManagerOperations {

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a string.
   */
  void createParameter(String locationId, String parameterId, String versionId, String payload);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a string.
   */
  void createParameter(
      String projectId, String locationId, String parameterId, String versionId, String payload);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload} in provided {@code format}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a string.
   * @param format the parameter format (JSON or YAML or UNFORMATTED).
   */
  void createParameter(
      String locationId,
      String parameterId,
      String versionId,
      String payload,
      ParameterFormat format);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a string.
   * @param format the parameter format (JSON or YAML or UNFORMATTED).
   */
  void createParameter(
      String projectId,
      String locationId,
      String parameterId,
      String versionId,
      String payload,
      ParameterFormat format);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a byte array.
   */
  void createParameter(String locationId, String parameterId, String versionId, byte[] payload);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload} in given {@code format}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a byte array.
   * @param format the parameter format (JSON or YAML or UNFORMATTED).
   */
  void createParameter(
      String locationId,
      String parameterId,
      String versionId,
      byte[] payload,
      ParameterFormat format);

  /**
   * Creates a new parameter or a new version of existing parameter with the provided {@code
   * payload}.
   *
   * <p>If there is already a parameter saved in ParameterManager with the specified {@code
   * parameterId}, then it simply creates a new version with given id under the parameter with the
   * specified {@code payload}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the parameter location string.
   * @param parameterId the parameter ID of the parameter to create.
   * @param versionId the version ID of the parameter version to create.
   * @param payload the parameter payload as a byte array.
   * @param format the parameter format (JSON or YAML or UNFORMATTED).
   */
  void createParameter(
      String projectId,
      String locationId,
      String parameterId,
      String versionId,
      byte[] payload,
      ParameterFormat format);

  /**
   * Enables the specified parameter version under the default-configured project for specified
   * {@code locationId}.
   *
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to enable.
   * @param versionId the version ID to be enabled.
   */
  void enableParameterVersion(String locationId, String parameterId, String versionId);

  /**
   * Enables the specified parameter version under the specified {@code projectId} for specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to enable.
   * @param versionId the version ID to be enabled.
   */
  void enableParameterVersion(
      String projectId, String locationId, String parameterId, String versionId);

  /**
   * Disables the specified parameter version under the default-configured project for specified
   * {@code locationId}.
   *
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to enable.
   * @param versionId the version ID to be enabled.
   */
  void disableParameterVersion(String locationId, String parameterId, String versionId);

  /**
   * Disables the specified parameter version under the specified {@code projectId} for specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to enable.
   * @param versionId the version ID to be enabled.
   */
  void disableParameterVersion(
      String projectId, String locationId, String parameterId, String versionId);

  /**
   * Deletes the specified {@code parameterId} under the default-configured project for specified
   * {@code locationId}.
   *
   * @param parameterId the parameter ID of the parameter to delete.
   * @param locationId the location string where given parameter is present.
   */
  void deleteParameter(String locationId, String parameterId);

  /**
   * Deletes the specified {@code parameterID} under the specified {@code projectId} for specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to delete.
   */
  void deleteParameter(String projectId, String locationId, String parameterId);

  /**
   * Deletes the specified parameter version under the default-configured project for specified
   * {@code locationId}.
   *
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to delete.
   * @param versionId the version ID to be deleted.
   */
  void deleteParameterVersion(String locationId, String parameterId, String versionId);

  /**
   * Deletes the specified parameter version under the specified {@code projectId} for specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to delete.
   * @param versionId the version ID to be deleted.
   */
  void deleteParameterVersion(
      String projectId, String locationId, String parameterId, String versionId);

  /**
   * Checks if the specified {@code parameterID} exists in the default-configured project for
   * specified {@code locationId}.
   *
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to check.
   * @return true if the parameter exists in Parameter Manager; false otherwise
   */
  boolean parameterExists(String locationId, String parameterId);

  /**
   * Checks if the specified {@code parameterID} exists in the specified project for specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where given parameter is present.
   * @param parameterId the parameter ID of the parameter to check.
   * @return true if the parameter exists in Parameter Manager; false otherwise
   */
  boolean parameterExists(String projectId, String locationId, String parameterId);

  /**
   * Checks if the specified parameter version exists in the default-configured project for the
   * specified {@code locationId}.
   *
   * @param locationId the location string where the given parameter version is present.
   * @param parameterId the parameter ID of the parameter to check.
   * @param versionId the version ID of the parameter version to check.
   * @return true if the parameter version exists in Parameter Manager; false otherwise
   */
  boolean parameterVersionExists(String locationId, String parameterId, String versionId);

  /**
   * Checks if the specified parameter version exists in the specified project for the specified
   * {@code locationId}.
   *
   * @param projectId unique identifier of your GCP project.
   * @param locationId the location string where the given parameter version is present.
   * @param parameterId the parameter ID of the parameter to check.
   * @param versionId the version ID of the parameter version to check.
   * @return true if the parameter version exists in Parameter Manager; false otherwise
   */
  boolean parameterVersionExists(
      String projectId, String locationId, String parameterId, String versionId);

  /**
   * Gets the parameter data as a string from the specified {@code parameterIdentifier}.
   *
   * <p>The {@code parameterIdentifier} must be a fully qualified `pm@` protocol string which
   * specifies the parameter (see javadocs of {@link ParameterManagerOperations} for the protocol
   * format).
   *
   * <p>If the parameter ID string is passed in, then this will return the data of the parameter for
   * the default project at the latest version.
   *
   * @param parameterIdentifier a pm@ formatted string specifying the parameter version.
   * @return The parameter data as a string
   */
  String getParameterString(String parameterIdentifier);

  /**
   * Gets the parameter data as a byte array from the specified {@code parameterIdentifier}.
   *
   * <p>The {@code parameterIdentifier} must be a fully qualified `pm@` protocol string which
   * specifies the parameter (see javadocs of {@link ParameterManagerOperations} for the protocol
   * format).
   *
   * <p>If the parameter ID string is passed in, then this will return the data of the parameter for
   * the default project at the latest version.
   *
   * @param parameterIdentifier a pm@ formatted string specifying the parameter version.
   * @return The parameter data as a byte array
   */
  byte[] getParameterBytes(String parameterIdentifier);

  /**
   * Gets the rendered parameter data as a string from the specified {@code parameterIdentifier}.
   *
   * <p>The {@code parameterIdentifier} must be a fully qualified `pm@` protocol string which
   * specifies the parameter (see javadocs of {@link ParameterManagerOperations} for the protocol
   * format).
   *
   * <p>If the parameter ID string is passed in, then this will return the rendered data of the
   * parameter for the default project at the latest version.
   *
   * @param parameterIdentifier a pm@ formatted string specifying the parameter version.
   * @return The rendered parameter data as a string
   */
  String renderedParameterVersionString(String parameterIdentifier);

  /**
   * Gets the rendered parameter data as a byte array from the specified {@code
   * parameterIdentifier}.
   *
   * <p>The {@code parameterIdentifier} must be a fully qualified `pm@` protocol string which
   * specifies the parameter (see javadocs of {@link ParameterManagerOperations} for the protocol
   * format).
   *
   * <p>If the parameter ID string is passed in, then this will return the rendered data of the
   * parameter for the default project at the latest version.
   *
   * @param parameterIdentifier a pm@ formatted string specifying the parameter version.
   * @return The rendered parameter data as a byte array
   */
  byte[] renderedParameterVersionBytes(String parameterIdentifier);
}
