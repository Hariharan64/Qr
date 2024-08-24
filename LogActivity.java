package com.example.qrstaff;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogActivity extends AppCompatActivity {

    private TextView tvName, tvProf, tvBio, tvEmail, tvWebsite;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Initialize Firebase Database and Firestore
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child("unique_user_id"); // Adjust your path
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        tvName = findViewById(R.id.tv_name_f1);
        tvProf = findViewById(R.id.tv_prof_f1);
        tvBio = findViewById(R.id.tv_bio_f1);
        tvEmail = findViewById(R.id.tv_email_f1);
        tvWebsite = findViewById(R.id.tv_web_f1);

        // Retrieve data from Firebase Realtime Database
        getUserDataFromRealtimeDatabase();

        // Retrieve data from Firestore
        getUserDataFromFirestore();
    }

    private void getUserDataFromRealtimeDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profession = snapshot.child("profession").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    tvName.setText(name);
                    tvProf.setText(profession);
                    tvBio.setText(bio);
                    tvEmail.setText(email);
                } else {
                    Toast.makeText(LogActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LogActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDataFromFirestore() {
        DocumentReference docRef = firestore.collection("Users").document("UserID"); // Adjust your path
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String website = document.getString("website");
                    tvWebsite.setText(website);
                } else {
                    Toast.makeText(LogActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LogActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
