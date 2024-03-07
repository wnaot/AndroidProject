package com.example.androidproject;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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



    }

    private void anhXa(){

    }

}
