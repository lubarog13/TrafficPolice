package org.traffic_police.ui;

import org.traffic_police.utils.BaseForm;
import org.traffic_police.utils.DbManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.traffic_police.model.Car;
import org.traffic_police.model.Driver;
import org.traffic_police.model.Violation;
import org.traffic_police.model.ViolationType;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import java.text.ParseException;
import javax.swing.text.DefaultFormatterFactory;

public class ViolationTable extends BaseForm {
    private JPanel mainPanel;
    private JComboBox<String> driverBox;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JTable violationTable;
    private JButton applyButton;

    private List<Violation> violations = new ArrayList<>();

    private String[] columnNames = {"Номер протокола", "Дата нарушения", "Место нарушения", "Автомобиль", "Код нарушения", "Другая информация"};    private String[][] data = new String[0][];
    private String[][] tableData = new String[0][];

    private List<Driver> drivers = new ArrayList<>();
    private Driver selectedDriver;
    private DefaultTableModel model;


    /**
     * Конструктор базовой формы.
     * Инициализирует окно с заданными параметрами: устанавливает размеры,
     * центрирует окно на экране, задает заголовок и иконку.
     *
     * @param width      ширина окна в пикселях
     * @param height     высота окна в пикселях
     * @param mainWindow если true, закрытие окна завершает приложение,
     *                   иначе окно просто закрывается
     */
    public ViolationTable() {
        super(700, 500, true);
        setContentPane(mainPanel);
        setVisible(true);
        initViolations();
        initTable();
        initDrivers();
        initDriverBox();
        initDateFields();
        applyButton.addActionListener(e -> applyFilters());
    }

    private void initViolations() {
        try {
            violations = DbManager.getViolations();
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initDrivers() {
        try {
            drivers = DbManager.getDrivers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initDriverBox() {
        driverBox.removeAllItems();
        driverBox.addItem("Все водители");
        for (Driver driver : drivers) {
            driverBox.addItem(driver.getFullName());
        }
        driverBox.addActionListener(e -> {
            if (driverBox.getSelectedIndex() == 0) {
                selectedDriver = null;
                try {
                    violations = DbManager.getViolations();
                    updateTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                return;
            } else {
            selectedDriver = drivers.get(driverBox.getSelectedIndex() - 1);
            try {
                violations = DbManager.getViolationsByDriver(selectedDriver.getLicenseNumber());
                updateTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
        });
    }

    private void updateTable() {
        tableData = new String[violations.size()][columnNames.length];
        for (int i = 0; i < violations.size(); i++) {
            tableData[i][0] = violations.get(i).getProtocolNumber();
            tableData[i][1] = violations.get(i).getViolationDate();
            tableData[i][2] = violations.get(i).getPlace();
            tableData[i][3] = violations.get(i).getCar();
            tableData[i][4] = violations.get(i).getCode();
            tableData[i][5] = violations.get(i).getOtherDriverInfo();
        }
        model.setDataVector(tableData, columnNames);
    }

    private void initDateFields() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        startDateField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        endDateField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        endDateField.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(new Date().getTime())));
    }

    private void initTable() {
        violationTable.getTableHeader().setReorderingAllowed(false);
        violationTable.setRowHeight(50);

        model = new DefaultTableModel(tableData, columnNames);
        violationTable.setModel(model);
        // Сделать таблицу нередактируемой
        violationTable.setDefaultEditor(Object.class, null);

        violationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = violationTable.getSelectedRow();
                    int column = violationTable.getSelectedColumn();
                    if (column == 4) {
                        String code = tableData[row][4];
                        ViolationType violationType;
                        try {
                            violationType = DbManager.getViolationTypeByCode(code);
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e1.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(null, "Нарушение: " + violationType.getName() + "\nШтраф: " + violationType.getPrice(), "Информация о нарушении", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (column == 3) {
                        String regNumber = tableData[row][3];
                        Car car;
                        try {
                            car = DbManager.getCarByRegNumber(regNumber);
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e1.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(null, "Автомобиль: " + car.getBrand() + " " + car.getModel() + " " + car.getColor() + "\nВладелец: " + car.getOwner(), "Информация о автомобиле", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }

    private void applyFilters() {
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
        Date startDateDate;
        Date endDateDate;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            try {
                startDateDate = new SimpleDateFormat("dd.MM.yyyy").parse(startDate);
                endDateDate = new SimpleDateFormat("dd.MM.yyyy").parse(endDate);
                if (endDateDate.before(startDateDate)) {
                    JOptionPane.showMessageDialog(null, "Дата окончания должна быть больше даты начала", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Date currentDate = new Date();
                if (endDateDate.after(currentDate)) {
                    JOptionPane.showMessageDialog(null, "Дата окончания должна быть меньше текущей даты", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    violations = DbManager.getViolationsBetweenDates(startDate, endDate);
                    updateTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
