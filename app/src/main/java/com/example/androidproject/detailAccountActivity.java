package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class detailAccountActivity extends AppCompatActivity {
    // các nút từ trên xuống dưới
    private ImageView btnPreviosAction, btnMore;
    private LinearLayout btnActionCall,btnActionVideoCall,btnActionSeeProfile, btnActionTogNotification;
    private TextView userName;
    private ImageView userImage;
    private TextView btnActionCustomChuDe,btnActionCustomEmotion,btnActionCustomNickname,btnActionCustomTextEffect;
    private TextView btnActionCustomSeeFile,btnActionCustomMessageClip,btnActionCustomFindMessage;
    private RelativeLayout btnCustomNotification;
    private TextView txtNotificationMessage;
    private TextView btnActionCustomNotificationVolumn,btnActionCustomPrivacyChat,btnActionCustomCreateGroup,btnActionCustomShare;
    private RelativeLayout btnCustomNotificationRead,btnCustomReport;
    private TextView txtNotificationReadMessage,txtReportMessage;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_account);

        btnPreviosAction = findViewById(R.id.btn_previos_action);
        userName = findViewById(R.id.user_name);
//        userImage = findViewById(R.id.user_image);

        btnPreviosAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detailAccountActivity.this,MessageBox.class);
                startActivity(intent);
            }
        });
//        btnActionCustomCreateGroup = findViewById(R.id.btn_action_custom_create_group);

        btnActionCustomCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(detailAccountActivity.this, CreateGroupChat.class);
                startActivity(intent);
            }
        });

        Intent ParentIntent = getIntent();
        String currentUserId = ParentIntent.getStringExtra("CurrentUserId");
        if(!currentUserId.isEmpty()){
            DatabaseReference databaseReference = FirebaseUtil.currentUserDetails();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User currentUser = UserUtil.getUserFromSnapshot(snapshot);
                    userName.setText(currentUser.getUserName());
//                    userImage.setImageResource(currentUser.getProfilePicture());
                    Toast.makeText(detailAccountActivity.this,"DetailAccount: Lấy thông tin user thành công",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(detailAccountActivity.this,"DetailAccount: Không tìm thấy thông tin user",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
