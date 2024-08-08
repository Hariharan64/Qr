package com.example.qrstaff;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AttendanceActivity extends AppCompatActivity {


    private TextView punchInTimeTextView, punchOutTimeTextView;
    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        punchInTimeTextView = findViewById(R.id.punchInTimeTextView);
        punchOutTimeTextView = findViewById(R.id.punchOutTimeTextView);

        attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
        String userId = getUserId(); // Replace with actual user ID retrieval logic

        // Retrieve punch-in and punch-out times from Firebase
        attendanceRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String punchInTime = snapshot.child("punchInTime").getValue(String.class);
                String punchOutTime = snapshot.child("punchOutTime").getValue(String.class);

                punchInTimeTextView.setText(punchInTime != null ? "Punch In Time: " + punchInTime : "Punch In Time: Not recorded");
                punchOutTimeTextView.setText(punchOutTime != null ? "Punch Out Time: " + punchOutTime : "Punch Out Time: Not recorded");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private String getUserId() {
        // Replace this with actual user ID retrieval logic
        return "user123"; // Example user ID
    }
}