package com.vgb;

import java.util.UUID;

/**
 * Represents a rental equipment item.
 * <p>
 * A Rental is a type of Equipment where the customer pays per hour based on a
 * small percentage (0.1%) of the equipment's retail price.
 * Tax is applied at a fixed rental tax rate.
 * <p>
 * This class extends {@link Equipment} and overrides the financial calculations
 * to handle rental-specific cost and tax.
 *
 * @author Shelton Bumhe 
 */
public class Rental extends Equipment {

    /** Percentage of retail price charged per hour (0.1%). */
    private static final double HOURLY_RATE_PERCENT = 0.001;

    /** Tax rate applied on rental subtotal (4.38%). */
    private static final double TAX_RATE = 0.0438;

    /** Number of hours the equipment is rented. */
    private double rentalHours;

    /**
     * Constructs a Rental instance.
     *
     * @param uuid         Unique identifier of the rental item.
     * @param name         Name of the equipment.
     * @param modelNumber  Model number of the equipment.
     * @param retailPrice  Retail price of the equipment.
     * @param rentalHours  Number of hours the equipment is rented.
     */
    public Rental(UUID uuid, String name, String modelNumber, double retailPrice, double rentalHours) {
        super(uuid, name, modelNumber, retailPrice);
        this.rentalHours = rentalHours;
    }

    /**
     * Gets the number of rental hours.
     *
     * @return Number of hours the equipment was rented.
     */
    public double getRentalHours() {
        return rentalHours;
    }

    /**
     * Calculates the subtotal for the rental.
     * <p>
     * Computed as:
     * hourly rate = retail price * HOURLY_RATE_PERCENT
     * subtotal = hourly rate * rental hours
     *
     * @return Subtotal rounded to two decimal places.
     */
    @Override
    public double calculateSubtotal() {
        double hourlyRate = getRetailPrice() * HOURLY_RATE_PERCENT;
        return roundToTwo(hourlyRate * rentalHours);
    }

    /**
     * Calculates the tax for the rental.
     *
     * @return Tax amount rounded to two decimal places.
     */
    @Override
    public double calculateTax() {
        return roundToTwo(calculateSubtotal() * TAX_RATE);
    }

    /**
     * Utility method to round a value to two decimal places.
     *
     * @param value Input value.
     * @return Rounded value.
     */
    public double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Returns a string representation of the rental item, including hours,
     * subtotal, and tax.
     *
     * @return String summary.
     */
    @Override
    public String toString() {
        return super.toString() +
               "\nHours Rented: " + rentalHours +
               "\nSubtotal: $" + calculateSubtotal() +
               "\nTax: $" + calculateTax();
    }
}
