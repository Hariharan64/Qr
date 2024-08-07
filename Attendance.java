package com.example.qrstaff;

// Attendance.java
public class Attendance {
    private String punchInTime;
    private String description;

    public Attendance() {
        // Default constructor required for calls to DataSnapshot.getValue(Attendance.class)
    }

    public Attendance(String punchInTime, String description) {
        this.punchInTime = punchInTime;
        this.description = description;
    }

    public String getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(String punchInTime) {
        this.punchInTime = punchInTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
