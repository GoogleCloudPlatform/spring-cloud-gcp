package com.google.cloud.spring.data.datastore.entities.subclasses.reference;

import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Reference;

@Entity(name = "A")
@DiscriminatorValue("C")
public class EntityCReference extends EntityAReference {
    @Reference
    private EntityBReference entityB;

    public EntityCReference(EntityBReference entityB) {
        this.entityB = entityB;
    }

    public EntityBReference getEntityB() {
        return entityB;
    }
}
