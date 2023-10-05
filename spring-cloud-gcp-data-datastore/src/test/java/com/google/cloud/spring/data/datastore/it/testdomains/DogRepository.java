package com.google.cloud.spring.data.datastore.it.testdomains;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import com.google.cloud.spring.data.datastore.repository.query.Query;
import java.util.List;

public interface DogRepository extends DatastoreRepository<Dog, Long> {
  List<Dog> findByName(String s);

  @Query("select * from Pet")
  List<Dog> findByCustomQuery();
}
