package com.example.qrstaff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;


    Button btnGenerateOtp, btnSignIn;
    EditText phoneNumber, Otp;
    TextView timer;
    Spinner spinner;

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String verificationCodeSent;
    String getPhoneNumber, getOtp;

    private EditText editTextUserPhoneNumber;
    private Button buttonVerify;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        btnGenerateOtp = findViewById(R.id.btn_generate_otp);
        btnSignIn = findViewById(R.id.btn_sign_in);

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
                    Toast.makeText(MainActivity2.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity2.this, "Phone number verified", Toast.LENGTH_SHORT).show();
                        // Proceed to next activity or perform next action
                        // Example: startActivity(new Intent(VerifyPhoneNumberActivity.this, NextActivity.class));
                    } else {
                        // Phone numbers do not match
                        Toast.makeText(MainActivity2.this, "Phone number does not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User ID not found in database or no phone number stored
                    Toast.makeText(MainActivity2.this, "User not found or no phone number stored", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(MainActivity2.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

        });



        btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnerText = spinner.getSelectedItem().toString();
                String phone = phoneNumber.getText().toString();

                if (phone == null || phone.trim().isEmpty()) {
                    phoneNumber.setError("Provide Phone Number");
                    return;
                }

                getPhoneNumber = spinnerText + phone;
                btnSignIn.setVisibility(View.VISIBLE);
                Otp.setVisibility(View.VISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        getPhoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        MainActivity2.this,
                        callbacks

                );

                startTimer(60 * 1000, 1000);
                btnGenerateOtp.setVisibility(View.INVISIBLE);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp = Otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                        verificationCodeSent,getOtp
                );

                SignInWithPhoneNumber(credential);
            }
        });

    }

    private void SignInWithPhoneNumber(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(MainActivity2.this,
                                    HomeActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity2.this,"Incorrect OTp",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void startTimer(final long finish, final long tick) {

        timer.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer;

        countDownTimer = new CountDownTimer(finish, tick) {
            @Override
            public void onTick(long l) {
                long remindSec = l / 1000;
                timer.setText("Retry after" + (remindSec / 60)
                        + ":" + (remindSec % 60));
            }

            @Override
            public void onFinish() {
                btnGenerateOtp.setText("Re-generate OTP");
                btnGenerateOtp.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity2.this, "Finish",
                        Toast.LENGTH_LONG).show();

                timer.setVisibility(View.INVISIBLE);
                cancel();
            }
        }.start();

    }

    private void firebaseLogin() {
        auth = FirebaseAuth.getInstance();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(MainActivity2.this, "Verification Completed",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(MainActivity2.this, "Verification Failed",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCodeSent = s;
                Toast.makeText(MainActivity2.this, "Code Sent",
                        Toast.LENGTH_LONG).show();

            }
        };
    }
}