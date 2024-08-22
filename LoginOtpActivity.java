package com.example.qrstaff;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class LoginOtpActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button verifyOtpBtn;
    private ProgressBar progressBar;

    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        mAuth = FirebaseAuth.getInstance();

        otpInput = findViewById(R.id.otp_input);
        verifyOtpBtn = findViewById(R.id.verify_otp_btn);
        progressBar = findViewById(R.id.otp_progress_bar);

        verificationId = getIntent().getStringExtra("verificationId");

        verifyOtpBtn.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                otpInput.setError("Enter valid OTP");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            verifyOtpBtn.setVisibility(View.INVISIBLE);
            verifyCode(code);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in succeeded, proceed to the main activity
                        Intent intent = new Intent(LoginOtpActivity.this, AttendanceActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        verifyOtpBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginOtpActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
