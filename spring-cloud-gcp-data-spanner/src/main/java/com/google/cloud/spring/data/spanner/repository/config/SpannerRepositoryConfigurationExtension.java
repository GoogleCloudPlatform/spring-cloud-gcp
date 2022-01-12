/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.data.spanner.repository.config;

import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.google.cloud.spring.data.spanner.repository.support.SpannerRepositoryFactoryBean;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.w3c.dom.Element;

/**
 * A boilerplate class configuring the instantiation of Spanner repositories.
 *
 * @since 1.1
 */
public class SpannerRepositoryConfigurationExtension
    extends RepositoryConfigurationExtensionSupport {

  @Override
  protected String getModulePrefix() {
    return "spanner";
  }

  @Override
  public String getRepositoryFactoryBeanClassName() {
    return SpannerRepositoryFactoryBean.class.getName();
  }

  @Override
  public void postProcess(
      BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
    AnnotationAttributes attributes = config.getAttributes();

    builder.addPropertyReference("spannerTemplate", attributes.getString("spannerTemplateRef"));
    builder.addPropertyReference(
        "spannerMappingContext", attributes.getString("spannerMappingContextRef"));
  }

  @Override
  protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
    return Collections.singleton(Table.class);
  }

  @Override
  protected Collection<Class<?>> getIdentifyingTypes() {
    return Collections.singleton(SpannerRepository.class);
  }

  @Override
  public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
    Element element = config.getElement();

    ParsingUtils.setPropertyReference(builder, element, "spanner-template-ref", "spannerTemplate");
    ParsingUtils.setPropertyReference(
        builder, element, "spanner-mapping-context-ref", "spannerMappingContext");
  }
}
