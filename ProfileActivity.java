package com.example.qrstaff;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String currentImageId;  // Track current image ID for replacement

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.imageView);
        Button uploadButton = findViewById(R.id.uploadButton);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        // Open image picker
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Upload image
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    if (currentImageId != null) {
                        // Image ID is available, proceed to replace the image
                        uploadImage(currentImageId);
                    } else {
                        // No current image ID, handle as a new upload
                        uploadImage(null);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load existing image metadata if available
        loadCurrentImageMetadata();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    private void uploadImage(String oldImageId) {
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                saveImageMetadata(uri.toString(), oldImageId);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveImageMetadata(String imageUrl, String oldImageId) {
        DatabaseReference ref = database.getReference("images");

        if (oldImageId != null) {
            // Remove old image metadata
            ref.child(oldImageId).removeValue();
        }

        // Add new image metadata
        String newImageId = ref.push().getKey();
        ImageMetadata metadata = new ImageMetadata(imageUrl, "Description or other metadata");

        if (newImageId != null) {
            ref.child(newImageId).setValue(metadata)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Image metadata saved", Toast.LENGTH_SHORT).show();
                            currentImageId = newImageId;  // Update current image ID
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed to save metadata", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadCurrentImageMetadata() {
        DatabaseReference ref = database.getReference("images");
        ref.orderByValue().limitToLast(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    ImageMetadata metadata = dataSnapshot.getValue(ImageMetadata.class);
                    if (metadata != null) {
                        currentImageId = dataSnapshot.getKey();  // Get the ID of the last image
                        Glide.with(ProfileActivity.this).load(metadata.imageUrl).into(imageView);
                    }
                }
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