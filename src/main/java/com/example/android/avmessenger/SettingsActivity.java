package com.example.android.avmessenger;

import android.app.ComponentCaller;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class SettingsActivity extends AppCompatActivity {

    ImageView setProfile;
    EditText setName, setStatus;
    Button doneButton;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String email, pwd;
    Uri setImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setProfile = findViewById(R.id.settingprofile);
        setName = findViewById(R.id.settingname);
        setStatus = findViewById(R.id.settingstatus);
        doneButton = findViewById(R.id.donebut);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue(String.class);
                pwd = snapshot.child("password").getValue(String.class);
                String name = snapshot.child("userName").getValue(String.class);
                String profile = snapshot.child("profilePic").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                // Check for null and provide fallback values
                if (name == null) name = "Default Name";
                if (profile == null) profile = "default_profile_pic_url"; // Provide a default URL for the profile pic
                if (status == null) status = "Hey there! I'm using AV Messenger.";

                setName.setText(name);
                setStatus.setText(status);
                Picasso.get().load(profile).into(setProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });

        setProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });

        doneButton.setOnClickListener(v -> {
            String name = setName.getText().toString();
            String status = setStatus.getText().toString();

            // Check if the image URI is not null and upload the image
            if (setImageUri != null) {
                storageReference.putFile(setImageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After the file is uploaded successfully, get the download URL
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String finalImageUri = uri.toString();

                            // Create a new user object
                            Users users = new Users(auth.getUid(), name, email, pwd, finalImageUri, status);

                            // Save the user data to the database
                            reference.setValue(users).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    // Data saved successfully
                                    Toast.makeText(SettingsActivity.this, "Data is Saved", Toast.LENGTH_SHORT).show();
                                    // Redirect to the MainActivity
                                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // End the current activity
                                } else {
                                    // Handle failure to save data
                                    Toast.makeText(SettingsActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    } else {
                        // Handle failure to upload the file
                        Toast.makeText(SettingsActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // If there is no new image, just update the profile with the existing image
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String finalImageUri = uri.toString();
                    Users users = new Users(auth.getUid(), name, email, pwd, finalImageUri, status);

                    reference.setValue(users).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Data saved successfully
                            Toast.makeText(SettingsActivity.this, "Data is Saved", Toast.LENGTH_SHORT).show();
                            // Redirect to the MainActivity
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // End the current activity
                        } else {
                            // Handle failure to save data
                            Toast.makeText(SettingsActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && data != null) {
            setImageUri = data.getData();
            setProfile.setImageURI(setImageUri);
        }
    }
}
