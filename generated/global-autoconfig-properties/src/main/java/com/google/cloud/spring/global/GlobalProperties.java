package com.google.cloud.spring.global;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("com.google.cloud.global")
public class GlobalProperties implements CredentialsSupplier {

    // Overrides the GCP OAuth2 credentials specified in the Core module.
    @NestedConfigurationProperty
    private final Credentials credentials = new Credentials();

    @Override
    public Credentials getCredentials() {
        return this.credentials;
    }
}
