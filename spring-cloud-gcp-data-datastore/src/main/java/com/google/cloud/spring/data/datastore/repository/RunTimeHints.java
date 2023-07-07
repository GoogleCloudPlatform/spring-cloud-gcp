package com.google.cloud.spring.data.datastore.repository;

import com.google.cloud.spring.data.datastore.core.DatastoreTransactionManager;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.repository.config.DatastoreAuditingRegistrar;
import com.google.cloud.spring.data.datastore.repository.config.DatastoreRepositoriesRegistrar;
import com.google.cloud.spring.data.datastore.repository.config.DatastoreRepositoryConfigurationExtension;
import com.google.cloud.spring.data.datastore.repository.config.EnableDatastoreAuditing;
import com.google.cloud.spring.data.datastore.repository.config.EnableDatastoreRepositories;
import com.google.cloud.spring.data.datastore.repository.query.AbstractDatastoreQuery;
import com.google.cloud.spring.data.datastore.repository.query.DatastorePageable;
import com.google.cloud.spring.data.datastore.repository.query.DatastoreQueryLookupStrategy;
import com.google.cloud.spring.data.datastore.repository.query.DatastoreQueryMethod;
import com.google.cloud.spring.data.datastore.repository.query.GqlDatastoreQuery;
import com.google.cloud.spring.data.datastore.repository.query.PartTreeDatastoreQuery;
import com.google.cloud.spring.data.datastore.repository.query.Query;
import com.google.cloud.spring.data.datastore.repository.support.DatastoreAuditingEventListener;
import com.google.cloud.spring.data.datastore.repository.support.DatastoreRepositoryFactory;
import com.google.cloud.spring.data.datastore.repository.support.DatastoreRepositoryFactoryBean;
import com.google.cloud.spring.data.datastore.repository.support.SimpleDatastoreRepository;
import java.util.Arrays;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class RunTimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

		hints.reflection().registerTypes(
				Arrays.asList(TypeReference.of(SimpleDatastoreRepository.class),
						TypeReference.of(EnableDatastoreRepositories.class),
						TypeReference.of(DatastoreRepositoriesRegistrar.class),
						TypeReference.of(DatastoreRepositoryConfigurationExtension.class),
						TypeReference.of(DatastoreRepositoryFactoryBean.class),
						TypeReference.of(DatastoreRepositoryFactory.class),
						TypeReference.of(DatastoreAuditingEventListener.class),
						TypeReference.of(DatastoreAuditingRegistrar.class),
						TypeReference.of(EnableDatastoreAuditing.class),
						TypeReference.of(DatastoreRepository.class),
						TypeReference.of(AbstractDatastoreQuery.class),
						TypeReference.of(DatastorePageable.class),
						TypeReference.of(DatastoreQueryLookupStrategy.class),
						TypeReference.of(DatastoreQueryMethod.class),
						TypeReference.of(GqlDatastoreQuery.class),
						TypeReference.of(PartTreeDatastoreQuery.class),
						TypeReference.of(Query.class),
						TypeReference.of(DatastoreMappingContext.class),
						TypeReference.of(DatastoreTransactionManager.class)
						),
				hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
						MemberCategory.INVOKE_PUBLIC_METHODS,
						MemberCategory.INVOKE_DECLARED_METHODS,
						MemberCategory.DECLARED_FIELDS));
		hints.proxies().registerJdkProxy(
				org.springframework.aop.SpringProxy.class,
				org.springframework.aop.framework.Advised.class,
				org.springframework.core.DecoratingProxy.class);

		hints.proxies().registerJdkProxy(
				TypeReference.of(EnableDatastoreRepositories.class),
				TypeReference.of(EnableDatastoreAuditing.class),
				TypeReference.of(Query.class));
  }
}
