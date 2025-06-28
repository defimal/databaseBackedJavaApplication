package com.vgb;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.ArrayList;

/**
 * CSVReaderUtil is a utility class for reading data from CSV files and converting 
 * them into in-memory Java objects (Person, Company, Item, Invoice, InvoiceItem).
 *
 * This class helps load the initial data into the system before inserting into a database.
 */
public class CSVReaderUtil {

    /**
     * Reads Person records from the given CSV file.
     * 
     * @param filePath Path to the Persons CSV file.
     * @return Map of UUID to Person objects.
     */
    public static Map<UUID, Person> readPersons(String filePath) {
        Map<UUID, Person> persons = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine();  // Skip header
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(",", -1);
                if (tokens.length < 4 || tokens[0].isEmpty()) continue;

                UUID uuid = UUID.fromString(tokens[0]);
                String firstName = tokens[1];
                String lastName = tokens[2];
                String phone = tokens[3];

                List<String> emails = new ArrayList<>();
                for (int i = 4; i < tokens.length; i++) {
                    if (!tokens[i].isEmpty()) {
                        emails.add(tokens[i]);
                    }
                }

                persons.put(uuid, new Person(uuid, firstName, lastName, phone, emails));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Persons file not found: " + e.getMessage());
        }
        return persons;
    }

    /**
     * Reads Company records from the given CSV file.
     * 
     * @param filePath Path to the Companies CSV file.
     * @param persons  Map of loaded Person objects for linking company contacts.
     * @return Map of UUID to Company objects.
     */
    public static Map<UUID, Company> readCompanies(String filePath, Map<UUID, Person> persons) {
        Map<UUID, Company> companies = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine();  // Skip header
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(",", -1);
                if (tokens.length < 7 || tokens[0].isEmpty()) continue;

                UUID uuid = UUID.fromString(tokens[0]);
                UUID contactId = UUID.fromString(tokens[1]);
                String name = tokens[2];
                String street = tokens[3];
                String city = tokens[4];
                String state = tokens[5];
                String zip = tokens[6];

                Person contact = persons.get(contactId);
                if (contact == null) {
                    continue;
                }

                Address address = new Address(street, city, state, zip);
                companies.put(uuid, new Company(uuid, contact, name, address));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Companies file not found: " + e.getMessage());
        }
        return companies;
    }

    /**
     * Reads Item records (Equipment, Material, Contract) from the given CSV file.
     * 
     * @param filePath Path to the Items CSV file.
     * @param companies Map of loaded Company objects for linking Contracts.
     * @return Map of UUID to Item objects.
     */
    public static Map<UUID, Item> readItems(String filePath, Map<UUID, Company> companies) {
        Map<UUID, Item> items = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine();  // Skip header
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(",", -1);
                if (tokens.length < 4 || tokens[0].isEmpty()) continue;

                UUID uuid = UUID.fromString(tokens[0]);
                char type = tokens[1].charAt(0);
                String name = tokens[2];

                switch (type) {
                    case 'E': {
                        String modelNumber = tokens[3];
                        double retailPrice = Double.parseDouble(tokens[4]);
                        items.put(uuid, new Equipment(uuid, name, modelNumber, retailPrice));
                        break;
                    }
                    case 'M': {
                        String unit = tokens[3];
                        double costPerUnit = Double.parseDouble(tokens[4]);
                        items.put(uuid, new Material(uuid, name, unit, costPerUnit));
                        break;
                    }
                    case 'C': {
                        UUID companyUuid = UUID.fromString(tokens[3]);
                        Company company = companies.get(companyUuid);
                        if (company == null) {
                            continue;
                        }
                        double defaultContractAmount = 10500.0;
                        items.put(uuid, new Contract(uuid, name, company, defaultContractAmount));
                        break;
                    }
                    default:
                        // Unknown item type, skip
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Items file not found: " + e.getMessage());
        }
        return items;
    }

    /**
     * Reads InvoiceItem records and attaches them to the corresponding Invoices.
     * 
     * @param filePath Path to the InvoiceItems CSV file.
     * @param invoices Map of loaded Invoice objects.
     * @param items    Map of loaded Item objects.
     */
    public static void readInvoiceItems(String filePath, Map<UUID, Invoice> invoices, Map<UUID, Item> items) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine();  // Skip header
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(",", -1);
                if (tokens.length < 3 || tokens[0].isEmpty() || tokens[1].isEmpty()) continue;

                UUID invoiceId = UUID.fromString(tokens[0]);
                UUID itemId = UUID.fromString(tokens[1]);

                Invoice invoice = invoices.get(invoiceId);
                Item item = items.get(itemId);
                if (invoice == null || item == null) {
                    continue;
                }

                InvoiceItem invoiceItem = new InvoiceItem(invoiceId, item);

                if (item instanceof Material) {
                    int quantity = Integer.parseInt(tokens[2]);
                    invoiceItem.setQuantity(quantity);

                } else if (item instanceof Contract contract) {
                    double amount = Double.parseDouble(tokens[2]);
                    invoiceItem.setContractAmount(amount);
                    contract.setContractAmount(amount);

                } else if (item instanceof Equipment equipment) {
                    String usageType = tokens[2];

                    if ("L".equalsIgnoreCase(usageType)) {
                        LocalDate start = LocalDate.parse(tokens[3]);
                        LocalDate end = LocalDate.parse(tokens[4]);
                        Lease lease = new Lease(itemId, equipment.getName(), equipment.getModelNumber(), equipment.getRetailPrice(), start, end);
                        invoiceItem = new InvoiceItem(invoiceId, lease);
                        invoiceItem.setLeaseDates(start, end);

                    } else if ("R".equalsIgnoreCase(usageType)) {
                        double hours = Double.parseDouble(tokens[3]);
                        Rental rental = new Rental(itemId, equipment.getName(), equipment.getModelNumber(), equipment.getRetailPrice(), hours);
                        invoiceItem = new InvoiceItem(invoiceId, rental);
                        invoiceItem.setRentalHours(hours);
                    }
                }

                if (invoiceItem != null) {
                    invoice.addItem(invoiceItem);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("InvoiceItems file not found: " + e.getMessage());
        }
    }

    /**
     * Reads Invoice records from the given CSV file.
     * 
     * @param filePath  Path to the Invoices CSV file.
     * @param persons   Map of loaded Person objects for linking salespersons.
     * @param companies Map of loaded Company objects for linking customers.
     * @return Map of UUID to Invoice objects.
     */
    public static Map<UUID, Invoice> loadInvoices(String filePath, Map<UUID, Person> persons, Map<UUID, Company> companies) {
        Map<UUID, Invoice> invoices = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine();  // Skip header
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(",", -1);
                if (tokens.length < 4 || tokens[0].isEmpty()) continue;

                UUID invoiceId = UUID.fromString(tokens[0]);
                UUID customerId = UUID.fromString(tokens[1]);
                UUID salespersonId = UUID.fromString(tokens[2]);
                LocalDate date = LocalDate.parse(tokens[3]);

                Company customer = companies.get(customerId);
                Person salesperson = persons.get(salespersonId);
                if (customer != null && salesperson != null) {
                    invoices.put(invoiceId, new Invoice(invoiceId, customer, salesperson, date));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Invoices file not found: " + e.getMessage());
        }
        return invoices;
    }
}
