/*
 * Copyright 2023 Google LLC
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

import com.google.api.generator.engine.ast.ClassDefinition;
import com.google.api.generator.engine.ast.PackageInfoDefinition;
import com.google.api.generator.engine.writer.JavaWriterVisitor;
import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicPackageInfo;
import com.google.cloud.generator.spring.utils.Utils;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class SpringWriter {
  static class GapicWriterException extends RuntimeException {

    public GapicWriterException(String errorMessage, Throwable cause) {
      super(errorMessage, cause);
    }
  }

  public static CodeGeneratorResponse write(
      GapicContext context,
      List<GapicClass> clazzes,
      GapicPackageInfo gapicPackageInfo,
      String outputFilePath) {
    ByteString.Output output = ByteString.newOutput();
    JavaWriterVisitor codeWriter = new JavaWriterVisitor();
    JarOutputStream jos;
    try {
      jos = new JarOutputStream(output);
    } catch (IOException e) {
      throw new GapicWriterException(e.getMessage(), e);
    }

    for (GapicClass gapicClazz : clazzes) {
      writeClazz(gapicClazz, codeWriter, jos);
    }

    // write spring.factories file
    writeAutoConfigRegistration(context, jos);
    writeSpringAdditionalMetadataJson(context, jos);
    writePom(context, jos);

    // write package-info.java
    writePackageInfo(gapicPackageInfo, codeWriter, jos);

    try {
      jos.finish();
      jos.flush();
    } catch (IOException e) {
      throw new GapicWriterException(e.getMessage(), e);
    }

    CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();
    response
        .setSupportedFeatures(CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL_VALUE)
        .addFileBuilder()
        .setName(outputFilePath)
        .setContentBytes(output.toByteString());
    return response.build();
  }

  private static String writeClazz(
      GapicClass gapicClazz, JavaWriterVisitor codeWriter, JarOutputStream jos) {
    ClassDefinition clazz = gapicClazz.classDefinition();

    clazz.accept(codeWriter);
    String code = codeWriter.write();
    codeWriter.clear();

    String path = getPath(clazz.packageString(), clazz.classIdentifier().name());
    String className = clazz.classIdentifier().name();
    JarEntry jarEntry = new JarEntry(String.format("%s/%s.java", path, className));
    try {
      jos.putNextEntry(jarEntry);
      jos.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException(
          String.format(
              "Could not write code for class %s.%s: %s",
              clazz.packageString(), clazz.classIdentifier().name(), e.getMessage()),
          e);
    }
    return path;
  }

  private static String writePackageInfo(
      GapicPackageInfo gapicPackageInfo, JavaWriterVisitor codeWriter, JarOutputStream jos) {
    PackageInfoDefinition packageInfo = gapicPackageInfo.packageInfo();
    packageInfo.accept(codeWriter);
    String code = codeWriter.write();
    codeWriter.clear();

    String packagePath = "src/main/java/" + packageInfo.pakkage().replaceAll("\\.", "/");
    JarEntry jarEntry = new JarEntry(String.format("%s/package-info.java", packagePath));
    try {
      jos.putNextEntry(jarEntry);
      jos.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException("Could not write code for package-info.java", e);
    }
    return packagePath;
  }

  @VisibleForTesting
  static String buildAutoConfigRegistrationString(GapicContext context) {
    StringJoiner sb = new StringJoiner("\n", "", "");
    context
        .services()
        .forEach(
            service ->
                sb.add(
                    String.format(
                        "%s.spring.%s",
                        service.pakkage(), Utils.getServiceAutoConfigurationClassName(service))));
    return sb.toString();
  }

  private static void writeAutoConfigRegistration(GapicContext context, JarOutputStream jos) {
    String path = "src/main/resources/META-INF/spring";
    String fileName =
        String.format("%s/org.springframework.boot.autoconfigure.AutoConfiguration.imports", path);
    JarEntry jarEntry = new JarEntry(fileName);
    try {
      jos.putNextEntry(jarEntry);
      String result = buildAutoConfigRegistrationString(context);
      jos.write(result.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException(
          String.format("Could not write spring autoconfiguration registration to %s", fileName),
          e);
    }
  }

  @VisibleForTesting
  static String buildSpringAdditionalMetadataJsonString(GapicContext context) {
    JsonObject jsonResult = new JsonObject();
    JsonArray objectArray = new JsonArray();
    context
        .services()
        .forEach(
            service -> {
              JsonObject innerObject = new JsonObject();
              innerObject.addProperty(
                  "name",
                  String.format(
                      "%s.enabled",
                      Utils.getSpringPropertyPrefix(
                          Utils.getPackageName(context), service.name())));
              innerObject.addProperty("type", "java.lang.Boolean");
              innerObject.addProperty(
                  "description",
                  String.format(
                      "Auto-configure Google Cloud %s components.",
                      Utils.getLibName(context) + "/" + service.name()));
              innerObject.addProperty("defaultValue", true);
              objectArray.add(innerObject);
            });
    jsonResult.add("properties", objectArray);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonElement prettyJson = JsonParser.parseString(jsonResult.toString());
    return gson.toJson(prettyJson);
  }

  private static void writeSpringAdditionalMetadataJson(GapicContext context, JarOutputStream jos) {
    String path = "src/main/resources/META-INF";
    JarEntry jarEntry =
        new JarEntry(String.format("%s/additional-spring-configuration-metadata.json", path));
    try {
      jos.putNextEntry(jarEntry);
      String result = buildSpringAdditionalMetadataJsonString(context);
      jos.write(result.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException(
          "Could not write additional-spring-configuration-metadata.json", e);
    }
  }

  @VisibleForTesting
  static String buildPomString(GapicContext context) {
    String clientLibraryShortName = Utils.getLibName(context);
    String clientLibraryGroupId = "{{client-library-group-id}}";
    String clientLibraryName = "{{client-library-artifact-id}}";

    String springStarterArtifactId = clientLibraryName + "-spring-starter";
    String springStarterName = "Spring Boot Starter - " + clientLibraryShortName;
    String springParentVersion = "{{parent-version}}";

    StringJoiner sb = new StringJoiner(",\\\n");
    sb.add(
        String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                + "  <modelVersion>4.0.0</modelVersion>\n"
                + "\n"
                + "  <parent>\n"
                + "    <groupId>com.google.cloud</groupId>\n"
                + "    <artifactId>spring-cloud-gcp-starters</artifactId>\n"
                + "    <version>%s</version><!-- {x-version-update:spring-cloud-gcp:current} -->\n"
                + "    <relativePath>../../spring-cloud-gcp-starters/pom.xml</relativePath>\n"
                + "  </parent>\n"
                + "  <artifactId>%s</artifactId>\n"
                + "  <version>${project.parent.version}-preview</version>\n"
                + "  <name>%s</name>\n"
                + "  <description>Spring Boot Starter with AutoConfiguration for %s</description>\n"
                + "\n"
                + "\n"
                + "  <dependencies>\n"
                + "    <dependency>\n"
                + "      <groupId>%s</groupId>\n"
                + "      <artifactId>%s</artifactId>\n"
                + "    </dependency>\n"
                + "\n"
                + "    <dependency>\n"
                + "      <groupId>org.springframework.boot</groupId>\n"
                + "      <artifactId>spring-boot-starter</artifactId>\n"
                + "    </dependency>\n"
                + "\n"
                + "  <dependency>\n"
                + "    <groupId>com.google.cloud</groupId>\n"
                + "    <artifactId>spring-cloud-gcp-autoconfigure</artifactId>\n"
                + "  </dependency>\n"
                + "</dependencies>\n"
                + "\n"
                + "</project>",
            springParentVersion,
            springStarterArtifactId,
            springStarterName,
            clientLibraryShortName,
            clientLibraryGroupId,
            clientLibraryName));

    return sb.toString();
  }

  private static void writePom(GapicContext context, JarOutputStream jos) {
    JarEntry jarEntry = new JarEntry("pom.xml");
    try {
      jos.putNextEntry(jarEntry);
      String result = buildPomString(context);
      jos.write(result.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException("Could not write pom.xml", e);
    }
  }

  private static String getPath(String pakkage, String className) {
    String path = pakkage.replaceAll("\\.", "/");
    if (className.startsWith("Mock") || className.endsWith("Test")) {
      path = "src/test/java/" + path;
    } else {
      path = "src/main/java/" + path;
    }

    // Resource name helpers go into the protobuf package. Denote this with "proto/src/main/*".
    if (className.endsWith("Name")) {
      path = "proto/" + path;
    }
    return path;
  }
}
