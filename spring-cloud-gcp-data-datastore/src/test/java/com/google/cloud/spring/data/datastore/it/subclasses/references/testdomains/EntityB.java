package com.google.cloud.spring.data.datastore.it.subclasses.references.testdomains;

import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;

@Entity(name = "A")
@DiscriminatorValue("B")
public class EntityB extends EntityA {}
