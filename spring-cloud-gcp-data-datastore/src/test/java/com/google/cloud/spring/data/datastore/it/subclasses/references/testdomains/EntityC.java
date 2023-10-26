package com.google.cloud.spring.data.datastore.it.subclasses.references.testdomains;

import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Reference;

@Entity(name = "A")
@DiscriminatorValue("C")
public class EntityC extends EntityA {
  @Reference private EntityB entityB;

  public EntityC(EntityB entityB) {
    this.entityB = entityB;
  }

  public EntityB getEntityB() {
    return entityB;
  }
}
