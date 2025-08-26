package com.google.cloud.spring.autoconfigure.parametermanager;

import static com.google.cloud.spring.parametermanager.ParameterManagerTemplate.GLOBAL_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultParameterManagerClientFactoryUnitTests {
  @Mock
  private CredentialsProvider credentialsProvider;

  @Mock
  private ParameterManagerClient globalClient;

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
}
