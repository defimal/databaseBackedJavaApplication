package com.vgb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory class for establishing and closing MySQL database connections.
 * Uses static methods to simplify JDBC access.
 *
 * @author Shelton
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:mysql://nuros.unl.edu/sbumhe2";
    private static final String USER = "sbumhe2";
    private static final String PASS = "Uuxoo9Yeikoh";

    /**
     * Opens and returns a new database connection.
     *
     * @return a JDBC Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Safely closes the database connection.
     *
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
