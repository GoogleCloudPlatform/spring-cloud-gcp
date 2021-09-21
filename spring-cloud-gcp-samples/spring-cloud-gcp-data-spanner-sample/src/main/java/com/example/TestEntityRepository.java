package com.example;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "test", path = "test")
public interface TestEntityRepository extends SpannerRepository<TestEntity, String> {

}

