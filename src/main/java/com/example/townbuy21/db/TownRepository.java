package com.example.townbuy21.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TownRepository {
    private final Connection connection;

    public TownRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveTown(String name, String mayor, boolean buyable, double price) throws SQLException {
        String sql = "REPLACE INTO towns(name, mayor, buyable, price) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, mayor);
            ps.setBoolean(3, buyable);
            ps.setDouble(4, price);
            ps.executeUpdate();
        }
    }

    public double getPrice(String name) {
        String sql = "SELECT price FROM towns WHERE name=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        } catch (SQLException ignore) {}
        return 0.0;
    }

    public boolean isBuyable(String name) {
        String sql = "SELECT buyable FROM towns WHERE name=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("buyable");
                }
            }
        } catch (SQLException ignore) {}
        return false;
    }

    public List<String> getBuyableTowns() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM towns WHERE buyable=true";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException ignore) {}
        return list;
    }

    /**
     * Checks whether the town exists in the database.
     *
     * @param name Name of the town
     * @return true if the town exists
     */
    public boolean exists(String name) {
        String sql = "SELECT 1 FROM towns WHERE name=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ignore) {}
        return false;
    }
}
