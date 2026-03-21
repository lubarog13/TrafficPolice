package org.traffic_police.model;

public class Driver {
    private String licenseNumber;
    private String surname;
    private String name;
    private String middleName;
    private String address;
    private String otherInfo;

    public Driver(String licenseNumber, String surname, String name, String middleName, String address, String otherInfo) {
        this.licenseNumber = licenseNumber;
        this.surname = surname;
        this.name = name;
        this.middleName = middleName;
        this.address = address;
        this.otherInfo = otherInfo;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getSurname() {
        return surname;
    }
    
    public String getName() {
        return name;
    }
    public String getMiddleName() {
        return middleName;
    }

    public String getAddress() {
        return address;
    }

    public String getOtherInfo() {
        return otherInfo;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getFullName() {
        return surname + " " + name + " " + middleName;
    }
}
