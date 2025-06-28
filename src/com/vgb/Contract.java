package com.vgb;

import java.util.UUID;

public class Contract extends Item {

    private Company company;
    private double contractAmount; // Store it here directly

    public Contract(UUID uuid, String name, Company company, double contractAmount) {
        super(uuid, name);
        this.company = company;
        this.contractAmount = contractAmount;
    }

    public Company getCompany() {
        return company;
    }

    public double getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(double contractAmount) {
        this.contractAmount = contractAmount;
    }

    /**
     * Subtotal is just the contract amount.
     */
    @Override
    public double calculateSubtotal() {
        return roundToTwo(contractAmount);
    }

    /**
     * No tax on contracts.
     */
    @Override
    public double calculateTax() {
        return 0.00;
    }

    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "uuid=" + getUuid() +
                ", name='" + getName() + '\'' +
                ", subcontractor=" + company.getName() +
                ", contractAmount=$" + contractAmount +
                '}';
    }
}
