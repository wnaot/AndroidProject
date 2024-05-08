package com.example.androidproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.GroupChat;
import com.example.androidproject.Model.Message;
import com.example.androidproject.Model.MessageGroup;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageBoxGroups extends AppCompatActivity {
    ImageView imageInfo, imageBack, avatar_image;
    TextView tv_sender_name;
    EditText et_message;
    Button btn_send;
    RecyclerView recyclerView;
    ChatGroupAdapter chatGroupAdapter;
    String groupChatId;

    List<MessageGroup> messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagebox);

        imageInfo = findViewById(R.id.info_image);
        imageBack = findViewById(R.id.btnBack);
        tv_sender_name = findViewById(R.id.tv_sender_name);
        avatar_image = findViewById(R.id.avatar_image);
        et_message = findViewById(R.id.et_message);
        btn_send = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.lv_messages);

        Intent intent = getIntent();
        groupChatId = intent.getStringExtra("groupChatId");

        messageList = new ArrayList<>();
        chatGroupAdapter = new ChatGroupAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatGroupAdapter);
        LoadDataGroupChat();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        FirebaseUtil.allGroupChat().child(groupChatId).child("messageGroups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageGroup messageGroup = snapshot.getValue(MessageGroup.class);

                // Kiểm tra xem messageGroup đã tồn tại trong messageList chưa
                if (messageGroup != null && !messageList.contains(messageGroup)) {
                    // Thêm messageGroup vào messageList
                    messageList.add(messageGroup);

                    // Cập nhật dữ liệu cho adapter
                    chatGroupAdapter.setData(messageList);

                    // Cuộn xuống vị trí mới nhất
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void LoadDataGroupChat(){
        if (groupChatId != null && !groupChatId.isEmpty()) {
            FirebaseUtil.allGroupChat().child(groupChatId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String groupchatPicture = snapshot.child("groupchatPicture").getValue(String.class);
                        String groupChatName = snapshot.child("name").getValue(String.class);
                        tv_sender_name.setText(groupChatName);
                        Picasso.get().load(groupchatPicture).into(avatar_image);
                    } else {
                        Log.e(TAG, "No data found for groupChatId: " + groupChatId);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase onCancelled: " + error.getMessage());
                }
            });
        } else {
            Log.e(TAG, "groupChatId is null or empty");
        }
    }

    private void sendMessage() {
        String messageText = et_message.getText().toString().trim();
        String sender_id = FirebaseUtil.currentUserId();

        if (groupChatId != null && !groupChatId.isEmpty()) {
            if (!messageText.isEmpty()) {
                String messageId = FirebaseUtil.allGroupChat().child(groupChatId).child("messageGroups").push().getKey();
                if (messageId != null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    try {
                        String formattedDateTime = dateFormat.format(new Date(currentTimeMillis));
                        MessageGroup message = new MessageGroup(sender_id, messageText, formattedDateTime);
                        FirebaseUtil.allGroupChat().child(groupChatId).child("messageGroups").child(messageId).setValue(message)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Tin nhắn đã được gửi thành công
                                            et_message.setText("");
                                        } else {
                                            // Xử lý nếu gửi tin nhắn không thành công
                                            Toast.makeText(MessageBoxGroups.this, "Gửi tin nhắn không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Xử lý nếu không tạo được messageId
                    Toast.makeText(MessageBoxGroups.this, "Không thể tạo messageId", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MessageBoxGroups.this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Xử lý nếu groupChatId là null hoặc rỗng
            Toast.makeText(MessageBoxGroups.this, "groupChatId không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }



}
