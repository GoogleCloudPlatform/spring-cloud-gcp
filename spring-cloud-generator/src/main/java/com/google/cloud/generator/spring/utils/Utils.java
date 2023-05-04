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

package com.google.cloud.generator.spring.utils;

import com.google.api.generator.gapic.composer.store.TypeStore;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.Method;
import com.google.api.generator.gapic.model.Service;
import com.google.api.generator.gapic.utils.JavaStyle;
import com.google.common.base.CaseFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

  private static final TypeStore FIXED_TYPESTORE = createStaticTypes();

  private static final String BRAND_NAME = "spring.cloud.gcp";

  public static String getServiceAutoConfigurationClassName(Service service) {
    return JavaStyle.toUpperCamelCase(service.name()) + "SpringAutoConfiguration";
  }

  public static String getServicePropertiesClassName(Service service) {
    return JavaStyle.toUpperCamelCase(service.name()) + "SpringProperties";
  }

  public static String getLibName(GapicContext context) {
    // Returns parsed name of client library
    // This should only be used in descriptive context, such as metadata and javadocs

    // Option 1: Use title from service yaml if available (e.g. Client Library Showcase API)
    // However, service yaml is optionally parsed and not always available
    //    if (context.hasServiceYamlProto()
    //        && !Strings.isNullOrEmpty(context.serviceYamlProto().getTitle())) {
    //      return context.serviceYamlProto().getTitle();
    //    }

    // Option 2: Parse ApiShortName from service proto's package name (e.g.
    // com.google.cloud.vision.v1)
    // This approach assumes pattern of xx.[...].xx.lib-name.v[version], which may have
    // discrepancies
    // eg. for vision proto: "com.google.cloud.vision.v1"
    // https://github.com/googleapis/java-vision/blob/main/proto-google-cloud-vision-v1/src/main/proto/google/cloud/vision/v1/image_annotator.proto#L36
    // List<String> pakkagePhrases = Splitter.on(".").splitToList(getPackageName(context));
    // return pakkagePhrases.get(pakkagePhrases.size() - 2);

    // Option 3: Use parsed apiShortName from service proto's default host
    // (e.g. vision.googleapis.com => vision)
    return context.services().get(0).apiShortName();
  }

  public static String getPackageName(GapicContext context) {
    // Returns package name of client library
    return context.services().get(0).pakkage();
  }

  public static String getSpringPackageName(String packageName) {
    // Returns package name of generated spring autoconfiguration library
    // e.g. for vision: com.google.cloud.vision.v1.spring
    return packageName + ".spring";
  }

  public static String getSpringPropertyPrefix(String packageName, String serviceName) {
    // Returns unique prefix for setting properties and enabling autoconfiguration
    // Pattern: [package-name].spring.auto.[service-name]
    // e.g. for vision's ImageAnnotator service:
    // com.google.cloud.vision.v1.image-annotator
    // Service name is converted to lower hyphen as required by ConfigurationPropertyName
    // https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/source/ConfigurationPropertyName.html
    return packageName + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, serviceName);
  }

  public static List<Method> getMethodsForRetryConfiguration(Service service) {
    // Returns list of methods with retry configuration support
    // This currently excludes streaming and LRO methods
    return service.methods().stream()
        .filter(m -> m.stream().equals(Method.Stream.NONE) && !m.hasLro())
        .collect(Collectors.toList());
  }

  private static TypeStore createStaticTypes() {
    List<Class<?>> concreteClazzes = Arrays.asList(org.threeten.bp.Duration.class);
    return new TypeStore(concreteClazzes);
  }
}
