package com.example.qrstaff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    private String userId;
    private ArrayList<String> dates;
    private CalendarAdapter calendarAdapter;
    private Map<String, String> dateStatusMap;
    private TextView presentCountView, absentCountView;
    private ImageView imageView;

    private ImageView imageViewProfile;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Initialize UI elements
        TextView shiftTimings = findViewById(R.id.shiftTimings);
        presentCountView = findViewById(R.id.presentCount);
        absentCountView = findViewById(R.id.absentCount);

        gridViewCalendar = findViewById(R.id.gridViewCalendar);
        punchInButton = findViewById(R.id.punchInButton);
        punchOutButton = findViewById(R.id.punchOutButton);
        btn = findViewById(R.id.btn);
        shiftTimings.setText("Shift Timings: 09:00 AM - 07:00 PM");

        imageViewProfile = findViewById(R.id.imageViewProfile);

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        loadProfileImage();
    }

    private void loadProfileImage() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.getValue(String.class);
                    Picasso.get().load(imageUrl).into(imageViewProfile);
                    break; // If you only want to load one image, break after loading
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });






        // Set a unique user ID (this should be dynamically set based on the logged-in user)
        userId = "user1";

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
        punchInButton.setOnClickListener(v -> recordPunch(true));

        // Punch Out button click event
        punchOutButton.setOnClickListener(v -> recordPunch(false));

        // Image view click listener (for navigating to profile)
        imageViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Button click listener (for navigating to home)
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void populateCalendarDates() {
        for (int i = 1; i <= 31; i++) {
            dates.add(String.format("2024-08-%02d", i)); // Example for August 2024
        }
    }

    private void recordPunch(boolean isPunchIn) {
        // Automatically set the selected date to the current date
        selectedDate = getCurrentDate();

        // Get the current time
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        // Reference to the specific date under the user's attendance record
        DatabaseReference dateRef = databaseReference.child(userId).child(selectedDate);

        if (isPunchIn) {
            // Record the punch-in time and update the status
            dateRef.child("punchInTime").setValue(currentTime);
            dateRef.child("status").setValue("punch_in");
            updatePresentCount();
            Toast.makeText(this, "Punch In Recorded", Toast.LENGTH_SHORT).show();
            calendarAdapter.setHighlightedDate(selectedDate);
        } else {
            // Record the punch-out time and update the status
            dateRef.child("punchOutTime").setValue(currentTime);
            dateRef.child("status").setValue("punch_out");
            calculateTotalTime(dateRef);
            Toast.makeText(this, "Punch Out Recorded", Toast.LENGTH_SHORT).show();
            calendarAdapter.setHighlightedDate(selectedDate);
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }


    private void calculateTotalTime(DatabaseReference dateRef) {
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String punchInTime = snapshot.child("punchInTime").getValue(String.class);
                String punchOutTime = snapshot.child("punchOutTime").getValue(String.class);

                if (punchInTime != null && punchOutTime != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        Date inTime = sdf.parse(punchInTime);
                        Date outTime = sdf.parse(punchOutTime);

                        if (inTime != null && outTime != null) {
                            long duration = outTime.getTime() - inTime.getTime();
                            long durationInHours = duration / (1000 * 60 * 60);
                            long durationInMinutes = (duration % (1000 * 60 * 60)) / (1000 * 60);

                            String totalWorkingTime = durationInHours + " hours " + durationInMinutes + " minutes";
                            dateRef.child("totalWorkingTime").setValue(totalWorkingTime);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AttendanceActivity.this, "Error calculating total time", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceActivity.this, "Error retrieving punch times", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePresentCount() {
        DatabaseReference userRef = databaseReference.child(userId);
        userRef.child("presentCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentPresentCount = snapshot.getValue(Long.class);
                if (currentPresentCount == null) {
                    currentPresentCount = 0L;
                }
                userRef.child("presentCount").setValue(currentPresentCount + 1);
                presentCountView.setText("Present: " + (currentPresentCount + 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceActivity.this, "Error updating present count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStatusFromFirebase() {
        DatabaseReference userRef = databaseReference.child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String status = dateSnapshot.child("status").getValue(String.class);
                    dateStatusMap.put(date, status);
                }
                calendarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });




    }
}
