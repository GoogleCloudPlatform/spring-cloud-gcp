package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

/**
 * Unit tests for {@link SecretManagerConfigDataLoader}.
 */
/*
public class SecretManagerConfigDataLoaderUnitTests {

  public static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";
  private final GcpProjectIdProvider idProvider = mock(GcpProjectIdProvider.class);
  private final SecretManagerTemplate template = mock(SecretManagerTemplate.class);
  private final SecretManagerConfigDataLoader loader = new SecretManagerConfigDataLoader(template,
      idProvider);
  private final ConfigDataLoaderContext loaderContext = mock(ConfigDataLoaderContext.class);

  @Test
  public void loadIncorrectResourceThrowsException() {
    SecretManagerServiceClient client = secretManagerServiceClient();
    when(template.secretExists(anyString(), anyString())).thenReturn(false);
    SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(client,
        ConfigDataLocation.of("fake"));
    assertThatExceptionOfType(ConfigDataResourceNotFoundException.class)
        .isThrownBy(() -> loader.load(loaderContext, resource));
  }

  private static SecretManagerServiceClient secretManagerServiceClient() {
    SecretManagerServiceClient client = mock(SecretManagerServiceClient.class);
    SecretVersionName secretVersionName =
        SecretVersionName.newBuilder()
            .setProject(SecretManagerConfigDataLoaderUnitTests.PROJECT_NAME)
            .setSecret("my-secret")
            .setSecretVersion("latest")
            .build();

    when(client.accessSecretVersion(secretVersionName))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(SecretPayload.newBuilder().setData(ByteString.copyFromUtf8("hello")))
                .build());

    return client;
  }
}
*/
