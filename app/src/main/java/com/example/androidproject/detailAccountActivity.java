package com.example.androidproject;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class detailAccountActivity extends AppCompatActivity {

    ImageView btn_previos_action;
    ShapeableImageView  imageViewGroup;

    TextView user_name,btn_action_exit;
    String idFriend;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_account);

        btn_previos_action = findViewById(R.id.btn_previos_action);
        imageViewGroup = findViewById(R.id.imageViewGroup);
        user_name = findViewById(R.id.user_name);
        btn_action_exit = findViewById(R.id.btn_action_exit);

        btn_previos_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detailAccountActivity.this,MainScreen.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        idFriend = intent.getStringExtra("idFriend");
        loadData();
        btn_action_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveGroupConfirmationDialog();
            }
        });
    }
    public void loadData(){
        FirebaseUtil.allUserDatabaseReference().child(idFriend).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("profilePicture").getValue(String.class);
                String name = snapshot.child("userName").getValue(String.class);
                user_name.setText(name);
                Picasso.get().load(image).into(imageViewGroup);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showRemoveGroupConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(detailAccountActivity.this);
        builder.setMessage("Bạn chắc chắn muốn hủy kết bạn không?")
                .setCancelable(true)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteFriend();
                        Intent intent = new Intent(detailAccountActivity.this,MainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void deleteFriend(){
        FirebaseUtil.currentUserDetails().child("friendList").child(idFriend).removeValue();
    }
}
