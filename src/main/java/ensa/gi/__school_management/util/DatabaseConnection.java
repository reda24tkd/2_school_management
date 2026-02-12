package ensa.gi.__school_management.util;

import jakarta.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;
    
    public static Connection getConnection(ServletContext context) throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            String dbUrl = context.getInitParameter("db-url");
            String dbUser = context.getInitParameter("db-user");
            String dbPassword = context.getInitParameter("db-password");
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        return conn;
    }
    
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
