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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Violations");
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocol_number"), resultSet.getString("violation_date"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
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
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violations WHERE DATE(violation_date) BETWEEN ? AND ?");
                statement.setString(1, startDate);
                statement.setString(2, endDate);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocol_number"), resultSet.getString("violation_date"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
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
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violations WHERE car = ?");
                statement.setString(1, car);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocol_number"), resultSet.getString("violation_date"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
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
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Violations inner join Car on Violations.car = Car.reg_number where Car.owner = ?");
                statement.setString(1, driver);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    violations.add(new Violation(resultSet.getString("protocol_number"), resultSet.getString("violation_date"), resultSet.getString("place"), resultSet.getString("car"), resultSet.getString("code"), resultSet.getString("other_driver_info")));
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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Drivers");
                while (resultSet.next()) {
                    drivers.add(new Driver(resultSet.getString("license_number"), resultSet.getString("surname"), resultSet.getString("name"), resultSet.getString("middle_name"), resultSet.getString("address")));
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
                PreparedStatement statement = connection.prepareStatement("SELECT reg_number, brand, model, color, concat(surname, ' ', name, ' ', middle_name) as fio FROM Cars inner join Drivers on Cars.owner = Drivers.license_number WHERE reg_number = ?");
                statement.setString(1, regNumber);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    car = new Car(resultSet.getString("reg_number"), resultSet.getString("brand"), resultSet.getString("model"), resultSet.getString("color"), resultSet.getString("fio"));
                }
            }
        }
        return car;
    }


}
