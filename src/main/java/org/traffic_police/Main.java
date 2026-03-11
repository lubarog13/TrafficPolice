package org.traffic_police;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/traffic_police", "new_cybrid", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Закрывает соединение с базой данных.
     *
     * @param connection соединение с базой данных для закрытия
     * @throws SQLException если произошла ошибка при закрытии соединения
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
