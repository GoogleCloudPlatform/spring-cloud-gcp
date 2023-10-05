package com.google.cloud.spring.data.datastore.it.subclasses.references.testdomains;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubclassesReferencesEntityArepository extends DatastoreRepository<EntityA, Key> {}
