package com.example.androidproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageBox extends AppCompatActivity {
    ImageView imageInfo;

    RelativeLayout layoutMainScreen;
    ImageView imageBack,avatar_image;
    TextView tv_sender_name;

    // Firebase
    DatabaseReference mData, chatData, messageRef;
    FirebaseUser mUser;
    EditText et_message;
    Button btn_send;
    String idFriend;

    RecyclerView recyclerView;
    List<Message> messageList;
    ChatAdapter messageAdapter;

    @SuppressLint("MissingInflatedId")
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
        idFriend = intent.getStringExtra("FriendID"); // ID của bạn sẽ dùng chat với bạn là đây

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
        et_message = findViewById(R.id.et_message);
        btn_send = findViewById(R.id.btn_send);
        chatData = FirebaseDatabase.getInstance().getReference("Chats");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        // Khởi tạo danh sách tin nhắn
        messageList = new ArrayList<>();

        // Khởi tạo adapter và gán cho RecyclerView
        recyclerView = findViewById(R.id.lv_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new ChatAdapter();
        recyclerView.setAdapter(messageAdapter);

        // Cập nhật danh sách tin nhắn
        loadMessage();
    }
    private void sendMessage() {
        // Lay noi dung tin nhan tu EditText
        String messageText = et_message.getText().toString().trim();
        // Lay user_id hien tai
        String sender_id = mUser.getUid();
        // Lay friendid hien tai dang chat
        String receiver_id = idFriend;

        //Kiem tra neu tin nhan rong
        if(!messageText.isEmpty()) {
            //Tao mot ID duy nhat cho tin nhan moi
            String messageId = chatData.child("Chats").push().getKey();

            //Luu tin nhan vao Firebase Realtime DB
            chatData.child(messageId).child("messageText").setValue(messageText);
            chatData.child(messageId).child("SenderID").setValue(sender_id);
            chatData.child(messageId).child("ReceiverID").setValue(receiver_id);

            // Lấy thời gian hiện tại
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // Định dạng thời gian theo ý muốn
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDateTime = dateFormat.format(currentDate);
            // Lưu chuỗi vào Firebase
            chatData.child(messageId).child("time").setValue(formattedDateTime);

            Toast.makeText(MessageBox.this, "Đã gửi", Toast.LENGTH_SHORT).show();
            Toast.makeText(MessageBox.this, sender_id, Toast.LENGTH_SHORT).show();
            Toast.makeText(MessageBox.this, receiver_id, Toast.LENGTH_SHORT).show();

            //Xoa noi dung trong EditText sau khi luu
            et_message.setText("");
            loadMessage();
        } else {
            Toast.makeText(MessageBox.this, "Vui long nhap tin nhan", Toast.LENGTH_SHORT).show();
        }
    }
    public void loadMessage() {
        messageRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có tin nhắn mới được thêm vào
                Message message = snapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.setData(messageList);
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có sự thay đổi trong tin nhắn
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi có tin nhắn bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có tin nhắn được di chuyển
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
}
