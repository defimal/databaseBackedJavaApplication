package com.vgb;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents an item entry in an invoice, holding additional details 
 * specific to how the item was billed (e.g., quantity, rental hours, 
 * contract amount, lease dates).
 * 
 * This class wraps a general Item and provides invoice-specific values 
 * such as how many were purchased, or specific financial calculations 
 * for that invoice context.
 * 
 * Author: Shelton Bumhe
 */
public class InvoiceItem {

    // Unique ID of the invoice this item belongs to
    private UUID invoiceId;

    // Core item (Equipment, Material, Contract, Lease, or Rental)
    private Item item;

    // For Material: quantity purchased
    private int quantity;

    // For Contract: agreed contract amount
    private double contractAmount;

    // For Rental: number of hours rented
    private double rentalHours;

    // For Lease: start and end dates
    private LocalDate leaseStart;
    private LocalDate leaseEnd;

    /**
     * Creates a new InvoiceItem.
     * 
     * @param invoiceId the UUID of the invoice
     * @param item the underlying Item object
     */
    public InvoiceItem(UUID invoiceId, Item item) {
        this.invoiceId = invoiceId;
        this.item = item;
    }

    // ----------- Setters -----------

    /**
     * Sets the quantity for a material item.
     * @param quantity the number of units
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the contract amount for a contract item.
     * @param contractAmount the agreed contract amount
     */
    public void setContractAmount(double contractAmount) {
        this.contractAmount = contractAmount;
    }

    /**
     * Sets the rental hours for a rental item.
     * @param rentalHours number of hours rented
     */
    public void setRentalHours(double rentalHours) {
        this.rentalHours = rentalHours;
    }

    /**
     * Sets the lease start and end dates for a lease item.
     * @param start start date
     * @param end end date
     */
    public void setLeaseDates(LocalDate start, LocalDate end) {
        this.leaseStart = start;
        this.leaseEnd = end;
    }

    // ----------- Getters -----------

    /**
     * @return the invoice UUID this item belongs to
     */
    public UUID getInvoiceId() {
        return invoiceId;
    }

    /**
     * @return the associated core Item (Equipment, Material, etc.)
     */
    public Item getItem() {
        return item;
    }

    /**
     * @return quantity for material items
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return contract amount for contract items
     */
    public double getContractAmount() {
        return contractAmount;
    }

    /**
     * @return rental hours for rental items
     */
    public double getRentalHours() {
        return rentalHours;
    }

    /**
     * @return lease start date
     */
    public LocalDate getLeaseStart() {
        return leaseStart;
    }

    /**
     * @return lease end date
     */
    public LocalDate getLeaseEnd() {
        return leaseEnd;
    }

    // ----------- Financial Calculations -----------

    /**
     * Calculates the subtotal (before tax) for this invoice item, 
     * based on its type.
     * 
     * @return subtotal in dollars
     */
    public double getSubtotal() {
        if (item instanceof Material m) {
            return roundToTwo(m.getCostPerUnit() * quantity);
        }
        if (item instanceof Contract c) {
            return roundToTwo(c.getContractAmount());
        }
        if (item instanceof Lease l) {
            return l.calculateSubtotal();
        }
        if (item instanceof Rental r) {
            return r.calculateSubtotal();
        }
        if (item instanceof Equipment e) {
            return e.calculateSubtotal();
        }
        return 0.0;
    }

    /**
     * Calculates the tax for this invoice item, 
     * based on its type and applicable tax rules.
     * 
     * @return tax amount in dollars
     */
    public double getTax() {
        if (item instanceof Material m) {
            return roundToTwo(getSubtotal() * 0.0715);  // Use Material tax rate
        }
        if (item instanceof Contract) {
            return 0.0;  // Contracts are tax-exempt
        }
        if (item instanceof Lease l) {
            return l.calculateTax();
        }
        if (item instanceof Rental r) {
            return r.calculateTax();
        }
        if (item instanceof Equipment e) {
            return e.calculateTax();
        }
        return 0.0;
    }

    /**
     * Utility method to round values to two decimal places.
     * 
     * @param value the raw value
     * @return value rounded to two decimal places
     */
    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
