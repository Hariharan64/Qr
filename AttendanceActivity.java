package com.example.qrstaff;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private GridView gridViewCalendar;
    private Button punchInButton, punchOutButton, btn;
    private DatabaseReference databaseReference;
    private String selectedDate;
    private String userId; // Unique ID for the current user
    private ArrayList<String> dates;
    private CalendarAdapter calendarAdapter;
    private Map<String, String> dateStatusMap;
    private TextView textViewUsers;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        TextView shiftTimings = findViewById(R.id.shiftTimings);
        shiftTimings.setText("Shift Timings: 09:00 AM - 07:00 PM");

        // Initialize UI elements
        textViewUsers = findViewById(R.id.textViewUsers);


        // Initialize views
        gridViewCalendar = findViewById(R.id.gridViewCalendar);
        punchInButton = findViewById(R.id.punchInButton);
        punchOutButton = findViewById(R.id.punchOutButton);
        btn = findViewById(R.id.btn);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceActivity.this, HomeActivity.class);
            startActivity(intent);
        });

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

        // Punch In button click event
        punchInButton.setOnClickListener(v -> scanQRCode(true));

        // Punch Out button click event
        punchOutButton.setOnClickListener(v -> scanQRCode(false));
    }

    private void populateCalendarDates() {
        for (int i = 1; i <= 31; i++) {
            dates.add(String.format("2024-08-%02d", i)); // Example for August 2024
        }
    }

    private void scanQRCode(boolean isPunchIn) {
        Intent intent = new Intent(this, QRCodeScannerActivity.class);
        intent.putExtra("isPunchIn", isPunchIn);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            boolean isPunchIn = data.getBooleanExtra("isPunchIn", true);
            recordPunch(isPunchIn);
        }
    }

    private void recordPunch(boolean isPunchIn) {
        // Automatically select today's date when punching in
        if (isPunchIn) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            calendarAdapter.setSelectedDate(selectedDate);
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        DatabaseReference dateRef = databaseReference.child(userId).child(selectedDate);

        if (isPunchIn) {
            dateRef.child("punchInTime").setValue(currentTime);
            dateRef.child("status").setValue("punch_in"); // Save status as "punch_in" in Firebase
            Toast.makeText(this, "Punch In Recorded", Toast.LENGTH_SHORT).show();
            calendarAdapter.setHighlightedDate(selectedDate); // Highlight date as punch-in (blue)
        } else {
            dateRef.child("punchOutTime").setValue(currentTime);
            dateRef.child("status").setValue("punch_out"); // Save status as "punch_out" in Firebase
            calculateTotalTime(dateRef);
            Toast.makeText(this, "Punch Out Recorded", Toast.LENGTH_SHORT).show();
            calendarAdapter.setHighlightedDate(selectedDate); // Update the highlighted date to green
        }
    }

    private void calculateTotalTime(DatabaseReference dateRef) {
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String punchInTime = snapshot.child("punchInTime").getValue(String.class);
                String punchOutTime = snapshot.child("punchOutTime").getValue(String.class);

                if (punchInTime != null && punchOutTime != null) {
                    // Retrieve shift timings
                    DatabaseReference shiftRef = databaseReference.child(userId).child("shifts");
                    shiftRef.get().addOnCompleteListener(shiftTask -> {
                        if (shiftTask.isSuccessful()) {
                            DataSnapshot shiftSnapshot = shiftTask.getResult();
                            String shiftStartTime = shiftSnapshot.child("startTime").getValue(String.class);
                            String shiftEndTime = shiftSnapshot.child("endTime").getValue(String.class);

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                Date inTime = sdf.parse(punchInTime);
                                Date outTime = sdf.parse(punchOutTime);
                                Date shiftStart = sdf.parse(shiftStartTime);
                                Date shiftEnd = sdf.parse(shiftEndTime);

                                if (inTime != null && outTime != null && shiftStart != null && shiftEnd != null) {
                                    // Ensure punch-in and punch-out are within shift timings
                                    if (inTime.before(shiftStart)) {
                                        inTime = shiftStart;
                                    }
                                    if (outTime.after(shiftEnd)) {
                                        outTime = shiftEnd;
                                    }

                                    long duration = outTime.getTime() - inTime.getTime(); // duration in milliseconds
                                    long durationInHours = duration / (1000 * 60 * 60); // convert to hours
                                    long durationInMinutes = (duration % (1000 * 60 * 60)) / (1000 * 60); // remaining minutes

                                    String totalWorkingTime = durationInHours + " hours " + durationInMinutes + " minutes";
                                    dateRef.child("totalWorkingTime").setValue(totalWorkingTime);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error calculating total time", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error retrieving shift timings", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Punch in or punch out time missing", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error retrieving punch times", Toast.LENGTH_SHORT).show();
            }
        });
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

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve user details from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder userData = new StringBuilder();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    userData.append("Name: ").append(name).append("\n")
                            .append("Email: ").append(email).append("\n\n");
                }
                textViewUsers.setText(userData.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AttendanceActivity", "Failed to retrieve data", databaseError.toException());
            }
        });
    }
}

