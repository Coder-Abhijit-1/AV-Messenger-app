package com.example.android.avmessenger;

import android.app.ComponentCaller;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.Shapeable;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    EditText username ;
    EditText suEmail;
    EditText pwd;
    EditText pwdReCheck;
    Button signUp;
    TextView goToLogin;
    ShapeableImageView profileImageView;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    android.app.ProgressDialog progressDialog;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Account Creating...");
        progressDialog.setCancelable(false);

        username = findViewById(R.id.editTextSignupUserName);
        suEmail = findViewById(R.id.editTextSignupEmail);
        pwd = findViewById(R.id.editTextSignupPassword);
        pwdReCheck = findViewById(R.id.editTextSignupReEnterPassword);
        signUp = findViewById(R.id.signUpButton);
        goToLogin = findViewById(R.id.goToLogin);
        profileImageView = findViewById(R.id.circularImageView);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String email = suEmail.getText().toString().trim();
                String password = pwd.getText().toString();
                String cPassword = pwdReCheck.getText().toString();
                String status = "Hey I'm using this Application";

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "All Fields are Required!", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Give proper Email Address", Toast.LENGTH_SHORT).show();
                } else if(password.length() < 8 ) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Password Too Short", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id  = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);


                                if(imageURI != null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                        Users users = new Users(id,name,email,password,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.show();
                                                                    startActivity(new Intent(SignupActivity.this , MainActivity.class));
                                                                    finish();
                                                                }else{
                                                                    Toast.makeText(SignupActivity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    String status = "Hey I'm using this Application";
                                    imageuri = "https://firebasestorage.googleapis.com/v0/b/av-messenger-f5fd3.firebasestorage.app/o/man.png?alt=media&token=ba4bb00d-88e2-4135-ad90-b5795c5cd3df";
                                    Users users = new Users(id, name , email, password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(SignupActivity.this , MainActivity.class));
                                                finish();
                                            }else{
                                                Toast.makeText(SignupActivity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else{
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture" ), 10 );
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this , LoginActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 10 && resultCode == RESULT_OK && data.getData() != null){
                imageURI = data.getData();
                profileImageView.setImageURI(imageURI);
        }else{
            Toast.makeText(this, "No image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}