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

  private final SecretManagerTemplate secretManagerTemplate;
  // Application secrets can be accessed using configuration properties class,
  // secret can be refreshed when decorated with @RefreshScope on the class.
  private final SecretConfiguration configuration;

  // Application secrets can be accessed using @Value syntax.
  @Value("${sm://application-secret}")
  private String appSecret;

  public SecretManagerWebController(SecretManagerTemplate secretManagerTemplate,
      SecretConfiguration configuration
  ) {
    this.secretManagerTemplate = secretManagerTemplate;
    this.configuration = configuration;
  }

  @GetMapping("/")
  public ModelAndView renderIndex(ModelMap map) {
    map.put("applicationSecret", appSecret);
    map.put("myApplicationSecret", configuration.getSecret());
    return new ModelAndView("index.html", map);
  }

  @GetMapping("/getSecret")
  @ResponseBody
  public String getSecret(
      @RequestParam String secretId,
      @RequestParam(required = false) String version,
      @RequestParam(required = false) String projectId,
      ModelMap map) {

    if (StringUtils.isEmpty(version)) {
      version = SecretManagerTemplate.LATEST_VERSION;
    }

    String secretPayload;
    if (StringUtils.isEmpty(projectId)) {
      secretPayload =
          this.secretManagerTemplate.getSecretString("sm://" + secretId + "/" + version);
    } else {
      secretPayload =
          this.secretManagerTemplate.getSecretString(
              "sm://" + projectId + "/" + secretId + "/" + version);
    }

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
      @RequestParam(required = false) String projectId,
      ModelMap map) {

    if (StringUtils.isEmpty(projectId)) {
      this.secretManagerTemplate.createSecret(secretId, secretPayload);
    } else {
      this.secretManagerTemplate.createSecret(secretId, secretPayload.getBytes(), projectId);
    }

    map.put("applicationSecret", this.appSecret);
    map.put("message", "Secret created!");
    return new ModelAndView("index.html", map);
  }

  @PostMapping("/deleteSecret")
  public ModelAndView deleteSecret(
      @RequestParam String secretId,
      @RequestParam(required = false) String projectId,
      ModelMap map) {
    if (StringUtils.isEmpty(projectId)) {
      this.secretManagerTemplate.deleteSecret(secretId);
    } else {
      this.secretManagerTemplate.deleteSecret(secretId, projectId);
    }
    map.put("applicationSecret", this.appSecret);
    map.put("message", "Secret deleted!");
    return new ModelAndView("index.html", map);
  }
}
