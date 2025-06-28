package com.vgb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Lease extends Equipment {

    private static final double MARKUP = 1.5;
    private static final double TAX_THRESHOLD = 12500;
    private static final double FLAT_TAX = 1500;
    private static final int AMORTIZATION_YEARS = 5;
    private static final int DAYS_IN_YEAR = 365;

    private final LocalDate startDate;
    private final LocalDate endDate;

    public Lease(UUID uuid, String name, String modelNumber, double retailPrice, LocalDate startDate, LocalDate endDate) {
        super(uuid, name, modelNumber, retailPrice);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public double calculateSubtotal() {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double years = days / (double) DAYS_IN_YEAR;
        double amortizedFactor = years / AMORTIZATION_YEARS;
        double cost = amortizedFactor * getRetailPrice() * MARKUP;
        return roundToTwo(cost);
    }
    
    
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public double calculateTax() {
        return calculateSubtotal() > TAX_THRESHOLD ? FLAT_TAX : 0.0;
    }

    public double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return super.toString() +
               "\nLease Period: " + startDate + " to " + endDate +
               "\nSubtotal: $" + calculateSubtotal() +
               "\nTax: $" + calculateTax();
    }
}
