package com.example;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.NotMapped;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.spanner.v1.TypeCode;

@Table(name = "test")
public class TestEntity {
    @PrimaryKey
    @Column(name = "id")
    private String stringId;

    @Column(name = "name")
    private String name;

//    @NotMapped
     @Column(spannerType = TypeCode.JSON)
    private TraderDetails details;

    public TestEntity(String stringId, String name, TraderDetails details) {
        this.stringId = stringId;
        this.name = name;
        this.details = details;
    }

//    public TestEntity(String stringId, String name) {
//        this.stringId = stringId;
//        this.name = name;
//    }

    public String getStringId() {
        return stringId;
    }

    public String getName() {
        return name;
    }

    public TraderDetails getDetails() {
        return details;
    }
}
