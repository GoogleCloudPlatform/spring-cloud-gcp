package com.google.cloud.spring.autoconfigure.parametermanager;

import static com.google.cloud.spring.parametermanager.ParameterManagerTemplate.GLOBAL_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.spring.autoconfigure.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultParameterManagerClientFactoryUnitTests {
  @Mock private CredentialsProvider credentialsProvider;

  @Mock private ParameterManagerClient globalClient;

  private DefaultParameterManagerClientFactory factory;

  @BeforeEach
  void setUp() {
    factory = new DefaultParameterManagerClientFactory(credentialsProvider, globalClient);
  }

  @Test
  void getClientWithNullLocationReturnsGlobalClient() {
    ParameterManagerClient client = factory.getClient(null);
    assertThat(client).isEqualTo(globalClient);
  }

  @Test
  void getClientWithEmptyLocationReturnsGlobalClient() {
    ParameterManagerClient client = factory.getClient("");
    assertThat(client).isEqualTo(globalClient);
  }

  @Test
  void getClientWithGlobalLocationReturnsGlobalClient() {
    ParameterManagerClient client = factory.getClient(GLOBAL_LOCATION);
    assertThat(client).isEqualTo(globalClient);
  }

  @Test
  void getClientCachesClients() {
    ParameterManagerClient firstCall = factory.getClient(GLOBAL_LOCATION);
    assertThat(firstCall).isEqualTo(globalClient);

    ParameterManagerClient secondCall = factory.getClient(GLOBAL_LOCATION);
    assertThat(secondCall).isSameAs(firstCall);
  }

  private static java.util.function.Function<String, String> env(String value) {
    return name -> "GOOGLE_CLOUD_UNIVERSE_DOMAIN".equals(name) ? value : null;
  }

  private DefaultParameterManagerClientFactory factoryWith(
      String universeDomain, java.util.function.Function<String, String> env) {
    CredentialsProvider provider = () -> TestUtils.MOCK_CREDENTIALS;
    return new DefaultParameterManagerClientFactory(provider, globalClient, universeDomain, env);
  }

  @Test
  void regionalClientUsesDefaultUniverseDomainWhenUnset() {
    try (ParameterManagerClient client = factoryWith(null, env(null)).getClient("us-central1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("googleapis.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("parametermanager.us-central1.rep.googleapis.com:443");
    }
  }

  @Test
  void regionalClientUsesEnvUniverseDomainWhenPropertyUnset() {
    try (ParameterManagerClient client =
        factoryWith(null, env("custom.example.com")).getClient("europe-west1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("custom.example.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("parametermanager.europe-west1.rep.custom.example.com:443");
    }
  }

  @Test
  void explicitUniverseDomainTakesPrecedenceOverEnv() {
    try (ParameterManagerClient client =
        factoryWith("explicit.example.com", env("env.example.com")).getClient("us-east1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("explicit.example.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("parametermanager.us-east1.rep.explicit.example.com:443");
    }
  }

  @Test
  void nullEnvProviderFallsBackToSystemEnvironment() {
    String fromEnv = System.getenv("GOOGLE_CLOUD_UNIVERSE_DOMAIN");
    String expected =
        fromEnv != null && !fromEnv.isBlank() ? fromEnv : "googleapis.com";
    assertThat(factoryWith(null, null).resolveUniverseDomain()).isEqualTo(expected);
  }
}
