package com.vgb;

import java.util.UUID;

public class Material extends Item {
    private final String unit;
    private final double costPerUnit;
    private static final double TAX_RATE = 0.0715; // 7.15%

    public Material(UUID uuid, String name, String unit, double costPerUnit) {
        super(uuid, name);
        this.unit = unit;
        this.costPerUnit = costPerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    @Override
    public double calculateSubtotal() {
        // We no longer calculate it here — it's done in InvoiceItem using the quantity.
        return 0.0;
    }

    @Override
    public double calculateTax() {
        return 0.0;
    }

    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Material{" +
                "uuid=" + getUuid() +
                ", name='" + getName() + '\'' +
                ", unit='" + unit + '\'' +
                ", costPerUnit=" + costPerUnit +
                '}';
    }
}
