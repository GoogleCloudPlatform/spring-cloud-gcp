package com.google.cloud.spring.data.datastore.entities.subclasses.descendants;

import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;

@Entity(name = "A")
@DiscriminatorValue("C")
public class EntityCDescendants extends EntityADescendants{
}
