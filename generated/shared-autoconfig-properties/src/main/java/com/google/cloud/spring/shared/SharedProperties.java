package com.google.cloud.spring.shared;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("spring.auto.shared")
public class SharedProperties implements CredentialsSupplier {

    // Overrides the GCP OAuth2 credentials specified in the Core module.
    @NestedConfigurationProperty
    private final Credentials credentials = new Credentials("https://www.googleapis.com/auth/cloud-language");

    @Override
    public Credentials getCredentials() {
        return this.credentials;
    }
}
