/*
 *  Copyright 2017 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.cloud.gcp.autoconfigure.auth;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

/**
 * @author Vinicius Carvalho
 */
@Configuration
@ConditionalOnClass(GoogleCredentials.class)
public class CredentialsAutoConfiguration implements EnvironmentAware{

	private Environment environment;

	@Bean
	public GoogleCredentials googleCredentials(ResourceLoader resourceLoader) throws Exception{
		if(!StringUtils.isEmpty(this.environment.getProperty("cloud.gcp.auth.location"))){
			return GoogleCredentials.fromStream(
					new FileInputStream(resourceLoader
							.getResource(this.environment.getProperty("cloud.gcp.auth.location")).getFile()));
		}
		else{
			return GoogleCredentials.getApplicationDefault();
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
