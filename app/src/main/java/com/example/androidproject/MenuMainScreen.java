package com.example.androidproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MenuMainScreen extends AppCompatActivity {
    private ImageView btnBack,btnSetting;
    TextView userName;

    ShapeableImageView imageAvatar;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnBack = findViewById(R.id.btnBack);
        btnSetting = findViewById(R.id.setting_image);
        imageAvatar = findViewById(R.id.avatar_image_user);
        userName = findViewById(R.id.username_text);


//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                userName.setText(user.getUserName());
//                if(user.getProfilePicture().equals("default")){
//                    imageAvatar.setImageResource(R.mipmap.ic_launcher);
//                }
//                else{
//                    Picasso.get().load(user.getProfilePicture()).into(imageAvatar);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMainScreen.this,MainScreen.class);
                startActivity(intent);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMainScreen.this,InfoUser.class);
                startActivity(intent);
            }
        });
    }
}
