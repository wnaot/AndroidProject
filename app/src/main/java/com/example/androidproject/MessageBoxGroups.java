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
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageBoxGroups extends AppCompatActivity {
    ImageView imageInfo, imageBack, avatar_image;
    TextView tv_sender_name;
    EditText et_message;
    Button btn_send;
    RecyclerView recyclerView;
    ChatGroupAdapter chatGroupAdapter;
    String groupChatId;

    ZegoSendCallInvitationButton voiceCallBtn, videoCallBtn;
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

        voiceCallBtn = findViewById(R.id.voice_call_btn);
        videoCallBtn = findViewById(R.id.video_call_btn);

//        setVoiceCall(idFriend);
//        setVideoCall(idFriend);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBoxGroups.this, DetailGroupActivity.class);
                intent.putExtra("groupChatId",groupChatId);
                startActivity(intent);
                finish();
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

    private void setVoiceCall(String targetUserID){
        voiceCallBtn.setIsVideoCall(false);
        voiceCallBtn.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        voiceCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
    }

    private void setVideoCall(String targetUserID){
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
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
                                            et_message.setText("");
                                            List<String> listIdMember = new ArrayList<>();
                                            FirebaseUtil.allGroupChat().child(groupChatId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    listIdMember.clear();
                                                    for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                                                        String memberId = groupSnapshot.getValue(String.class);
                                                        listIdMember.add(memberId);
                                                    }
                                                    for(String id : listIdMember){
                                                        FirebaseUtil.allUserDatabaseReference().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String token = snapshot.child("fcmToken").getValue(String.class);
                                                                sendMessage(token,messageText);
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
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
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAikwt8ac:APA91bGG0Bya4SgXQtZ23p6EXIatM5n3cVnc57NYDWjZr6Nul_AXO5rn7cuzeGDFnXKjVfn9M_hGSElOWQ4bfcy1AJrOuh2o9KYdvG53yCoOXidwRXvNd1mHHG3DN77Nf83do6u_GNgz")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    void sendMessage(String otherToken,String message){
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User currentUser = UserUtil.getUserFromSnapshot(snapshot);
                    try{
                        JSONObject jsonObject  = new JSONObject();

                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title",currentUser.getUserName());
                        notificationObj.put("body",message);

                        JSONObject dataObj = new JSONObject();
                        dataObj.put("userId",currentUser.getUserId());

                        jsonObject.put("notification",notificationObj);
                        jsonObject.put("data",dataObj);

                        jsonObject.put("to",otherToken);

                        callApi(jsonObject);
                    }catch (Exception e) {
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
