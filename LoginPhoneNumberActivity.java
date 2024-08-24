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

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference adminPhoneNumberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        mAuth = FirebaseAuth.getInstance();
        adminPhoneNumberRef = FirebaseDatabase.getInstance().getReference("AdminPhoneNumbers");

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.editTextEmployeeName);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            sendOtpBtn.setVisibility(View.INVISIBLE);

            String phoneNumber = countryCodePicker.getFullNumberWithPlus();
            checkIfPhoneNumberIsAdmin(phoneNumber);
        });
    }

    private void checkIfPhoneNumberIsAdmin(String phoneNumber) {
        adminPhoneNumberRef.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendVerificationCode(phoneNumber);
                } else {
                    progressBar.setVisibility(View.GONE);
                    sendOtpBtn.setVisibility(View.VISIBLE);
                    phoneInput.setError("Phone number not recognized");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                sendOtpBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LoginPhoneNumberActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                sendOtpBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginPhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressBar.setVisibility(View.GONE);
                                sendOtpBtn.setVisibility(View.VISIBLE);

                                Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
                                intent.putExtra("phone", phoneNumber);
                                intent.putExtra("verificationId", verificationId);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginPhoneNumberActivity.this, AttendanceActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginPhoneNumberActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
