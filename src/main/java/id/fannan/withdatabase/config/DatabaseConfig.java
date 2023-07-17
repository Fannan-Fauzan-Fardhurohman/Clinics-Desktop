package id.fannan.withdatabase.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String jdbc_driver;

    public DatabaseConfig() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            jdbc_driver = "com.mysql.cj.jdbc.Driver";
            url = "jdbc:mysql://localhost:3306/clinic_db";
            username = "root";
            password = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(jdbc_driver);
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    public void testConnection() {
        try {
            Connection connection = getConnection();
            System.out.println("Konek ke database berhasil");
            connection.close();
        } catch (Exception e) {
            System.out.println("Gagal terhubung ke database : " + e.getMessage());
        }
    }
}
