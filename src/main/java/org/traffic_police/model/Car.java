package org.traffic_police.model;

public class Car {
    private String regNumber;
    private String brand;
    private String model;
    private String color;
    private String owner;

    public Car(String regNumber, String brand, String model, String color, String owner) {
        this.regNumber = regNumber;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.owner = owner;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String toString() {
        return this.brand + ' ' + this.model + ' ' + this.regNumber;
    }
}
