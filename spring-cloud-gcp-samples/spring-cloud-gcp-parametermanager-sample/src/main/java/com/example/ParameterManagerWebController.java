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

package com.example;

import com.google.cloud.parametermanager.v1.ParameterFormat;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
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
public class ParameterManagerWebController {

  private static final String INDEX_PAGE = "index.html";
  private static final String APPLICATION_PARAMETER_FROM_VALUE = "applicationParameterFromValue";

  private final ParameterManagerTemplate parameterManagerTemplate;
  // Application parameters can be accessed from the configuration properties class,
  // parameter can be refreshed when decorated with @RefreshScope on the class
  private final ParameterManagerConfiguration configuration;

  @Value("${pm@global/application-fake/dev:DEFAULT}")
  private String defaultParameter;

  // Application parameters can be accessed using @Value syntax.
  @Value("${pm@global/application-parameter/dev:DEFAULT}")
  private String appParameterFromValue;

  public ParameterManagerWebController(
      ParameterManagerTemplate parameterManagerTemplate,
      ParameterManagerConfiguration configuration) {
    this.parameterManagerTemplate = parameterManagerTemplate;
    this.configuration = configuration;
  }

  @GetMapping("/")
  public ModelAndView renderIndex(ModelMap map) {
    map.put("applicationDefaultParameter", defaultParameter);
    map.put(APPLICATION_PARAMETER_FROM_VALUE, appParameterFromValue);
    map.put("applicationParameterFromConfigurationProperties", configuration.getParameter());
    return new ModelAndView(INDEX_PAGE, map);
  }

  @GetMapping("/getParameter")
  @ResponseBody
  public String getParameter(
      @RequestParam String locationId,
      @RequestParam String parameterId,
      @RequestParam String versionId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    String parameterPayload;
    String parameterIdentifier;
    if (StringUtils.isEmpty(projectId)) {
      parameterIdentifier = "pm@" + locationId + "/" + parameterId + "/" + versionId;
    } else {
      parameterIdentifier =
          "pm@" + projectId + "/" + locationId + "/" + parameterId + "/" + versionId;
    }
    parameterPayload = this.parameterManagerTemplate.getParameterString(parameterIdentifier);
    return "Parameter Version ID: "
        + HtmlUtils.htmlEscape(versionId)
        + " | Value: "
        + parameterPayload
        + "<br/><br/><a href='/'>Go back</a>";
  }

  @PostMapping("/createParameter")
  public ModelAndView createParameter(
      @RequestParam String locationId,
      @RequestParam String parameterId,
      @RequestParam String versionId,
      @RequestParam String parameterPayload,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    ParameterFormat format = ParameterFormat.JSON;
    if (StringUtils.isEmpty(projectId)) {
      this.parameterManagerTemplate.createParameter(
          locationId, parameterId, versionId, parameterPayload, format);
    } else {
      this.parameterManagerTemplate.createParameter(
          projectId, locationId, parameterId, versionId, parameterPayload, format);
    }
    map.put(APPLICATION_PARAMETER_FROM_VALUE, this.appParameterFromValue);
    map.put("message", "Parameter created!");
    return new ModelAndView(INDEX_PAGE, map);
  }

  @PostMapping("/deleteParameter")
  public ModelAndView deleteParameter(
      @RequestParam String locationId,
      @RequestParam String parameterId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    if (StringUtils.isEmpty(projectId)) {
      this.parameterManagerTemplate.deleteParameter(locationId, parameterId);
    } else {
      this.parameterManagerTemplate.deleteParameter(projectId, locationId, parameterId);
    }

    map.put(APPLICATION_PARAMETER_FROM_VALUE, this.appParameterFromValue);
    map.put("message", "Parameter deleted!");
    return new ModelAndView(INDEX_PAGE, map);
  }

  @PostMapping("/deleteParameterVersion")
  public ModelAndView deleteParameterVersion(
      @RequestParam String locationId,
      @RequestParam String parameterId,
      @RequestParam String versionId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    if (StringUtils.isEmpty(projectId)) {
      this.parameterManagerTemplate.deleteParameterVersion(locationId, parameterId, versionId);
    } else {
      this.parameterManagerTemplate.deleteParameterVersion(
          projectId, locationId, parameterId, versionId);
    }

    map.put("message", "Parameter Version deleted!");
    return new ModelAndView(INDEX_PAGE, map);
  }
}
