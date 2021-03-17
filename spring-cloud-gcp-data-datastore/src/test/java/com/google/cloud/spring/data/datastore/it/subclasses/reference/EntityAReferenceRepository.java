package com.google.cloud.spring.data.datastore.it.subclasses.reference;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityAReference;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityAReferenceRepository extends DatastoreRepository<EntityAReference, Key> {
}
