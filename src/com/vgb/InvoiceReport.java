package com.vgb;

import java.util.Map;
import java.util.UUID;
import java.util.Comparator;

/**
 * Generates and prints summary reports for invoices and companies.
 */
public class InvoiceReport {

    /**
     * @author sheltonbumhe
     * Generates the full invoice report with three sections:
     * - Invoices sorted by total descending
     * - Invoices sorted by customer name
     * - Company summary totals (number of invoices, total amounts)
     */
    public static void generateInvoiceReport(Map<UUID, Invoice> invoiceMap, Map<UUID, Company> allCompaniesMap) {

        // Comparator: sort invoices by total amount descending, then by invoice ID
        Comparator<Invoice> byTotalDesc = (a, b) -> {
            int cmp = Double.compare(b.getTotalAmount(), a.getTotalAmount());
            return (cmp != 0) ? cmp : a.getInvoiceId().compareTo(b.getInvoiceId());
        };

        // Comparator: sort invoices by customer name, then by invoice ID
        Comparator<Invoice> byCustomerName = (a, b) -> {
            int cmp = a.getCompany().getName().compareTo(b.getCompany().getName());
            return (cmp != 0) ? cmp : a.getInvoiceId().compareTo(b.getInvoiceId());
        };

        // Comparator: sort company summaries by total amount ascending, then by company name
        Comparator<CompanySummary> byTotalAmountThenName = (a, b) -> {
            int cmp = Double.compare(a.totalAmount, b.totalAmount);
            if (cmp != 0) return cmp;
            return a.company.getName().compareTo(b.company.getName());
        };

        // Create sorted lists using the above comparators
        SortedList<Invoice> invoicesByTotal = new SortedList<>(byTotalDesc);
        SortedList<Invoice> invoicesByCustomer = new SortedList<>(byCustomerName);
        SortedList<CompanySummary> companySummaries = new SortedList<>(byTotalAmountThenName);

        // Fill sorted invoice lists
        for (Invoice inv : invoiceMap.values()) {
            invoicesByTotal.add(inv);
            invoicesByCustomer.add(inv);
        }

        // Build company summary list (count and total per company)
        for (Company company : allCompaniesMap.values()) {
            double total = 0;
            int count = 0;
            for (Invoice inv : invoiceMap.values()) {
                if (inv.getCompany().getUuid().equals(company.getUuid())) {
                    total += inv.getTotalAmount();
                    count++;
                }
            }
            companySummaries.add(new CompanySummary(company, count, total));
        }

        // ======= Print Invoices by Total =======
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("| Invoices by Total                                                       |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("%-40s %-30s %12s\n", "Invoice", "Customer", "Total");
        for (Invoice inv : invoicesByTotal) {
            System.out.printf("%-40s %-30s $%10.2f\n",
                    inv.getInvoiceId(), inv.getCompany().getName(), inv.getTotalAmount());
        }
        System.out.println("+-------------------------------------------------------------------------+\n");

        // ======= Print Invoices by Customer =======
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("| Invoices by Customer                                                    |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("%-40s %-30s %12s\n", "Invoice", "Customer", "Total");
        for (Invoice inv : invoicesByCustomer) {
            System.out.printf("%-40s %-30s $%10.2f\n",
                    inv.getInvoiceId(), inv.getCompany().getName(), inv.getTotalAmount());
        }
        System.out.println("+-------------------------------------------------------------------------+\n");

        // ======= Print Customer Invoice Totals =======
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("| Customer Invoice Totals                                                 |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("%-30s %18s %15s\n", "Customer", "Number of Invoices", "Total");
        for (CompanySummary summary : companySummaries) {
            System.out.printf("%-30s %18d $%13.2f\n",
                    summary.company.getName(), summary.numInvoices, summary.totalAmount);
        }
        System.out.println("+-------------------------------------------------------------------------+");
    }

    /**
     * Main method to run the invoice report.
     * Loads data using DBReaderUtil and generates the report.
     */
    public static void main(String[] args) {
        try {
            // Load all data from the database
            Map<UUID, Person> persons = DBReaderUtil.readPersons();
            Map<UUID, Company> companies = DBReaderUtil.readCompanies(persons);
            Map<UUID, Item> items = DBReaderUtil.readItems(companies);
            Map<UUID, Invoice> invoices = DBReaderUtil.loadInvoices(persons, companies);
            DBReaderUtil.loadInvoiceItems(invoices, items);

            // Generate the full report
            generateInvoiceReport(invoices, companies);
        } catch (Exception e) {
            System.err.println("An error occurred during report generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper class to hold company summary data: company, number of invoices, total amount.
     */
    private static class CompanySummary {
        Company company;
        int numInvoices;
        double totalAmount;

        public CompanySummary(Company company, int numInvoices, double totalAmount) {
            this.company = company;
            this.numInvoices = numInvoices;
            this.totalAmount = totalAmount;
        }
    }
}
