package com.vgb;

import java.util.UUID;

public class Company {
    private UUID uuid;
    private Person customer; // This is the actual person linked to the company (the buyer)
    private String name;
    private Address address;

    public Company(UUID uuid, Person customer, String name, Address address) {
        this.uuid = uuid;
        this.customer = customer;
        this.name = name;
        this.address = address;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Person getCustomer() {
        return customer;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Company{" +
                "uuid='" + uuid + '\'' +
                ", customer=" + customer +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}

