package ru.tolstov.translator.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// TODO url in separate config file
@Configuration
public class DatabaseConfig {
    public static Connection getConnection() throws SQLException {
        final String url = "jdbc:postgresql://localhost:5432/translator";
        Properties props = new Properties();
        props.setProperty("user", "daniel");
        props.setProperty("password", "secret");

        return DriverManager.getConnection(url, props);
    }
}
