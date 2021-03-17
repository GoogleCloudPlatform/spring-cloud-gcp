package com.google.cloud.spring.data.datastore.it.subclasses.descendants;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.entities.subclasses.descendants.EntityADescendants;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityADescendantsRepository extends DatastoreRepository<EntityADescendants, Key> {
}
