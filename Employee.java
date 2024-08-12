package com.example.qrstaff;

public class Employee {
    private String employeeName;
    private String employeeNumber;
    private String employeeId;
    private String phoneNumber;

    // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
    public Employee() {
    }

    public Employee(String employeeName, String employeeNumber, String employeeId, String phoneNumber) {
        this.employeeName = employeeName;
        this.employeeNumber = employeeNumber;
        this.employeeId = employeeId;
        this.phoneNumber = phoneNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
