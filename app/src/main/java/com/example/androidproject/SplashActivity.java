package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.activity.MainScreen;
import com.example.androidproject.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ProgressBar spinner = findViewById(R.id.progressBar);
        spinner.getIndeterminateDrawable().setColorFilter(0xFF0C2EDF, android.graphics.PorterDuff.Mode.MULTIPLY);

        if(getIntent().getExtras() != null ){
            Intent intent = new Intent(SplashActivity.this, MainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainScreen.class));
                        //AndroidUtil.showToast(SplashActivity.this,FirebaseUtil.currentUserId());
                    }else{
                        startActivity(new Intent(SplashActivity.this,SignInActivity.class));
                    }
                    finish();
                }
            },1000);
        }
//        onStop();
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        FirebaseAuth.getInstance().signOut();
//    }
}
