package com.example.todofx.util;

import com.example.todofx.ui.CustomDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/todo_fx";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                CustomDialog.showException(new SQLException("Unable to connect to the database. Please check your database settings.", e));
                throw e;
            }
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                CustomDialog.showException(new SQLException("Error occurred while closing the database connection.", e));
            } finally {
                connection = null;
            }
        }
    }
}