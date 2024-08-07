package com.example.qrstaff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity  {

    private ImageView imageView;
    private FirebaseDatabase database;
    Button button,btn;

    private TextView scanResult;

    private DatabaseReference attendanceRef;

    private TextView resultText;
    private EditText inputText;
    private Button scanButton;
    private Button submitButton;
    private CalendarView calendarView;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        database = FirebaseDatabase.getInstance();

        resultText = findViewById(R.id.result_text);
        scanButton = findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR Code");
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                resultText.setText("Cancelled");
            } else {
                resultText.setText("Scanned: " + result.getContents());
            }
        }




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AttendanceActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });


        // Retrieve image metadata from Firebase Realtime Database
        DatabaseReference ref = database.getReference("images");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop through all image metadata entries
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ImageMetadata metadata = dataSnapshot.getValue(ImageMetadata.class);

                    if (metadata != null) {
                        // Display image using Glide
                        Glide.with(MainActivity.this)
                                .load(metadata.imageUrl)
                                .into(imageView);
                    } else {
                        Toast.makeText(MainActivity.this, "No image metadata found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load image metadata", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ImageMetadata {
        public String imageUrl;
        public String description;

        public ImageMetadata() {
            // Default constructor required for calls to DataSnapshot.getValue(ImageMetadata.class)
        }

        public ImageMetadata(String imageUrl, String description) {
            this.imageUrl = imageUrl;
            this.description = description;
        }
    }
}