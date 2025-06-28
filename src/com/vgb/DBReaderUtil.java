package com.vgb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * DBReaderUtil is a utility class that handles all database read operations 
 * for the invoice system. It connects to the MySQL database, retrieves data, 
 * and constructs Java objects such as Person, Company, Item, Invoice, and InvoiceItem.
 *
 * Author: Shelton Bumhe
 */
public class DBReaderUtil {

    /**
     * Reads all Person records and their associated emails from the database.
     *
     * @return a map of person UUIDs to Person objects
     */
    public static Map<UUID, Person> readPersons() {
        Map<UUID, Person> persons = new HashMap<>();
        try (Connection conn = ConnectionFactory.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Person");

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("person_uuid"));
                Person p = new Person(uuid, rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("phone"), new ArrayList<>());
                persons.put(uuid, p);
            }

            rs = stmt.executeQuery("SELECT * FROM Email");
            while (rs.next()) {
                UUID personUUID = UUID.fromString(rs.getString("person_uuid"));
                Person p = persons.get(personUUID);
                if (p != null) {
                    p.getEmails().add(rs.getString("email_address"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }

    /**
     * Reads all Company records, joining with related Person and Address details.
     *
     * @param persons a map of person UUIDs to Person objects
     * @return a map of company UUIDs to Company objects
     */
    public static Map<UUID, Company> readCompanies(Map<UUID, Person> persons) {
        Map<UUID, Company> companies = new HashMap<>();
        String sql = """
            SELECT c.company_uuid, c.company_name, p.person_uuid, a.street, a.city, a.state, a.postal_code
            FROM Company c
            JOIN Person p ON c.contactId = p.personId
            JOIN Address a ON c.addressId = a.addressId
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("company_uuid"));
                Person contact = persons.get(UUID.fromString(rs.getString("person_uuid")));
                Address address = new Address(rs.getString("street"), rs.getString("city"),
                        rs.getString("state"), rs.getString("postal_code"));
                companies.put(uuid, new Company(uuid, contact, rs.getString("company_name"), address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    /**
     * Reads all Item records from the database and maps them to specific item types.
     *
     * @param companies a map of company UUIDs to Company objects (for contracts)
     * @return a map of item UUIDs to Item objects
     */
    public static Map<UUID, Item> readItems(Map<UUID, Company> companies) {
        Map<UUID, Item> items = new HashMap<>();
        String sql = "SELECT * FROM Item";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("item_uuid"));
                String type = rs.getString("type");
                String name = rs.getString("name");

                if ("E".equals(type)) {
                    String modelNo = rs.getString("model_no");
                    double retailPrice = rs.getDouble("retail_price");
                    items.put(uuid, new Equipment(uuid, name, modelNo, retailPrice));

                } else if ("M".equals(type)) {
                    String unit = rs.getString("weight_desc");
                    double unitCost = rs.getDouble("unit_cost");
                    items.put(uuid, new Material(uuid, name, unit, unitCost));

                } else if ("C".equals(type)) {
                    double fee = rs.getDouble("contract_fee");
                    Company subcontractor = companies.values().stream().findFirst().orElse(null);
                    items.put(uuid, new Contract(uuid, name, subcontractor, fee));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Loads all invoices from the database, including associated companies and salespersons.
     *
     * @param persons a map of person UUIDs to Person objects
     * @param companies a map of company UUIDs to Company objects
     * @return a map of invoice UUIDs to Invoice objects
     */
    public static Map<UUID, Invoice> loadInvoices(Map<UUID, Person> persons, Map<UUID, Company> companies) {
        Map<UUID, Invoice> invoices = new HashMap<>();
        String sql = """
            SELECT i.invoice_uuid, i.invoice_date, c.company_uuid, p.person_uuid
            FROM Invoice i
            JOIN Company c ON i.companyId = c.companyId
            JOIN Person p ON i.personId = p.personId
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("invoice_uuid"));
                Company company = companies.get(UUID.fromString(rs.getString("company_uuid")));
                Person salesperson = persons.get(UUID.fromString(rs.getString("person_uuid")));
                LocalDate date = rs.getDate("invoice_date").toLocalDate();
                invoices.put(uuid, new Invoice(uuid, company, salesperson, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /**
     * Loads and associates InvoiceItems (with type-specific details) into their corresponding Invoices.
     *
     * @param invoices a map of invoice UUIDs to Invoice objects
     * @param items a map of item UUIDs to Item objects
     */
    public static void loadInvoiceItems(Map<UUID, Invoice> invoices, Map<UUID, Item> items) {
        String sql = """
            SELECT ii.*, i.item_uuid, inv.invoice_uuid
            FROM InvoiceItems ii
            JOIN Item i ON ii.item_uuid = i.item_uuid
            JOIN Invoice inv ON ii.invoiceId = inv.invoiceId
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID invoiceUUID = UUID.fromString(rs.getString("invoice_uuid"));
                UUID itemUUID = UUID.fromString(rs.getString("item_uuid"));
                String purchaseType = rs.getString("purchase_type");

                Invoice invoice = invoices.get(invoiceUUID);
                Item baseItem = items.get(itemUUID);
                if (invoice == null || baseItem == null) {
                    continue;
                }

                InvoiceItem invoiceItem = new InvoiceItem(invoiceUUID, baseItem);

                if (baseItem instanceof Material) {
                    int quantity = rs.getInt("quantity");
                    invoiceItem.setQuantity(quantity);

                } else if (baseItem instanceof Contract) {
                    // Contract already has amount from Item table; no need to set

                } else if (baseItem instanceof Equipment equipment) {
                    if ("L".equals(purchaseType)) {
                        LocalDate start = Optional.ofNullable(rs.getDate("lease_start_date"))
                                .map(Date::toLocalDate).orElse(null);
                        LocalDate end = Optional.ofNullable(rs.getDate("lease_end_date"))
                                .map(Date::toLocalDate).orElse(null);
                        Lease lease = new Lease(itemUUID, equipment.getName(), equipment.getModelNumber(),
                                equipment.getRetailPrice(), start, end);
                        invoiceItem = new InvoiceItem(invoiceUUID, lease);
                        invoiceItem.setLeaseDates(start, end);

                    } else if ("R".equals(purchaseType)) {
                        int hours = rs.getInt("rental_hours");
                        Rental rental = new Rental(itemUUID, equipment.getName(), equipment.getModelNumber(),
                                equipment.getRetailPrice(), hours);
                        invoiceItem = new InvoiceItem(invoiceUUID, rental);
                        invoiceItem.setRentalHours(hours);
                    } else {
                        invoiceItem = new InvoiceItem(invoiceUUID, equipment);
                    }
                }

                invoice.addItem(invoiceItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
