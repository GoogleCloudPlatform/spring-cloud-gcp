package com.google.cloud.spring.data.datastore.it.testdomains;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import java.util.List;

public interface PetRepository extends DatastoreRepository<Pet, Long> {
  List<Pet> findByName(String s);
}
