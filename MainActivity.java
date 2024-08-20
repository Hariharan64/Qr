package com.example.qrstaff;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private DatabaseReference databaseReference;
    private String punchInTime, punchOutTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("attendance");

        Button scanButton = findViewById(R.id.scanButton);
        Button punchInButton = findViewById(R.id.punchInButton);
        Button punchOutButton = findViewById(R.id.punchOutButton);

        btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(v -> initiateScan());

        punchInButton.setOnClickListener(v -> punchIn());

        punchOutButton.setOnClickListener(v -> punchOut());
    }

    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    private void punchIn() {
        punchInTime = getCurrentTime();
        Toast.makeText(this, "Punched In at " + punchInTime, Toast.LENGTH_SHORT).show();
        saveToFirebase("in", punchInTime, null);
    }

    private void punchOut() {
        punchOutTime = getCurrentTime();
        Toast.makeText(this, "Punched Out at " + punchOutTime, Toast.LENGTH_SHORT).show();
        saveToFirebase("out", punchInTime, punchOutTime);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveToFirebase(String status, String punchInTime, @Nullable String punchOutTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String totalHours = calculateTotalHours(punchInTime, punchOutTime);

    }

    private String calculateTotalHours(String punchInTime, @Nullable String punchOutTime) {
        if (punchOutTime == null) return "N/A";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date inTime = sdf.parse(punchInTime);
            Date outTime = sdf.parse(punchOutTime);

            long difference = outTime.getTime() - inTime.getTime();
            long hours = (difference / (1000 * 60 * 60)) % 24;
            long minutes = (difference / (1000 * 60)) % 60;
            return hours + ":" + minutes + " hr";
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Handle QR code scan result here
            }
        }
    }
}
