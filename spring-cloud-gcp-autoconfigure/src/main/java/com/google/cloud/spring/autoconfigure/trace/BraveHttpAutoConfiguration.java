package com.google.cloud.spring.autoconfigure.trace;

import brave.Tracing;
import brave.http.HttpRequestParser;
import brave.http.HttpResponseParser;
import brave.http.HttpTracing;
import brave.http.HttpTracingCustomizer;
import java.util.List;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HttpTracing.class)
@AutoConfigureAfter({BraveAutoConfiguration.class})
public class BraveHttpConfiguration {

  @Bean
  @ConditionalOnMissingBean
  HttpTracing httpTracing(
      Tracing tracing,
      @Nullable HttpRequestParser httpRequestParser,
      @Nullable HttpResponseParser httpResponseParser,
      @Nullable List<HttpTracingCustomizer> httpTracingCustomizers) {
    HttpTracing.Builder builder = HttpTracing.newBuilder(tracing);
    if (httpRequestParser != null || httpResponseParser != null) {
      if (httpRequestParser != null) {
        builder.clientRequestParser(httpRequestParser);
      }
      if (httpResponseParser != null) {
        builder.clientResponseParser(httpResponseParser);
      }
    }

    if (httpTracingCustomizers != null) {
      for (HttpTracingCustomizer customizer : httpTracingCustomizers) {
        customizer.customize(builder);
      }
    }
    return builder.build();
  }
}
