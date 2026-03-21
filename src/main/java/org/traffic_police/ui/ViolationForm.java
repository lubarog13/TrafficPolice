package org.traffic_police.ui;

import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.traffic_police.utils.BaseForm;
import org.traffic_police.utils.DbManager;
import org.traffic_police.model.Violation;
import org.traffic_police.model.Car;
import org.traffic_police.model.ViolationType;

import java.awt.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.StyleContext;

public class ViolationForm extends BaseForm {
    private JPanel mainPanel;
    private JTextField protocolField;
    private JFormattedTextField dateField;
    private JTextField placeField;
    private JComboBox<String> carBox;
    private JComboBox<String> violationTypeBox;
    private JTextArea infoArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JComboBox violationBox;

    private List<Car> cars = new ArrayList<>();
    private List<ViolationType> violationTypes = new ArrayList<>();
    private Violation selectedViolation;
    private List<Violation> violations = new ArrayList<>();

    public ViolationForm() {
        super(500, 400, false);
        setContentPane(mainPanel);
        initViolations();
        initCarBox();
        initViolationTypeBox();
        initDateField();
        saveButton.addActionListener(e -> saveViolation());
        cancelButton.addActionListener(e -> cancel());
        setVisible(true);
    }

    private void initViolations() {
        try {
            this.violations = DbManager.getViolations();
            violationBox.removeAllItems();
            violationBox.addItem("Редактировать правонарушение");
            for (Violation violation : this.violations) {
                violationBox.addItem(violation.getProtocolNumber());
            }
            violationBox.addActionListener(e -> {
                if (violationBox.getSelectedIndex() == 0) {
                    selectedViolation = null;
                    protocolField.setText("");
                    dateField.setText("");
                    placeField.setText("");
                    carBox.setSelectedIndex(0);
                    violationTypeBox.setSelectedIndex(0);
                    infoArea.setText("");
                    saveButton.setText("Добавить");
                    protocolField.setEnabled(true);
                }
                selectedViolation = this.violations.get(violationBox.getSelectedIndex() - 1);
                protocolField.setText(selectedViolation.getProtocolNumber());
                try {
                    dateField.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(selectedViolation.getViolationDate())));
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Произошла ошибка при форматировании даты: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                placeField.setText(selectedViolation.getPlace());
                for (Car car : this.cars) {
                    if (car.getRegNumber().equals(selectedViolation.getCar())) {
                        carBox.setSelectedItem(car.toString());
                        break;
                    }
                }
                for (ViolationType violationType : this.violationTypes) {
                    if (violationType.getCode().equals(selectedViolation.getCode())) {
                        violationTypeBox.setSelectedItem('(' + violationType.getCode() + ") " + (violationType.getName().length() > 20 ? violationType.getName().substring(0, 20) + "..." : violationType.getName()));
                        break;
                    }
                }
                infoArea.setText(selectedViolation.getOtherDriverInfo());
                saveButton.setText("Сохранить");
                protocolField.setEnabled(false);
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCarBox() {
        try {
            this.cars = DbManager.getCars();
            for (Car car : this.cars) {
                carBox.addItem(car.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initViolationTypeBox() {
        try {
            this.violationTypes = DbManager.getViolationTypes();
            for (ViolationType violationType : this.violationTypes) {
                String item = '(' + violationType.getCode() + ") " + (violationType.getName().length() > 20 ? violationType.getName().substring(0, 20) + "..." : violationType.getName());
                violationTypeBox.addItem(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        dateField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        dateField.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(new Date().getTime())));
    }

    private void cancel() {
        new ViolationTable();
        dispose();
    }

    private void saveViolation() {
        String protocolNumber = protocolField.getText();
        Date violationDate;
        try {
            violationDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(dateField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Некорректный формат даты", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String place = placeField.getText();
        String car = cars.get(carBox.getSelectedIndex()).getRegNumber();
        String code = violationTypes.get(violationTypeBox.getSelectedIndex()).getCode();
        String otherDriverInfo = infoArea.getText();
        Violation violation = new Violation(protocolNumber, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(violationDate), place, car, code, otherDriverInfo);
        if (protocolNumber.isEmpty() || place.isEmpty() || car.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (selectedViolation == null) {
                DbManager.addViolation(violation);
            } else {
                DbManager.updateViolation(violation);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при добавлении правонарушения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Правонарушение успешно " + (selectedViolation == null ? "добавлено" : "изменено"), "Успешно", JOptionPane.INFORMATION_MESSAGE);
        cancel();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Добавление правонарушения");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Номер протокола");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        protocolField = new JTextField();
        panel2.add(protocolField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Дата правонарушения");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateField = new JFormattedTextField();
        panel2.add(dateField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Место правонарушения");
        panel2.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        placeField = new JTextField();
        panel2.add(placeField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Автомобиль");
        panel2.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        carBox = new JComboBox();
        panel2.add(carBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Наименования правонарушения");
        panel2.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        violationTypeBox = new JComboBox();
        panel2.add(violationTypeBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Примечание");
        panel2.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoArea = new JTextArea();
        panel2.add(infoArea, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Сохранить");
        panel2.add(saveButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Отменить");
        panel2.add(cancelButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        violationBox = new JComboBox();
        panel2.add(violationBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
