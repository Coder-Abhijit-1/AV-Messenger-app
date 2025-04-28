package com.example.android.avmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class SplashActivity extends AppCompatActivity {


    ImageView logoImg;
    TextView logoText,owner,ownerName;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImg = findViewById(R.id.logoImg);
        logoText = findViewById(R.id.logoText);
        owner = findViewById(R.id.owner);
        ownerName = findViewById(R.id.ownerName);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);


        logoImg.setAnimation(bottomAnim);
        logoText.setAnimation(topAnim);
        owner.setAnimation(topAnim);
        ownerName.setAnimation(topAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        },3000);

    }
}