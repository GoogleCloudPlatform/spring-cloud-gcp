package com.example;

import com.google.cloud.spring.data.spanner.core.mapping.NotMapped;

import java.util.Objects;

public class TraderDetails {
    private String address;

    private Long yoe;

    private Boolean active;

    public TraderDetails(String address, Long yoe, Boolean active) {
        this.address = address;
        this.yoe = yoe;
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraderDetails that = (TraderDetails) o;
        return active == that.active && Objects.equals(address, that.address) && Objects.equals(yoe, that.yoe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, yoe, active);
    }

    @Override
    public String toString() {
        return "TraderDetails{" +
                "address='" + address + '\'' +
                ", yoe=" + yoe +
                ", active=" + active +
                '}';
    }
}
