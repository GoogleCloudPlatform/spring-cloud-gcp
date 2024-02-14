/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.generator.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.Service;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicServiceConfig;
import com.google.api.generator.gapic.model.Message;
import com.google.api.generator.gapic.model.ResourceName;
import com.google.api.generator.gapic.model.Transport;
import com.google.api.generator.gapic.protoparser.Parser;
import com.google.api.generator.gapic.protoparser.ServiceConfigParser;
import com.google.api.generator.gapic.protoparser.ServiceYamlParser;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.showcase.v1beta1.EchoOuterClass;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestUtils {

  private static final String TEST_FILES_DIRECTORY = "src/test/resources/";
  private static final String ECHO_SERVICE_DESCRIPTION =
      "This service is used showcase the four main types of rpcs - unary, server\n"
          + " side streaming, client side streaming, and bidirectional streaming. This\n"
          + " service also exposes methods that explicitly implement server delay, and\n"
          + " paginated calls. Set the 'showcase-trailer' metadata key on any method\n"
          + " to have the values echoed in the response trailers.";

  /**
   * Temporary workaround that copies the implementation of
   * {@link com.google.api.generator.test.protoloader.TestProtoLoader} {@code parseShowcaseEcho()}
   * This is due to the resources not being available in the test-jar exported
   * by gapic-generator-java
   * We could in theory create a TestProtoLoader instance with a custom test resources path but the
   * constructor is protected
   * @return The {@link GapicContext} of the Showcase Echo service
   */
  public static GapicContext parseShowcaseEcho() {
    FileDescriptor echoFileDescriptor = EchoOuterClass.getDescriptor();
    ServiceDescriptor echoServiceDescriptor = echoFileDescriptor.getServices().get(0);
    assertEquals(echoServiceDescriptor.getName(), "Echo");

    String serviceYamlFilename = "echo_v1beta1.yaml";
    Path serviceYamlPath = Paths.get(TEST_FILES_DIRECTORY, serviceYamlFilename);
    System.out.println("serviceYamlPath = " + serviceYamlPath.toString());
    Optional<Service> serviceYamlOpt =
        ServiceYamlParser.parse(serviceYamlPath.toString());
    assertTrue(serviceYamlOpt.isPresent());

    Map<String, Message> messageTypes = Parser.parseMessages(echoFileDescriptor);
    Map<String, ResourceName> resourceNames = Parser.parseResourceNames(echoFileDescriptor);
    Set<ResourceName> outputResourceNames = new HashSet<>();
    List<com.google.api.generator.gapic.model.Service> services =
        Parser.parseService(
            echoFileDescriptor, messageTypes, resourceNames, serviceYamlOpt, outputResourceNames);

    // Explicitly adds service description, since this is not parsed from source code location
    // in test protos, as it would from a protoc CodeGeneratorRequest
    List<com.google.api.generator.gapic.model.Service> servicesWithDescription =
        services.stream()
            .map(s -> s.toBuilder().setDescription(ECHO_SERVICE_DESCRIPTION).build())
            .collect(Collectors.toList());

    String jsonFilename = "showcase_grpc_service_config.json";
    Path jsonPath = Paths.get(TEST_FILES_DIRECTORY, jsonFilename);
    Optional<GapicServiceConfig> configOpt = ServiceConfigParser.parse(jsonPath.toString());
    assertTrue(configOpt.isPresent());
    GapicServiceConfig config = configOpt.get();

    return GapicContext.builder()
        .setMessages(messageTypes)
        .setResourceNames(resourceNames)
        .setServices(servicesWithDescription)
        .setServiceConfig(config)
        .setHelperResourceNames(outputResourceNames)
        .setTransport(Transport.GRPC)
        .build();
  }
}
