package com.example;

import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class ConverterConfig {

    @Bean
    public SpannerEntityProcessor spannerEntityProcessor(SpannerMappingContext spannerMappingContext) {
        return new ConverterAwareMappingSpannerEntityProcessor(spannerMappingContext,
                Arrays.asList(new WriteConverter()),
                Arrays.asList(new ReadConverter()));
    }
}
