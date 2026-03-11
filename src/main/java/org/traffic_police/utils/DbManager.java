package org.traffic_police.utils;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.traffic_police.Main;
import org.traffic_police.model.Violation;
import org.traffic_police.model.ViolationType;
import org.traffic_police.model.Driver;
import org.traffic_police.model.Car;

public class DbManager {
    public static List<Violation> getViolations() throws SQLException {
        List<Violation> violations = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Violation");
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocolNumber"), resultSet.getString("violationDate"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
                }
            }
        }
        return violations;
    }

    public static List<Violation> getViolationsBetweenDates(String startDate, String endDate) throws SQLException {
        List<Violation> violations = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violation WHERE DATE(violationDate) BETWEEN ? AND ?");
                statement.setString(1, startDate);
                statement.setString(2, endDate);
                System.out.println(statement.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocolNumber"), resultSet.getString("violationDate"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
                }
            }
        }
        return violations;
    }

    public static List<Violation> getViolationsByCar(String car) throws SQLException {
        List<Violation> violations = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violation WHERE car = ?");
                statement.setString(1, car);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocolNumber"), resultSet.getString("violationDate"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
                }
            }
        }
        return violations;
    }

    public static List<Violation> getViolationsByDriver(String driver) throws SQLException {
        List<Violation> violations = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violation inner join Car on Violation.car = Car.regNumber where Car.owner = ?");
                statement.setString(1, driver);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocolNumber"), resultSet.getString("violationDate"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
                }
            }
        }
        return violations;
    }

    public static List<Driver> getDrivers() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Driver");
                while (resultSet.next()) {
                    drivers.add(new Driver(resultSet.getString("licenseNumber"), resultSet.getString("surname"), resultSet.getString("name"), resultSet.getString("middleName"), resultSet.getString("address")));
                }
            }
        }
        return drivers;
    }

    public static ViolationType getViolationTypeByCode(String code) throws SQLException {
        ViolationType violationType = null;
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM ViolationTypes WHERE code = ?");
                statement.setString(1, code);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violationType = new ViolationType(resultSet.getString("code"), resultSet.getString("name"), resultSet.getDouble("price"));
                }
            }
        }
        return violationType;
    }

    public static Car getCarByRegNumber(String regNumber) throws SQLException {
        Car car = null;
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("SELECT regNumber, brand, model, color, concat(surname, ' ', name, ' ', middleName) as fio FROM Car inner join Driver on Car.owner = Driver.licenseNumber WHERE regNumber = ?");
                statement.setString(1, regNumber);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    car = new Car(resultSet.getString("regNumber"), resultSet.getString("brand"), resultSet.getString("model"), resultSet.getString("color"), resultSet.getString("fio"));
                }
            }
        }
        return car;
    }


}
