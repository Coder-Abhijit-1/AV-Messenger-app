package com.example.android.avmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.EmptySuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button loginBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView goToSignup;
    android.app.ProgressDialog progressDialog;


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        email =findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginTextPassword);
        loginBtn = findViewById(R.id.loginButton);
        goToSignup = findViewById(R.id.goToSignup);

        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            // User already logged in
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // close login activity
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(pwd)){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                }else if(pwd.length() < 8 ){
                    progressDialog.dismiss();
                    password.setError("Password too Short");
                } else if (!Email.matches(emailPattern)) {
                    email.setError("Give proper Email Address");
                    progressDialog.dismiss();
                }else{
                    auth.signInWithEmailAndPassword(Email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.show();
                                try{
                                    startActivity(new Intent(LoginActivity.this , MainActivity.class));
                                    finish();
                                }catch(Exception e ){
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , SignupActivity.class));
                finish();
            }
        });


    }
}