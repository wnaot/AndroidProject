package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MessageBox extends AppCompatActivity {
    ImageView imageInfo;

    RelativeLayout layoutMainScreen;
    ImageView imageBack,avatar_image;
    TextView tv_sender_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagebox);

        imageInfo = findViewById(R.id.info_image);
        imageBack = findViewById(R.id.btnBack);
        tv_sender_name = findViewById(R.id.tv_sender_name);
        avatar_image = findViewById(R.id.avatar_image);
        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBox.this, detailAccountActivity.class);
                startActivity(intent);
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBox.this,MainScreen.class);
                startActivity(intent);
            }
        });
        tv_sender_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBox.this, detailAccountActivity.class);
                startActivity(intent);
            }
        });
        avatar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBox.this, detailAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
