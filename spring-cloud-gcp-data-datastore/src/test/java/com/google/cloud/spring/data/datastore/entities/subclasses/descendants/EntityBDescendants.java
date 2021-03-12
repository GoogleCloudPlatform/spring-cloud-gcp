package com.google.cloud.spring.data.datastore.entities.subclasses.descendants;

import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.common.collect.Lists;

import java.util.List;

@Entity(name = "A")
@DiscriminatorValue("B")
public class EntityBDescendants extends EntityADescendants {
    @Descendants
    private List<EntityCDescendants> entitiesC = Lists.newArrayList();

    public void addEntityC(EntityCDescendants entityCDescendants){
        this.entitiesC.add(entityCDescendants);
    }

    public List<EntityCDescendants> getEntitiesC() {
        return entitiesC;
    }
}
