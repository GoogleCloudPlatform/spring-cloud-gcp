package com.example;

public class TraderDetails {
    private String address;

    private Long yoe;

    private boolean active;

    public TraderDetails(String address, Long yoe, boolean active) {
        this.address = address;
        this.yoe = yoe;
        this.active = active;
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
