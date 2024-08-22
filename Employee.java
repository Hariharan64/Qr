package com.example.qrstaff;


public class Employee {
    private String name;
    private String number;
    private String designation;
    private String phone;

    // No-argument constructor required for Firestore
    public Employee() {
    }

    public Employee(String name, String number, String designation, String phone) {
        this.name = name;
        this.number = number;
        this.designation = designation;
        this.phone = phone;
    }

    // Getter and Setter methods for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
