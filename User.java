package com.example.qrstaff;





public class User {
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
