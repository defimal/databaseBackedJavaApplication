package com.vgb;

import java.util.UUID;

public class Equipment extends Item {

    private String modelNumber;
    private double retailPrice;
    private String purchaseType = "P"; // Default is 'P' for purchase

    private static final double PURCHASE_TAX_RATE = 0.0525;

    public Equipment(UUID uuid, String name, String modelNumber, double retailPrice) {
        super(uuid, name);
        this.modelNumber = modelNumber;
        this.retailPrice = retailPrice;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    @Override
    public double calculateSubtotal() {
        if ("P".equals(purchaseType)) {
            return roundToTwo(retailPrice);
        }
        return 0.0;
    }

    @Override
    public double calculateTax() {
        if ("P".equals(purchaseType)) {
            return roundToTwo(calculateSubtotal() * PURCHASE_TAX_RATE);
        }
        return 0.0;
    }

    protected double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return super.toString() +
               "\nModel: " + modelNumber +
               "\nRetail: $" + retailPrice;
    }
}
