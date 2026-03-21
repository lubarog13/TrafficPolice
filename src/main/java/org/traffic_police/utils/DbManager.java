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
                    drivers.add(new Driver(resultSet.getString("licenseNumber"), resultSet.getString("surname"), resultSet.getString("name"), resultSet.getString("middleName"), resultSet.getString("address"), resultSet.getString("other_info")));
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

    public static List<String> getDriversInDate(String date) throws SQLException {
        List<String> drivers = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("select concat(surname, ' ', name, ' ', middleName) as fio from Driver inner join Car on Car.owner=Driver.licenseNumber inner join Violation on Car.regNumber = Violation.car where DATE(violationDate) = ?");        
                statement.setString(1, date);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    drivers.add(resultSet.getString("fio"));
                }
            }
        }
        return drivers;
    }

    public static List<Car> getCars() throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Car");
                while (resultSet.next()) {
                    cars.add(new Car(resultSet.getString("regNumber"), resultSet.getString("brand"), resultSet.getString("model"), resultSet.getString("color"), resultSet.getString("owner")));
                }
            }
        }
        return cars;
    }

    public static List<ViolationType> getViolationTypes() throws SQLException {
        List<ViolationType> violationTypes = new ArrayList<>();
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM ViolationTypes");
                while (resultSet.next()) {
                    violationTypes.add(new ViolationType(resultSet.getString("code"), resultSet.getString("name"), resultSet.getDouble("price")));
                }
            }
        }
        return violationTypes;
    }

    public static void addDriver(Driver driver) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                if (driver.getMiddleName() == null) {
                    driver.setMiddleName("");
                }
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Driver (licenseNumber, surname, name, middleName, address, other_info) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, driver.getLicenseNumber());
                statement.setString(2, driver.getSurname());
                statement.setString(3, driver.getName());
                statement.setString(4, driver.getMiddleName());
                statement.setString(5, driver.getAddress());
                statement.setString(6, driver.getOtherInfo());
                statement.executeUpdate();
            }
        }
    }

    public static void addCar(Car car) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Car (regNumber, brand, model, color, owner) VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, car.getRegNumber());
                statement.setString(2, car.getBrand());
                statement.setString(3, car.getModel());
                statement.setString(4, car.getColor());
                statement.setString(5, car.getOwner());
                statement.executeUpdate();
            }
        }
    }

    public static void addViolation(Violation violation) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Violation (protocolNumber, violationDate, place, car, code, other_driver_info) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, violation.getProtocolNumber());
                statement.setString(2, violation.getViolationDate());
                statement.setString(3, violation.getPlace());
                statement.setString(4, violation.getCar());
                statement.setString(5, violation.getCode());
                statement.setString(6, violation.getOtherDriverInfo());
                statement.executeUpdate();
            }
        }
    }

    public static void updateCar(Car car) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("UPDATE Car SET brand = ?, model = ?, color = ?, owner = ? WHERE regNumber = ?");
                statement.setString(1, car.getBrand());
                statement.setString(2, car.getModel());
                statement.setString(3, car.getColor());
                statement.setString(4, car.getOwner());
                statement.setString(5, car.getRegNumber());
                statement.executeUpdate();
            }
        }
    }

    public static void updateViolation(Violation violation) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("UPDATE Violation SET protocolNumber = ?, violationDate = ?, place = ?, car = ?, code = ?, other_driver_info = ? WHERE protocolNumber = ?");
                statement.setString(1, violation.getProtocolNumber());
                statement.setString(2, violation.getViolationDate());
                statement.setString(3, violation.getPlace());
                statement.setString(4, violation.getCar());
                statement.setString(5, violation.getCode());
                statement.setString(6, violation.getOtherDriverInfo());
                statement.setString(7, violation.getProtocolNumber());
                statement.executeUpdate();
            }
        }
    }

    public static void updateDriver(Driver driver) throws SQLException {
        try (Connection connection = Main.getConnection()) {
            if (connection != null) {
                assert connection != null;
                PreparedStatement statement = connection.prepareStatement("UPDATE Driver SET surname = ?, name = ?, middleName = ?, address = ?, other_info = ? WHERE licenseNumber = ?");
                statement.setString(1, driver.getSurname());
                statement.setString(2, driver.getName());
                statement.setString(3, driver.getMiddleName());
                statement.setString(4, driver.getAddress());
                statement.setString(5, driver.getLicenseNumber());
                statement.setString(6, driver.getOtherInfo());
                statement.executeUpdate();
            }
        }
    }
}

