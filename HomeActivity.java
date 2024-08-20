package com.example.qrstaff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private GridView gridViewCalendar;
    private Button punchInButton, punchOutButton, btn;
    private DatabaseReference databaseReference;
    private String selectedDate;
    private String userId; // Unique ID for the current user
    private ArrayList<String> dates;
    private CalendarAdapter calendarAdapter;
    private Map<String, String> dateStatusMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gridViewCalendar = findViewById(R.id.gridViewCalendar);




        // Retrieve the user ID (you should set this based on actual user login or selection)
        userId = "user1"; // This should be dynamically set based on the logged-in user

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        // Initialize calendar dates and statuses
        dates = new ArrayList<>();
        dateStatusMap = new HashMap<>();
        populateCalendarDates();

        // Set calendar adapter
        calendarAdapter = new CalendarAdapter(this, dates, dateStatusMap);
        gridViewCalendar.setAdapter(calendarAdapter);

        // Load statuses from Firebase
        fetchStatusFromFirebase();

        // Calendar item click listener
        gridViewCalendar.setOnItemClickListener((parent, view, position, id) -> {
            selectedDate = dates.get(position);
            calendarAdapter.setSelectedDate(selectedDate);
        });
    }





    private void populateCalendarDates() {
        for (int i = 1; i <= 31; i++) {
            dates.add(String.format("2024-08-%02d", i)); // Example for August 2024
        }
    }



    private void fetchStatusFromFirebase() {
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dateStatusMap.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String status = dateSnapshot.child("status").getValue(String.class); // Retrieve status from Firebase
                    if (status != null) {
                        dateStatusMap.put(date, status);
                    }
                }
                calendarAdapter.notifyDataSetChanged(); // Refresh the calendar view
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}