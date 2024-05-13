package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.androidproject.Model.Message;
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class MessageBox extends AppCompatActivity {
    ImageView imageInfo, imageBack, avatar_image;
    TextView tv_sender_name;
    EditText et_message;
    Button btn_send;
    RecyclerView recyclerView;
    ChatAdapter messageAdapter;
    String idFriend;
    ZegoSendCallInvitationButton voiceCallBtn, videoCallBtn;
    DatabaseReference chatData, messageRef;
    FirebaseUser mUser;
    List<Message> messageList;

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

        // Lấy dữ liệu bạn bè theo ID để hiển thị thông tin bạn bè trên màn hình chat
        Intent intent = getIntent();
        idFriend = intent.getStringExtra("FriendID");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        chatData = FirebaseDatabase.getInstance().getReference("Chats");
        // Lắng nghe sự thay đổi trong nút "Chats" của cả hai người dùng

        voiceCallBtn = findViewById(R.id.voice_call_btn);
        videoCallBtn = findViewById(R.id.video_call_btn);

        setVoiceCall(idFriend);
        setVideoCall(idFriend);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageBox.this, detailAccountActivity.class);
                intent.putExtra("idFriend",idFriend);
                startActivity(intent);
            }
        });
        chatData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    String senderID = message.getSenderID();
                    String receiverID = message.getReceiverID();
                    if ((senderID.equals(mUser.getUid()) && receiverID.equals(idFriend)) ||
                            (senderID.equals(idFriend) && receiverID.equals(mUser.getUid()))) {
                        // Kiểm tra xem tin nhắn có tồn tại trong danh sách chưa
                        if (!messageList.contains(message)) {
                            // Thêm tin nhắn vào danh sách và cập nhật giao diện
                            messageList.add(message);
                            messageAdapter.setData(messageList);
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                }
            }

            // Các phương thức khác của ChildEventListener không cần thiết cho trường hợp này
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        // Lấy dữ liệu bạn bè từ Firebase và hiển thị thông tin
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users").child(idFriend);
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("userName").getValue(String.class);
                String profilePicture = snapshot.child("profilePicture").getValue(String.class);

                User user = new User(userName, profilePicture);
                tv_sender_name.setText(user.getUserName());
                if ("default".equals(user.getProfilePicture())) {
                    avatar_image.setImageResource(R.drawable.default_avatar);
                } else {
                    Picasso.get().load(user.getProfilePicture()).into(avatar_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Gửi tin nhắn khi nhấn nút Gửi
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
        // Khởi tạo danh sách tin nhắn và adapter
        messageList = new ArrayList<>();
        messageAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Load tin nhắn từ Firebase
        //loadMessage();
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

    private void sendMessage() {
        String messageText = et_message.getText().toString().trim();
        String sender_id = mUser.getUid(); // ID của người gửi tin nhắn
        String receiver_id = idFriend; // ID của người nhận tin nhắn

        if (!messageText.isEmpty()) {
            String messageId = chatData.push().getKey();
            if (messageId != null) {
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDateTime = dateFormat.format(new Date(currentTimeMillis));
                Message message = new Message(receiver_id, sender_id, messageText, formattedDateTime);
                chatData.child(messageId).setValue(message)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUtil.allUserDatabaseReference().child(idFriend).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String token = snapshot.child("fcmToken").getValue(String.class);
                                            et_message.setText("");
                                            sendMessage(token,messageText);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    // Xử lý nếu gửi tin nhắn không thành công
                                    Toast.makeText(MessageBox.this, "Gửi tin nhắn không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(MessageBox.this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMessage() {
        messageRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasMessages = false; // Biến để kiểm tra xem có tin nhắn nào hay không
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        String senderID = message.getSenderID();
                        String receiverID = message.getReceiverID();
                        if ((senderID.equals(mUser.getUid()) && receiverID.equals(idFriend)) ||
                                (senderID.equals(idFriend) && receiverID.equals(mUser.getUid()))) {
                            // Kiểm tra xem tin nhắn đã tồn tại trong danh sách chưa
                            if (!messageList.contains(message)) {
                                messageList.add(message);
                                hasMessages = true; // Có tin nhắn được tìm thấy
                            }
                        }
                    }
                }
                messageAdapter.setData(messageList);
                if (hasMessages) {
                    // Nếu có tin nhắn, cuộn đến vị trí cuối cùng
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
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
