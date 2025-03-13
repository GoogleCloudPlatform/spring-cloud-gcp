/*
 * Copyright 2017-2020 the original author or authors.
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

package com.example;

import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

@Controller
public class SecretManagerWebController {

  private static final String INDEX_PAGE = "index.html";
  private static final String APPLICATION_SECRET_FROM_VALUE = "applicationSecretFromValue";
  private static final String APPLICATION_REGIONAL_SECRET_FROM_VALUE = "applicationRegionalSecretFromValue";

  private final SecretManagerTemplate secretManagerTemplate;
  // Application secrets can be accessed using configuration properties class,
  // secret can be refreshed when decorated with @RefreshScope on the class.
  private final SecretConfiguration configuration;

  // This syntax is not recommended. Please switch your code to the `sm@my_secret` syntax. Users
  // will be warned if using this syntax.
  // Note that the colon of the protocol specification section must be escaped;
  // See https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3440
  //@Value("${sm\\://application-fake:DEFAULT}")
  //private String defaultSecretDeprecatedSyntax;

  // This syntax is not recommended. Please switch your code to the `sm@my_secret` syntax. Users
  // will be warned if using this syntax.
  //@Value("${sm://application-secret}")
  //private String appSecretFromValueDeprecatedSyntax;

  // For the default value to take place, there should be no property called `application-fake`
  // in property files.
  // When using the new syntax, it is not necessary to escape the colon character by nesting
  // placeholders as done with the legacy syntax (${${sm://secret}:DEFAULT}).
  @Value("${sm@application-fake:DEFAULT}")
  private String defaultSecret;

  // Application secrets can be accessed using @Value syntax.
  @Value("${sm@application-secret:DEFAULT}")
  private String appSecretFromValue;

  // Application secrets can be accessed using @Value syntax.
  @Value("${sm@locations/us-central1/application-secret:DEFAULT}")
  private String appRegionalSecretFromValue;

  public SecretManagerWebController(SecretManagerTemplate secretManagerTemplate,
      SecretConfiguration configuration
  ) {
    this.secretManagerTemplate = secretManagerTemplate;
    this.configuration = configuration;
  }

  @GetMapping("/")
  public ModelAndView renderIndex(ModelMap map) {
    map.put("applicationDefaultSecret", defaultSecret);
    map.put(APPLICATION_SECRET_FROM_VALUE, appSecretFromValue);
    map.put(APPLICATION_REGIONAL_SECRET_FROM_VALUE, appRegionalSecretFromValue);
    map.put("applicationSecretFromConfigurationProperties", configuration.getSecret());
    map.put("applicationRegionalSecretFromConfigurationProperties", configuration.getRegSecret());
    return new ModelAndView(INDEX_PAGE, map);
  }

  @GetMapping("/getSecret")
  @ResponseBody
  public String getSecret(
      @RequestParam String secretId,
      @RequestParam(required = false) String locationId,
      @RequestParam(required = false) String version,
      @RequestParam(required = false) String projectId,
      ModelMap map) {

    if (StringUtils.isEmpty(version)) {
      version = SecretManagerTemplate.LATEST_VERSION;
    }

    String secretPayload;
    String secretIdentifier;
    if (StringUtils.isEmpty(projectId)) {
      if (StringUtils.isEmpty(locationId)) {
        secretIdentifier = "sm@" + secretId + "/" + version;
      } else {
        secretIdentifier = "sm@" + "locations/" + locationId + "/" + secretId + "/" + version;
      }
    } else {
      if (StringUtils.isEmpty(locationId)) {
        secretIdentifier = "sm@" + projectId + "/" + secretId + "/" + version;
      } else {
        secretIdentifier = "sm@" + projectId + "/" + locationId + "/" + secretId + "/" + version;
      }
    }
    secretPayload = this.secretManagerTemplate.getSecretString(secretIdentifier);

    return "Secret ID: "
        + HtmlUtils.htmlEscape(secretId)
        + " | Value: "
        + secretPayload
        + "<br/><br/><a href='/'>Go back</a>";
  }

  @PostMapping("/createSecret")
  public ModelAndView createSecret(
      @RequestParam String secretId,
      @RequestParam String secretPayload,
      @RequestParam(required = false) String locationId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {

    if (StringUtils.isEmpty(projectId)) {
      if (StringUtils.isEmpty(locationId)) {
        this.secretManagerTemplate.createSecret(secretId, secretPayload);
      } else {
        this.secretManagerTemplate.createSecret(secretId, secretPayload, locationId);
      }
    } else {
      if (StringUtils.isEmpty(locationId)) {
        this.secretManagerTemplate.createSecret(secretId, secretPayload.getBytes(), projectId);
      } else {
        this.secretManagerTemplate.createSecret(secretId, secretPayload.getBytes(), projectId, locationId);
      }
    }

    map.put(APPLICATION_SECRET_FROM_VALUE, this.appSecretFromValue);
    map.put("message", "Secret created!");
    return new ModelAndView(INDEX_PAGE, map);
  }

  @PostMapping("/deleteSecret")
  public ModelAndView deleteSecret(
      @RequestParam String secretId,
      @RequestParam(required = false) String locationId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    if (StringUtils.isEmpty(projectId)) {
      if (StringUtils.isEmpty(locationId)) {
        this.secretManagerTemplate.deleteSecret(secretId);
      } else {
        this.secretManagerTemplate.deleteSecret(secretId, this.secretManagerTemplate.getProjectId(), locationId);
      }
    } else {
      if (StringUtils.isEmpty(locationId)) {
        this.secretManagerTemplate.deleteSecret(secretId, projectId);
      } else {
        this.secretManagerTemplate.deleteSecret(secretId, projectId, locationId);
      }
    }
    map.put(APPLICATION_SECRET_FROM_VALUE, this.appSecretFromValue);
    map.put("message", "Secret deleted!");
    return new ModelAndView(INDEX_PAGE, map);
  }
}
