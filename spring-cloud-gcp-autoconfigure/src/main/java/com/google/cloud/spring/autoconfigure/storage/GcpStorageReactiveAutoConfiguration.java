package com.google.cloud.spring.autoconfigure.storage;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.ReactiveTokenProvider;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolver;
import com.google.cloud.spring.storage.GoogleStorageTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@AutoConfiguration
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.storage.reactive.enabled", matchIfMissing = true)
@EnableConfigurationProperties({GcpProperties.class, GcpStorageProperties.class})
@Import(GoogleStorageProtocolResolver.class)
public class GcpStorageReactiveAutoConfiguration {

    private final GcpProjectIdProvider gcpProjectIdProvider;

    private final CredentialsProvider credentialsProvider;

    public GcpStorageReactiveAutoConfiguration(
            GcpProjectIdProvider coreProjectIdProvider,
            CredentialsProvider credentialsProvider,
            GcpStorageProperties gcpStorageProperties)
            throws IOException {

        this.gcpProjectIdProvider =
                gcpStorageProperties.getProjectId() != null
                        ? gcpStorageProperties::getProjectId
                        : coreProjectIdProvider;

        this.credentialsProvider =
                gcpStorageProperties.getCredentials().hasKey()
                        ? new DefaultCredentialsProvider(gcpStorageProperties)
                        : credentialsProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() throws IOException {
        return WebClient.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveTokenProvider reactiveTokenProvider(WebClient webClient) throws IOException {
        return ReactiveTokenProvider.create(credentialsProvider.getCredentials(), webClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public GoogleStorageTemplate storage(WebClient webClient, ReactiveTokenProvider reactiveTokenProvider) throws IOException {
        return new GoogleStorageTemplate(webClient, reactiveTokenProvider);
    }

}
