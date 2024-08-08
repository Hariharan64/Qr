package com.example.qrstaff;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private FirebaseDatabase database;
    private DatabaseReference attendanceRef;

    private Button scanBtn, punchInBtn, punchOutBtn, profileBtn;
    private TextView messageText, messageFormat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("attendance");

        // Referencing and initializing the buttons and textviews
        scanBtn = findViewById(R.id.scanBtn);
        punchInBtn = findViewById(R.id.punchInBtn);
        punchOutBtn = findViewById(R.id.punchOutBtn);
        profileBtn = findViewById(R.id.profileBtn);
        messageText = findViewById(R.id.textContent);
        messageFormat = findViewById(R.id.textFormat);

        // Adding listener to the buttons
        scanBtn.setOnClickListener(this);
        punchInBtn.setOnClickListener(v -> punchIn());
        punchOutBtn.setOnClickListener(v -> punchOut());
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                messageText.setText(intentResult.getContents());
                messageFormat.setText(intentResult.getFormatName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void punchIn() {
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String userId = getUserId(); // Replace with actual user ID retrieval logic

        // Store punch-in time in Firebase
        attendanceRef.child(userId).child("punchInTime").setValue(currentDateTime)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Punch In Time recorded.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to record Punch In Time.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void punchOut() {
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String userId = getUserId(); // Replace with actual user ID retrieval logic

        // Store punch-out time in Firebase
        attendanceRef.child(userId).child("punchOutTime").setValue(currentDateTime)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Punch Out Time recorded.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,AttendanceActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to record Punch Out Time.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserId() {
        // Replace this with actual user ID retrieval logic
        return "user123"; // Example user ID
    }
}