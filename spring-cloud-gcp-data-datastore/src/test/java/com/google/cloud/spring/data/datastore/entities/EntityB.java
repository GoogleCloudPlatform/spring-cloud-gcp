package com.google.cloud.spring.data.datastore.entities;

import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.common.collect.Lists;

import java.util.List;

@Entity(name = "A")
@DiscriminatorValue("B")
public class EntityB extends EntityA {
    @Descendants
    public List<EntityC> entitiesC = Lists.newArrayList();

    public EntityB() {
    }

    public void addEntityC(EntityC entityC){
        this.entitiesC.add(entityC);
    }

    public List<EntityC> getEntitiesC() {
        return entitiesC;
    }
}
