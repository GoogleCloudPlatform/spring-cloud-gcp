package com.google.cloud.spring.data.datastore.entities.subclasses.reference;

import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.common.collect.Lists;

import java.util.List;

@Entity(name = "A")
@DiscriminatorValue("B")
public class EntityBReference extends EntityAReference {
}
