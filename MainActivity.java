package com.example.qrstaff;


import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private TextView textViewUserInfo;
    private DatabaseReference databaseReference;

    private static final int REQUEST_CODE = 0;
    private TextView resultTextView;
    Button btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_scan=findViewById(R.id.btn_scan);


        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI element
        textViewUserInfo = findViewById(R.id.textViewUserInfo);

        // Retrieve user data from Firebase
        fetchUserData();
    }

    private void fetchUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder userInfo = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userInfo.append("Name: ").append(user.getName()).append("\n")
                                .append("Designation: ").append(user.getDesignation()).append("\n")
                                .append("User ID: ").append(user.getUserId()).append("\n")
                                .append("Phone Number: ").append(user.getPhoneNumber()).append("\n\n");
                    }
                }
                textViewUserInfo.setText(userInfo.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });


       btn_scan.setOnClickListener(v -> {

           ScanCode();
       });
    }

    private void ScanCode() {
        ScanOptions options=new ScanOptions();
        options.setPrompt("Volume up to Flash on");
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);

    }

    ActivityResultLauncher<ScanOptions>barLauncher=registerForActivityResult(new ScanContract(),result->{

        if (result.getContents() !=null){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }


    });
}