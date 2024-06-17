package org.alexshin.util;


import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class ConfiguredDB {
    Connection connection = null;
    String db_url;
    String db_username;
    String db_password;

    public ConfiguredDB() {

        try (InputStream propStream = ConfiguredDB.class.getClassLoader().getResourceAsStream("db.properties");) {
            Properties prop = new Properties();
            prop.load(propStream);

            db_url = prop.getProperty("db.url");
            db_username = prop.getProperty("db.username");
            db_password = prop.getProperty("db.password");
//            String db_driver = prop.getProperty("db.driverClassName");
//            Class.forName(db_driver);



//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM main.Currencies");
//
//            while (resultSet.next()) {
//                Integer id = resultSet.getInt(1);
//                String code = resultSet.getString(2);
//                String fullName = resultSet.getString(3);
//                String sign = resultSet.getString(4);
//                System.out.println("id: " + id + "\tcode: "+ code + "\t" + sign);
//            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        connection = DriverManager.getConnection(db_url, db_username, db_password);
        return connection;
    }

    public void closeDB() throws SQLException {
        connection.close();
    }

}
