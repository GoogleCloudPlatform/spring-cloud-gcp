/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.google.cloud.spanner.r2dbc.v2.JsonWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomConfiguration extends AbstractR2dbcConfiguration {

  @Autowired
  ApplicationContext applicationContext;

  @Override
  public ConnectionFactory connectionFactory() {
    return null;
  }

  @Bean
  @Override
  public R2dbcCustomConversions r2dbcCustomConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(this.applicationContext.getBean(JsonToReviewsConverter.class));
    converters.add(this.applicationContext.getBean(ReviewsToJsonConverter.class));
    return new R2dbcCustomConversions(getStoreConversions(), converters);
  }

  @Component
  @ReadingConverter
  public class JsonToReviewsConverter implements Converter<JsonWrapper, Review> {

    private final Gson gson;

    @Autowired
    public JsonToReviewsConverter(Gson gson) {
      this.gson = gson;
    }

    @Override
    public Review convert(JsonWrapper json) {
      try {
        return this.gson.fromJson(json.toString(), Review.class);
      } catch (JsonParseException e) {
        return new Review();
      }
    }
  }

  @Component
  @WritingConverter
  public class ReviewsToJsonConverter implements Converter<Review, JsonWrapper> {

    private final Gson gson;

    @Autowired
    public ReviewsToJsonConverter(Gson gson) {
      this.gson = gson;
    }

    @Override
    public JsonWrapper convert(Review source) {
        return JsonWrapper.of(this.gson.toJson(source));
    }
  }

}
