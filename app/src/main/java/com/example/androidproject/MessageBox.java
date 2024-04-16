package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidproject.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MessageBox extends AppCompatActivity {
    ImageView imageInfo;

    RelativeLayout layoutMainScreen;
    ImageView imageBack,avatar_image;
    TextView tv_sender_name;

    // Firebase
    DatabaseReference mData;
    FirebaseUser mUser;
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


        // Lấy dữ liệu bạn bè theo ID để hiện thông tin bạn bè trên màn hình chat
        Intent intent = getIntent();
        String idFriend = intent.getStringExtra("FriendID"); // ID của bạn sẽ dùng chat với bạn là đây

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference("Users").child(idFriend);

        // Lấy dữ liệu bạn bè theo ID và hiện username và image lên chat box
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tv_sender_name.setText(user.getUserName());

                if("default".equals(user.getProfilePicture())) {
                    avatar_image.setImageResource(R.drawable.dog);
                }
                else {
                    Picasso.get().load(user.getProfilePicture()).into(avatar_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}
