package com.vgb;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;


/**
 * @author sheltonbumhe 
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */

public class InvoiceData {
	
	/**
	 * Removes all records from all tables in the database.
	 */

    public static void clearDatabase() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM InvoiceItems");
            stmt.executeUpdate("DELETE FROM Invoice");
            stmt.executeUpdate("DELETE FROM Item");
            stmt.executeUpdate("DELETE FROM Company");
            stmt.executeUpdate("DELETE FROM Email");
            stmt.executeUpdate("DELETE FROM Person");
            stmt.executeUpdate("DELETE FROM Address");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
	 * Method to add a person record to the database with the provided data.
	 *
	 * @param personUuid
	 * @param firstName
	 * @param lastName
	 * @param phone
	 */

    public static void addPerson(UUID personUuid, String firstName, String lastName, String phone) {
        String sql = "INSERT INTO Person (person_uuid, first_name, last_name, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personUuid.toString());
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personUuid</code>
	 *
	 * @param personUuid
	 * @param email
	 */
    public static void addEmail(UUID personUuid, String email) {
        String sql = "INSERT INTO Email (person_uuid, email_address) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personUuid.toString());
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Adds a company record to the database with the primary contact person identified by the
	 * given code.
	 *
	 * @param companyUuid
	 * @param name
	 * @param contactUuid
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */

    public static void addCompany(UUID companyUuid, UUID contactUuid, String name, String street, String city, String state, String zip) {
        String addressSql = "INSERT INTO Address (street, city, state, postal_code) VALUES (?, ?, ?, ?)";
        String companySql = "INSERT INTO Company (company_uuid, company_name, contactid, addressid) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection()) {
            // Insert address
            int addressId;
            try (PreparedStatement addrPs = conn.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS)) {
                addrPs.setString(1, street);
                addrPs.setString(2, city);
                addrPs.setString(3, state);
                addrPs.setString(4, zip);
                addrPs.executeUpdate();
                ResultSet rs = addrPs.getGeneratedKeys();
                if (rs.next()) addressId = rs.getInt(1);
                else throw new SQLException("Failed to get generated address ID");
            }

            int contactId = getPersonId(contactUuid);

            try (PreparedStatement compPs = conn.prepareStatement(companySql)) {
                compPs.setString(1, companyUuid.toString());
                compPs.setString(2, name);
                compPs.setInt(3, contactId);
                compPs.setInt(4, addressId);
                compPs.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Adds an equipment record to the database of the given values.
	 *
	 * @param equipmentUuid
	 * @param name
	 * @param modelNumber
	 * @param retailPrice
	 */

    public static void addEquipment(UUID equipmentUuid, String name, String modelNumber, double retailPrice) {
        String sql = "INSERT INTO Item (item_uuid, type, name, model_no, retail_price) VALUES (?, 'E', ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentUuid.toString());
            ps.setString(2, name);
            ps.setString(3, modelNumber);
            ps.setDouble(4, retailPrice);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Adds an material record to the database of the given values.
	 *
	 * @param materialUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */

    public static void addMaterial(UUID materialUuid, String name, String unit, double pricePerUnit) {
        String sql = "INSERT INTO Item (item_uuid, type, name, unit, unit_cost) VALUES (?, 'M', ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, materialUuid.toString());
            ps.setString(2, name);
            ps.setString(3, unit);
            ps.setDouble(4, pricePerUnit);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Adds an contract record to the database of the given values.
	 *
	 * @param contractUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
    public static void addContract(UUID contractUuid, String name, UUID servicerUuid) {
        String sql = "INSERT INTO Item (item_uuid, type, name, servicer_uuid) VALUES (?, 'C', ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractUuid.toString());
            ps.setString(2, name);
            ps.setString(3, servicerUuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    /* Adds an Invoice record to the database with the given data.
	 *
	 * @param invoiceUuid
	 * @param customerUuid
	 * @param salesPersonUuid
	 * @param date
	 */
    public static void addInvoice(UUID invoiceUuid, UUID customerUuid, UUID salesPersonUuid, LocalDate date) {
        String sql = "INSERT INTO Invoice (invoice_uuid, companyid, personid, invoice_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceUuid.toString());
            ps.setInt(2, getCompanyId(customerUuid));
            ps.setInt(3, getPersonId(salesPersonUuid));
            ps.setDate(4, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	/**
	 * Adds an Equipment purchase record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 */

    public static void addEquipmentPurchaseToInvoice(UUID invoiceUuid, UUID itemUuid) {
        String sql = "INSERT INTO InvoiceItems (invoiceid, item_uuid, purchase_type) VALUES (?, ?, 'P')";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getInvoiceId(invoiceUuid));
            ps.setString(2, itemUuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
	 * Adds an Equipment lease record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param start
	 * @param end
	 */
    public static void addEquipmentLeaseToInvoice(UUID invoiceUuid, UUID itemUuid, LocalDate start, LocalDate end) {
        String sql = "INSERT INTO InvoiceItems (invoiceid, item_uuid, purchase_type, lease_start_date, lease_end_date) VALUES (?, ?, 'L', ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getInvoiceId(invoiceUuid));
            ps.setString(2, itemUuid.toString());
            ps.setDate(3, Date.valueOf(start));
            ps.setDate(4, Date.valueOf(end));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Adds an Equipment rental record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfHours
	 */


    public static void addEquipmentRentalToInvoice(UUID invoiceUuid, UUID itemUuid, double numberOfHours) {
        String sql = "INSERT INTO InvoiceItems (invoiceid, item_uuid, purchase_type, rental_hours) VALUES (?, ?, 'R', ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getInvoiceId(invoiceUuid));
            ps.setString(2, itemUuid.toString());
            ps.setDouble(3, numberOfHours);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Adds a material record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfUnits
	 */

    public static void addMaterialToInvoice(UUID invoiceUuid, UUID itemUuid, int numberOfUnits) {
    	String sql = "INSERT INTO InvoiceItems (invoiceid, item_uuid, purchase_type, quantity) VALUES (?, ?, 'P', ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getInvoiceId(invoiceUuid));
            ps.setString(2, itemUuid.toString());
            ps.setInt(3, numberOfUnits);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Adds a contract record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param amount
	 */

    public static void addContractToInvoice(UUID invoiceUuid, UUID itemUuid, double amount) {
        String updateItemSql = "UPDATE Item SET contract_fee = ? WHERE item_uuid = ?";
        String insertInvoiceItemSql = "INSERT INTO InvoiceItems (invoiceid, item_uuid, purchase_type) VALUES (?, ?, 'P')";
        try (Connection conn = ConnectionFactory.getConnection()) {
        
            try (PreparedStatement ps = conn.prepareStatement(updateItemSql)) {
                ps.setDouble(1, amount);
                ps.setString(2, itemUuid.toString());
                ps.executeUpdate();
            }

            
            try (PreparedStatement ps = conn.prepareStatement(insertInvoiceItemSql)) {
                ps.setInt(1, getInvoiceId(invoiceUuid));
                ps.setString(2, itemUuid.toString());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Helper method: gets the internal database person ID (integer) using the provided UUID.
     */
    private static int getPersonId(UUID personUuid) throws SQLException {
        String sql = "SELECT personid FROM Person WHERE person_uuid = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("personid");
            else throw new SQLException("Person UUID not found");
        }
    }

    /**
     * Helper method: gets the internal database company ID (integer) using the provided UUID.
     */
    private static int getCompanyId(UUID companyUuid) throws SQLException {
        String sql = "SELECT companyid FROM Company WHERE company_uuid = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, companyUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("companyid");
            else throw new SQLException("Company UUID not found");
        }
    }

    /**
     * Helper method: gets the internal database invoice ID (integer) using the provided UUID.
     */
    private static int getInvoiceId(UUID invoiceUuid) throws SQLException {
        String sql = "SELECT invoiceid FROM Invoice WHERE invoice_uuid = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("invoiceid");
            else throw new SQLException("Invoice UUID not found");
        }
    }
    
}