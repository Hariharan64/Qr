package com.example.qrstaff;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {

    private EditText editTextPhoneNumber, editTextOTP;
    private Button buttonVerifyPhone, buttonSubmitData;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextOTP = findViewById(R.id.editTextOTP);
        buttonVerifyPhone = findViewById(R.id.buttonVerifyPhone);
        buttonSubmitData = findViewById(R.id.buttonSubmitData);

        mAuth = FirebaseAuth.getInstance();

        buttonVerifyPhone.setOnClickListener(v -> verifyPhoneNumber());
        buttonSubmitData.setOnClickListener(v -> verifyOTPAndSubmitData());
    }

    private void verifyPhoneNumber() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            editTextPhoneNumber.setError("Enter a valid phone number");
            editTextPhoneNumber.requestFocus();
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)  // Note: replace with country code
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Auto verification or instant verification
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                editTextOTP.setVisibility(View.VISIBLE);
                                buttonSubmitData.setVisibility(View.VISIBLE);
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTPAndSubmitData() {
        String code = editTextOTP.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {
            editTextOTP.setError("Enter valid code");
            editTextOTP.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in
                        // Proceed to store user data
                        storeUserData();
                    } else {
                        Toast.makeText(MainActivity2.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserData() {
        // Store data in Firestore


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("phoneNumber", editTextPhoneNumber.getText().toString());
        // Add other user data here

        db.collection("employees")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity2.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity2.this, AttendanceActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity2.this, "Error adding user", Toast.LENGTH_SHORT).show());

    }
}
