package com.example.townbuy21.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

public class MysqlManager {
    private final JavaPlugin plugin;
    private Connection connection;

    public MysqlManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        String host = plugin.getConfig().getString("mysql.host", "localhost");
        int port = plugin.getConfig().getInt("mysql.port", 3306);
        String database = plugin.getConfig().getString("mysql.database", "townbuy");
        String user = plugin.getConfig().getString("mysql.user", "root");
        String password = plugin.getConfig().getString("mysql.password", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
        connection = DriverManager.getConnection(url, user, password);
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS towns(" +
                    "name VARCHAR(255) PRIMARY KEY," +
                    "mayor VARCHAR(255)," +
                    "buyable BOOLEAN," +
                    "price DOUBLE)");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {}
        }
    }
}
