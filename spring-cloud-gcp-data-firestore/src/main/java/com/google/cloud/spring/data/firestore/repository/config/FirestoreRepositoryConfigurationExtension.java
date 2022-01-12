/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.data.firestore.repository.config;

import com.google.cloud.spring.data.firestore.Document;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.google.cloud.spring.data.firestore.repository.support.FirestoreRepositoryFactoryBean;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.w3c.dom.Element;

/**
 * Configures the use of Firestore repositories.
 *
 * @since 1.2
 */
public class FirestoreRepositoryConfigurationExtension
    extends RepositoryConfigurationExtensionSupport {
  @Override
  protected String getModulePrefix() {
    return "firestore-reactive";
  }

  @Override
  public String getRepositoryFactoryBeanClassName() {
    return FirestoreRepositoryFactoryBean.class.getName();
  }

  @Override
  public void postProcess(
      BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
    AnnotationAttributes attributes = config.getAttributes();

    builder.addPropertyReference("firestoreTemplate", attributes.getString("firestoreTemplateRef"));
    builder.addPropertyReference(
        "firestoreMappingContext", attributes.getString("firestoreMappingContextRef"));
  }

  @Override
  protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
    return Collections.singleton(Document.class);
  }

  @Override
  protected Collection<Class<?>> getIdentifyingTypes() {
    return Collections.singleton(FirestoreReactiveRepository.class);
  }

  @Override
  public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
    Element element = config.getElement();

    ParsingUtils.setPropertyReference(
        builder, element, "firestore-template-ref", "firestoreTemplate");
    ParsingUtils.setPropertyReference(
        builder, element, "firestore-mapping-context-ref", "firestoreMappingContext");
  }

  @Override
  protected boolean useRepositoryConfiguration(RepositoryMetadata repositoryMetadata) {
    return repositoryMetadata.isReactiveRepository();
  }
}
