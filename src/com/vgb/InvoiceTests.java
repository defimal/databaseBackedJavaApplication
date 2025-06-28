package com.vgb;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

public class InvoiceTests {

    public static final double TOLERANCE = 0.001;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice(UUID.randomUUID(), null, null, null); // Placeholder invoice
    }

    @Test
    public void testLeaseAndRentalInvoice() {
        // Create Lease and Rental objects with proper attributes
        Lease lease = new Lease(UUID.randomUUID(), "Office Space", "12345", 6000, LocalDate.now(), LocalDate.now().plusMonths(12));
        Rental rental = new Rental(UUID.randomUUID(), "Car", "67890", 150, 20);

        // Wrap in InvoiceItems and add to invoice
        InvoiceItem leaseItem = new InvoiceItem(invoice.getInvoiceId(), lease);
        InvoiceItem rentalItem = new InvoiceItem(invoice.getInvoiceId(), rental);

        invoice.addItem(leaseItem);
        invoice.addItem(rentalItem);

        // Test actual values using class methods
        double leaseSubtotal = lease.calculateSubtotal();
        double leaseTax = lease.calculateTax();

        double rentalSubtotal = rental.calculateSubtotal();
        double rentalTax = rental.calculateTax();

        double expectedSubtotal = leaseSubtotal + rentalSubtotal;
        double expectedTax = leaseTax + rentalTax;
        double expectedTotal = expectedSubtotal + expectedTax;

        assertEquals(expectedSubtotal, invoice.getSubtotal(), TOLERANCE);
        assertEquals(expectedTax, invoice.getTotalTax(), TOLERANCE);
        assertEquals(expectedTotal, invoice.getTotalAmount(), TOLERANCE);
    }

    @Test
    public void testAnotherLeaseAndRentalInvoice() {
        Lease lease = new Lease(UUID.randomUUID(), "Warehouse", "Model789", 7200, LocalDate.now(), LocalDate.now().plusMonths(6));
        Rental rental = new Rental(UUID.randomUUID(), "Bike", "Model101", 200, 15);

        InvoiceItem leaseItem = new InvoiceItem(invoice.getInvoiceId(), lease);
        InvoiceItem rentalItem = new InvoiceItem(invoice.getInvoiceId(), rental);

        invoice.addItem(leaseItem);
        invoice.addItem(rentalItem);

        double expectedSubtotal = lease.calculateSubtotal() + rental.calculateSubtotal();
        double expectedTax = lease.calculateTax() + rental.calculateTax();
        double expectedTotal = expectedSubtotal + expectedTax;

        assertEquals(expectedSubtotal, invoice.getSubtotal(), TOLERANCE);
        assertEquals(expectedTax, invoice.getTotalTax(), TOLERANCE);
        assertEquals(expectedTotal, invoice.getTotalAmount(), TOLERANCE);
    }
}
