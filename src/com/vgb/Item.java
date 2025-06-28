package com.vgb;

import java.util.UUID;

public abstract class Item {
    private UUID uuid;
    private String name;

    public Item(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

 
    public abstract double calculateSubtotal();
    public abstract double calculateTax();

    @Override
    public String toString() {
        return "Item{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

