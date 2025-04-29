package com.marketmadness.persistence;

import com.marketmadness.model.Trade;

import java.sql.*;

/**
 * Very small SQLite helper — keeps a single trades table for later analysis.
 * Creates marketmadness.db in the project working directory.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:marketmadness.db";

    public DatabaseManager() {
        // create table once
        try (Connection c = DriverManager.getConnection(URL);
             Statement  st = c.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS trades (
                    id     INTEGER PRIMARY KEY AUTOINCREMENT,
                    side   TEXT,
                    price  REAL,
                    qty    INTEGER,
                    ts     DATETIME DEFAULT CURRENT_TIMESTAMP
                )""");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** insert one trade row */
    public void saveTrade(Trade t) {
        String sql = "INSERT INTO trades (side, price, qty) VALUES (?,?,?)";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, t.side().name());
            ps.setDouble(2, t.price());
            ps.setInt   (3, t.qty());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
