package com.example.qrstaff;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {


    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    private EditText editTextUserPhoneNumber;
    private Button buttonVerify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        editTextUserPhoneNumber = findViewById(R.id.login_mobile_number);
        buttonVerify = findViewById(R.id.send_otp_btn);

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInputPhoneNumber = editTextUserPhoneNumber.getText().toString().trim();
                if (!userInputPhoneNumber.isEmpty()) {
                    // Verify phone number against Firebase
                    verifyPhoneNumberInFirebase(userInputPhoneNumber);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyPhoneNumberInFirebase(final String userInputPhoneNumber) {
        // Get a reference to your Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Assuming you have the userId of the user
        String userId = "unique_user_id"; // Replace with the user's ID

        // Retrieve the stored phone number from Firebase
        usersRef.child(userId).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPhoneNumber = dataSnapshot.getValue(String.class);
                    if (storedPhoneNumber.equals(userInputPhoneNumber)) {
                        // Phone numbers match, allow access to the next page
                        Toast.makeText(LoginActivity.this, "Phone number verified", Toast.LENGTH_SHORT).show();
                        // Proceed to next activity or perform next action
                        // Example: startActivity(new Intent(VerifyPhoneNumberActivity.this, NextActivity.class));
                    } else {
                        // Phone numbers do not match
                        Toast.makeText(LoginActivity.this, "Phone number does not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User ID not found in database or no phone number stored
                    Toast.makeText(LoginActivity.this, "User not found or no phone number stored", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}