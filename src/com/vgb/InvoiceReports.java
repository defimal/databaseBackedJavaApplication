package com.vgb;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;


public class InvoiceReports {

    public static void generateInvoiceReport(List<Invoice> invoices, Map<UUID, Company> allCompaniesMap) {
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.println("| Summary Report - By Total                                                              |");
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.printf("%-40s %-30s %10s %12s %12s\n", "Invoice #", "Customer", "Num Items", "Tax", "Total");

        double totalTax = 0, grandTotal = 0;
        int totalItems = 0;

        Map<String, Double> companyTotals = new HashMap<>();
        Map<String, Integer> companyCounts = new HashMap<>();

        // Summary content
        for (Invoice invoice : invoices) {
            double tax = invoice.getTotalTax();
            double total = invoice.getTotalAmount();
            int itemCount = invoice.getItems().size();

            System.out.printf("%-40s %-30s %10d %12s %12s\n",
                    invoice.getInvoiceId(),
                    invoice.getCompany().getName(),
                    itemCount,
                    String.format("$%,10.2f", tax),
                    String.format("$%,10.2f", total));

            totalTax += tax;
            grandTotal += total;
            totalItems += itemCount;

            String company = invoice.getCompany().getName();
            companyTotals.put(company, companyTotals.getOrDefault(company, 0.0) + total);
            companyCounts.put(company, companyCounts.getOrDefault(company, 0) + 1);
        }

        // Totals row
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.printf("%-71s %12s %12s\n\n", "", String.format("$%,10.2f", totalTax), String.format("$%,10.2f", grandTotal));

        // Company Invoice Summary Report
        System.out.println("+----------------------------------------------------------------+");
        System.out.println("| Company Invoice Summary Report                                 |");
        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("%-30s %12s %15s\n", "Company", "# Invoices", "Grand Total");

        List<String> sortedCompanyNames = new ArrayList<>();
        for (Company company : allCompaniesMap.values()) {
            sortedCompanyNames.add(company.getName());
        }
        Collections.sort(sortedCompanyNames);

        for (String companyName : sortedCompanyNames) {
            int count = companyCounts.getOrDefault(companyName, 0);
            double total = companyTotals.getOrDefault(companyName, 0.0);
            System.out.printf("%-30s %12d     $%12.2f\n", companyName, count, total);
        }

        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("%-30s %12d     $%12.2f\n\n", "", invoices.size(), grandTotal);

        // Detailed Invoices
        for (Invoice invoice : invoices) {
            System.out.println("Invoice#  " + invoice.getInvoiceId());
            System.out.println("Date      " + invoice.getDate());

            Person customer = invoice.getCompany().getCustomer();
            System.out.println("Customer:");
            System.out.printf("%s (%s)\n", invoice.getCompany().getName(), customer.getUuid());
            System.out.printf("%s, %s\n", customer.getFirstName(), customer.getLastName());
            System.out.println("[" + String.join(", ", customer.getEmails()) + "]");
            System.out.println(invoice.getCompany().getAddress().getFormattedAddress());

            Person salesperson = invoice.getSalesperson();
            System.out.println("Sales Person:");
            System.out.printf("%s, %s\n", salesperson.getFirstName(), salesperson.getLastName());
            System.out.println("[" + String.join(", ", salesperson.getEmails()) + "]");

            System.out.printf("Items (%d)                                                            Tax       Total\n", invoice.getItems().size());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-                          -=-=-=-=-=- -=-=-=-=-=");

            for (InvoiceItem item : invoice.getItems()) {
                System.out.printf("%s (%s) %s\n",
                        item.getItem().getUuid(),
                        item.getItem().getClass().getSimpleName(),
                        item.getItem().getName());
                System.out.printf("                                                             $%10.2f $%10.2f\n",
                        item.getTax(), item.getSubtotal());
            }

            System.out.println("                                                             -=-=-=-=-=- -=-=-=-=-=");
            System.out.printf("                                                   Subtotals $%10.2f $%10.2f\n",
                    invoice.getTotalTax(),
                    invoice.getSubtotal());
            System.out.printf("                                                 Grand Total           $%10.2f\n\n",
                    invoice.getTotalAmount());
        }
    }

    public static void main(String[] args) {
        String personsFile = "data/Persons.csv";
        String companiesFile = "data/Companies.csv";
        String itemsFile = "data/Items.csv";
        String invoicesFile = "data/Invoices.csv";
        String invoiceItemsFile = "data/InvoiceItems.csv";

        Map<UUID, Person> persons = CSVReaderUtil.readPersons(personsFile);
        Map<UUID, Company> companies = CSVReaderUtil.readCompanies(companiesFile, persons);
        Map<UUID, Item> items = CSVReaderUtil.readItems(itemsFile, companies);
        Map<UUID, Invoice> invoices = CSVReaderUtil.loadInvoices(invoicesFile, persons, companies);
        CSVReaderUtil.readInvoiceItems(invoiceItemsFile, invoices, items);

        generateInvoiceReport(new ArrayList<>(invoices.values()), companies);
    }
}
