/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.datastore;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DatastoreReaderWriter;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.data.datastore.core.DatastoreOperations;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreCustomConversions;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreEntityConverter;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreServiceObjectToKeyFactory;
import com.google.cloud.spring.data.datastore.core.convert.DefaultDatastoreEntityConverter;
import com.google.cloud.spring.data.datastore.core.convert.ObjectToKeyFactory;
import com.google.cloud.spring.data.datastore.core.convert.ReadWriteConversions;
import com.google.cloud.spring.data.datastore.core.convert.TwoStepsConversions;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

/**
 * Provides Spring Data classes to use with Cloud Datastore.
 *
 * @since 1.1
 */
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.datastore.enabled", matchIfMissing = true)
@ConditionalOnClass({DatastoreOperations.class, Datastore.class})
@EnableConfigurationProperties(GcpDatastoreProperties.class)
public class GcpDatastoreAutoConfiguration {

  private static final Log LOGGER = LogFactory.getLog(GcpDatastoreAutoConfiguration.class);

  private final String projectId;

  private final String namespace;

  private final String databaseId;

  private final Credentials credentials;

  private final String host;

  private final boolean useHttpJson;

  private final int cacheCapacity;

  GcpDatastoreAutoConfiguration(
      GcpDatastoreProperties gcpDatastoreProperties,
      GcpProjectIdProvider projectIdProvider,
      CredentialsProvider credentialsProvider)
      throws IOException {

    this.projectId =
        (gcpDatastoreProperties.getProjectId() != null)
            ? gcpDatastoreProperties.getProjectId()
            : projectIdProvider.getProjectId();
    this.namespace = gcpDatastoreProperties.getNamespace();
    this.databaseId = gcpDatastoreProperties.getDatabaseId();

    String hostToConnect = gcpDatastoreProperties.getHost();
    if (gcpDatastoreProperties.getEmulator().isEnabled()) {
      hostToConnect = "localhost:" + gcpDatastoreProperties.getEmulator().getPort();
      LOGGER.info("Connecting to a local datastore emulator.");
    }

    if (hostToConnect == null) {
      this.credentials =
          (gcpDatastoreProperties.getCredentials().hasKey()
                  ? new DefaultCredentialsProvider(gcpDatastoreProperties)
                  : credentialsProvider)
              .getCredentials();
    } else {
      // Use empty credentials with Datastore Emulator.
      this.credentials = NoCredentials.getInstance();
    }

    this.host = hostToConnect;
    this.useHttpJson = gcpDatastoreProperties.isUseHttpJson();
    this.cacheCapacity = gcpDatastoreProperties.getCacheCapacity();
  }

  @Bean
  @ConditionalOnMissingBean({
    Datastore.class,
    DatastoreNamespaceProvider.class,
    DatastoreProvider.class
  })
  public Datastore datastore() {
    return getDatastore(this.namespace);
  }

  @Bean
  @ConditionalOnMissingBean
  public DatastoreProvider datastoreProvider(
      ObjectProvider<DatastoreNamespaceProvider> namespaceProvider,
      ObjectProvider<Datastore> datastoreProvider) {
    if (datastoreProvider.getIfAvailable() != null) {
      namespaceProvider.ifAvailable(
          unused -> {
            throw new DatastoreDataException(
                "A Datastore namespace provider and Datastore client were both configured. "
                    + "Only one can be configured.");
          });
      return datastoreProvider::getIfAvailable;
    }
    return getDatastoreProvider(namespaceProvider.getIfAvailable());
  }

  @Bean
  @ConditionalOnMissingBean
  public DatastoreCustomConversions datastoreCustomConversions() {
    return new DatastoreCustomConversions();
  }

  @Bean
  @ConditionalOnMissingBean
  public ReadWriteConversions datastoreReadWriteConversions(
      DatastoreCustomConversions customConversions,
      ObjectToKeyFactory objectToKeyFactory,
      DatastoreMappingContext datastoreMappingContext) {
    return new TwoStepsConversions(customConversions, objectToKeyFactory, datastoreMappingContext);
  }

  @Bean
  @ConditionalOnMissingBean
  public DatastoreMappingContext datastoreMappingContext(
      GcpDatastoreProperties gcpDatastoreProperties) {
    return new DatastoreMappingContext(gcpDatastoreProperties.isSkipNullValue());
  }

  @Bean
  @ConditionalOnMissingBean
  public ObjectToKeyFactory objectToKeyFactory(DatastoreProvider datastore) {
    return new DatastoreServiceObjectToKeyFactory(datastore);
  }

  @Bean
  @ConditionalOnMissingBean
  public DatastoreEntityConverter datastoreEntityConverter(
      DatastoreMappingContext datastoreMappingContext, ReadWriteConversions conversions) {
    return new DefaultDatastoreEntityConverter(datastoreMappingContext, conversions);
  }

  @Bean
  @ConditionalOnMissingBean
  public DatastoreTemplate datastoreTemplate(
      Supplier<? extends DatastoreReaderWriter> datastore,
      DatastoreMappingContext datastoreMappingContext,
      DatastoreEntityConverter datastoreEntityConverter,
      ObjectToKeyFactory objectToKeyFactory) {
    return new DatastoreTemplate(
        datastore, datastoreEntityConverter, datastoreMappingContext, objectToKeyFactory);
  }

  private DatastoreProvider getDatastoreProvider(DatastoreNamespaceProvider keySupplier) {
    return new CachedDatastoreProvider(keySupplier, this.cacheCapacity, this::getDatastore);
  }

  /**
   * Thread-safe bounded Least Recently Used (LRU) cache for {@link Datastore} clients keyed by
   * namespace.
   * <p>
   * When dynamic namespaces are configured, each namespace requires its own {@link Datastore}
   * client with dedicated options. To prevent resource leaks from unbounded cache growth
   * (e.g. gRPC channels and threads), this cache evicts the least-recently-used client
   * and ensures proper closure of evicted clients and on context shutdown.
   */
  static class CachedDatastoreProvider implements DatastoreProvider {

    private final DatastoreNamespaceProvider keySupplier;

    private final Map<String, Datastore> store;

    private final Function<String, Datastore> datastoreFactory;

    private final int capacity;

    private volatile boolean closed = false;

    CachedDatastoreProvider(
        DatastoreNamespaceProvider keySupplier,
        int cacheCapacity,
        Function<String, Datastore> datastoreFactory) {
      this.keySupplier = keySupplier;
      this.datastoreFactory = datastoreFactory;
      this.capacity = Math.max(1, cacheCapacity);
      // Access-order LinkedHashMap: eldest accessed entry is at the head for Least Recently Used (LRU) eviction.
      this.store = new LinkedHashMap<>(this.capacity, 0.75f, true);
    }

    @Override
    public Datastore get() {
      String namespace = this.keySupplier != null ? this.keySupplier.get() : null;
      Datastore client;
      // Fast path: check if client already exists under lock.
      synchronized (this.store) {
        if (this.closed) {
          throw new IllegalStateException("DatastoreProvider has been closed");
        }
        client = this.store.get(namespace);
        if (client != null) {
          return client;
        }
      }

      // Create new client outside the lock to avoid blocking concurrent accesses across namespaces.
      Datastore newClient = this.datastoreFactory.apply(namespace);
      Datastore toClose = null;
      synchronized (this.store) {
        if (this.closed) {
          toClose = newClient;
        } else {
          // Re-check in case another thread created a client for this namespace in the meantime.
          client = this.store.get(namespace);
          if (client != null) {
            toClose = newClient;
          } else {
            // Evict the least recently used client if capacity is reached.
            if (this.store.size() >= this.capacity) {
              String eldestKey = this.store.keySet().iterator().next();
              toClose = this.store.remove(eldestKey);
            }
            this.store.put(namespace, newClient);
          }
        }
      }

      // Close evicted or redundant client outside the lock to avoid blocking during network/channel shutdown.
      if (toClose != null) {
        closeDatastore(toClose);
      }
      if (this.closed) {
        throw new IllegalStateException("DatastoreProvider has been closed");
      }
      return client != null ? client : newClient;
    }

    @Override
    public void close() {
      List<Datastore> toClose;
      // Snapshot and clear entries under lock, then close outside the lock.
      synchronized (this.store) {
        this.closed = true;
        toClose = new ArrayList<>(this.store.values());
        this.store.clear();
      }
      for (Datastore datastore : toClose) {
        closeDatastore(datastore);
      }
    }

    int size() {
      synchronized (this.store) {
        return this.store.size();
      }
    }

    private static void closeDatastore(Datastore datastore) {
      if (datastore != null) {
        try {
          datastore.close();
        } catch (Exception e) {
          LOGGER.warn("Failed to close Datastore client", e);
        }
      }
    }
  }

  private Datastore getDatastore(String namespace) {
    DatastoreOptions.Builder builder =
        DatastoreOptions.newBuilder()
            .setProjectId(this.projectId)
            .setHeaderProvider(new UserAgentHeaderProvider(this.getClass()))
            .setCredentials(this.credentials);
    if (this.useHttpJson) {
      builder.setTransportOptions(HttpTransportOptions.newBuilder().build());
    }
    if (namespace != null) {
      builder.setNamespace(namespace);
    }

    if (databaseId != null) {
      builder.setDatabaseId(databaseId);
    }

    if (this.host != null) {
      builder.setHost(this.host);
    }
    return builder.build().getService();
  }

  /** REST settings. */
  @ConditionalOnClass(BackendIdConverter.class)
  static class DatastoreKeyRestSupportAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BackendIdConverter datastoreKeyIdConverter(
        DatastoreMappingContext datastoreMappingContext) {
      return new DatastoreKeyIdConverter(datastoreMappingContext);
    }
  }
}
