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

import com.google.cloud.spring.kms.KmsTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class KmsWebController {

	@Autowired
	private Environment environment;

	@Autowired
	private KmsTemplate kmsTemplate;

	@PostMapping("/encrypt")
	public ModelAndView encryt(
			@RequestParam String keyId,
			@RequestParam String text,
			ModelMap map) {

		String encrypted = kmsTemplate.encrypt(keyId, text);
		map.put("message", "Text encrypted: " + encrypted);
		return new ModelAndView("index.html", map);
	}

	@PostMapping("/decrypt")
	public ModelAndView decrypt(
			@RequestParam String keyId,
			@RequestParam String encryptedText,
			ModelMap map) {

		String encrypted = kmsTemplate.decrypt(keyId, encryptedText);
		map.put("message", "Text decrypted " + encrypted);
		return new ModelAndView("index.html", map);
	}

}
