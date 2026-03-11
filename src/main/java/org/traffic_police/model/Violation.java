package org.traffic_police.model;

public class Violation {
    private String protocolNumber;
    private String violationDate;
    private String place;
    private String car;
    private String code;
    private String otherDriverInfo;

    public Violation(String protocolNumber, String violationDate, String place, String car, String code, String otherDriverInfo) {
        this.protocolNumber = protocolNumber;
        this.violationDate = violationDate;
        this.place = place;
        this.car = car;
        this.code = code;
        this.otherDriverInfo = otherDriverInfo;
    }

    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public String getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(String violationDate) {
        this.violationDate = violationDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOtherDriverInfo() {
        return otherDriverInfo;
    }

    public void setOtherDriverInfo(String otherDriverInfo) {
        this.otherDriverInfo = otherDriverInfo;
    }
}
