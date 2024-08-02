package ru.tolstov.translator.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    public static Connection getConnection() throws SQLException {
        final String url = "jdbc:postgresql://localhost:5432/translator";
        Properties props = new Properties();
        props.put("user", "daniel");
        props.put("password", "secret");
        return DriverManager.getConnection(url, props);
    }
}
