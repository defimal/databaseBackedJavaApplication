package com.vgb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Invoice {
    private UUID invoiceId;
    private Company company;
    private Person salesperson;
    private LocalDate date;
    private List<InvoiceItem> items;

    public Invoice(UUID invoiceId, Company company, Person salesperson, LocalDate date) {
        this.invoiceId = invoiceId;
        this.company = company;
        this.salesperson = salesperson;
        this.date = date;
        this.items = new ArrayList<>();
    }

    // Add an item to the invoice
    public void addItem(InvoiceItem item) {
        items.add(item);
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public Company getCompany() {
        return company;
    }

    public Person getSalesperson() {
        return salesperson;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (InvoiceItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double getTotalTax() {
        double tax = 0.0;
        for (InvoiceItem item : items) {
            tax += item.getTax();
        }
        return tax;
    }

    public double getTotalAmount() {
        return getSubtotal() + getTotalTax();
    }
}
