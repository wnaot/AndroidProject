package com.example.androidproject;

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

import com.example.androidproject.Model.FriendInvitation;
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupChat extends AppCompatActivity {
    private ImageView btn_previos;
    private Button btn_ok;
    private EditText groupchat_name;

    private List<User> listUsers;

    private RecyclerView rcv_selected,rcv_select;
    private CreateGroupChat_Select_Adapter selectAdapter;
    private CreateGroupChat_Selected_Adapter selectedAdapter;

    private List<User> listUserSelected;
    List<User> listuserSeletedtmp;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_chat);

        btn_previos = findViewById(R.id.creategroupchat_btnPrevios);
        btn_ok =  findViewById(R.id.creategroupchat_btn_ok);
        groupchat_name = (EditText) findViewById((R.id.creategroupchat_groupname));
        rcv_select = findViewById(R.id.creategroupchat_recycleview_listuser);
        rcv_selected = findViewById(R.id.creategroupchat_recycleview_userSelected);

        LinearLayoutManager selectLayoutManager  = new LinearLayoutManager(this);
        LinearLayoutManager selectedLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listUserSelected = new ArrayList<>();
        listUsers = new ArrayList<>();
        listuserSeletedtmp = new ArrayList<>();
        selectedAdapter = new CreateGroupChat_Selected_Adapter(listUserSelected,this);
        selectAdapter = new CreateGroupChat_Select_Adapter(listUsers, this, new CreateGroupChat_Select_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                // Xử lý sự kiện khi người dùng nhấn vào một mục
                if(listuserSeletedtmp.contains(user)){
                    listuserSeletedtmp.remove(user);
                } else{
                    listuserSeletedtmp.add(user);
                }
                // Gọi lại phương thức để cập nhật danh sách người dùng được chọn
                getUserSelected();
            }
        });

        rcv_select.setLayoutManager(selectLayoutManager);
        rcv_selected.setLayoutManager(selectedLayoutManager);
        rcv_selected.setAdapter(selectedAdapter);
        rcv_select.setAdapter(selectAdapter);
        getFriendList();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listuserSeletedtmp.size() > 1){
                    createGroupChat(listuserSeletedtmp,"Test group 1","");
                } else {
                    Toast.makeText(CreateGroupChat.this,"Vui lòng chọn ít nhất 2 thành viên",Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_previos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroupChat.this,MessageBox.class);
                startActivity(intent);
            }
        });



    }
    private void createGroupChat(List<User> listU, String groupname, String groupPicture){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        List<String> listIdUser = new ArrayList<>();
        for(User i : listU){
            listIdUser.add(i.getUserId());
        }
    // Tạo một key mới cho GroupChat
        String groupChatId = mDatabase.push().getKey();

    // Tạo một HashMap để lưu thông tin của GroupChat
        Map<String, Object> groupChatMap = new HashMap<>();

        groupChatMap.put("groupchatPicture", groupPicture); // Đặt giá trị ban đầu cho hình ảnh nhóm
        groupChatMap.put("members", listIdUser); // Khởi tạo một danh sách trống cho các thành viên
        groupChatMap.put("messages", new ArrayList<String>()); // Khởi tạo một danh sách trống cho các tin nhắn
        groupChatMap.put("name", groupname); // Đặt giá trị ban đầu cho tên nhóm

    // Đưa dữ liệu vào Firebase Database
        mDatabase.child(groupChatId).setValue(groupChatMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xử lý khi dữ liệu được thêm thành công vào Firebase Database
                        Toast.makeText(CreateGroupChat.this,"GroupChat added successfully",Toast.LENGTH_SHORT).show();
                        Log.d("CreateGroupChat", "GroupChat added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra khi thêm dữ liệu vào Firebase Database
                        Toast.makeText(CreateGroupChat.this,"GroupChat added failed",Toast.LENGTH_SHORT).show();
                        Log.e("CreateGroupChat", "Error adding GroupChat to database", e);
                    }
                });

    }
    private void getUserSelected(){
        // Xóa dữ liệu hiện có trong danh sách người dùng được chọn
        listUserSelected.clear();
        // Thêm tất cả người dùng từ danh sách mới vào danh sách người dùng được chọn
        listUserSelected.addAll(listuserSeletedtmp);
        // Cập nhật adapter của danh sách người dùng được ch
        selectedAdapter.setData(listUserSelected);
        }
    private void getFriendList() {
        listUsers = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseUtil.currentUserId()).child("friendList");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(childSnapshot.getKey());
                    UserDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listUsers.add(UserUtil.getUserFromSnapshot(snapshot));
                            selectAdapter.setData(listUsers);
                            selectedAdapter.setData(listUsers);
                            Log.e("thông tin user: ",UserUtil.getUserFromSnapshot(snapshot).getUserName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Thông tin user:","Không tồn tại");
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e("FriendInvitationId", "Error: " + error.getMessage());
            }
        });
    }

}
