package org.alexshin.util;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConfiguredDataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    static {
        try (InputStream propStream = ConfiguredDataSource.class.getClassLoader().getResourceAsStream("ds.properties");) {
            Properties prop = new Properties();
            prop.load(propStream);

            URL dbResource = ConfiguredDataSource.class.getClassLoader().getResource("CurrencyExchange.db");
            String db_path = Path.of(dbResource.toURI()).toString();
            String db_url_prefix = prop.getProperty("db.url_prefix");
            String db_username = prop.getProperty("db.username");
            String db_password = prop.getProperty("db.password");
            String db_driver = prop.getProperty("db.driverClassName");

            config.setJdbcUrl("jdbc:sqlite:" + db_path);
            config.setDriverClassName("org.sqlite.JDBC");
            config.setUsername("root");
            config.setPassword("root");
            dataSource = new HikariDataSource(config);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    private ConfiguredDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
