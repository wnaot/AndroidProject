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
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.FriendInvitation;
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private DatabaseReference usersRef;
    private FirebaseUser mUser;
    private TextView emptySearch;

    private RecyclerView rcv_selected,rcv_select;
    private CreateGroupChat_Select_Adapter selectAdapter;
    private CreateGroupChat_Selected_Adapter selectedAdapter;

    private List<User> listUserSelected;
    private List<String> listIdUser;
    List<User> listuserSeletedtmp;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_chat);

        btn_previos = findViewById(R.id.creategroupchat_btnPrevios);
        btn_ok =  findViewById(R.id.creategroupchat_btn_ok);
        groupchat_name = (EditText) findViewById((R.id.creategroupchat_groupname));
        rcv_select = findViewById(R.id.creategroupchat_recycleview_listuser);
        rcv_selected = findViewById(R.id.creategroupchat_recycleview_userSelected);
        SearchView searchView = findViewById(R.id.SearchViewAddGroup);
        emptySearch = findViewById(R.id.searchNull);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        LinearLayoutManager selectLayoutManager  = new LinearLayoutManager(this);
        LinearLayoutManager selectedLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listUserSelected = new ArrayList<>();
        listUsers = new ArrayList<>();
        listIdUser = new ArrayList<>();

        selectedAdapter = new CreateGroupChat_Selected_Adapter(listUserSelected, this, new CreateGroupChat_Selected_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                if(listUserSelected.contains(user)){
                    listUserSelected.remove(user);
                }
                selectedAdapter.notifyDataSetChanged();
            }
        });
        selectAdapter = new CreateGroupChat_Select_Adapter(listUsers, this, new CreateGroupChat_Select_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                select_itemClick(user);
            }
        });

        getAddUser();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    getAddUser();
                } else{
                    SearchUserWithName(newText);
                }
                return true;
            }
        });
        rcv_select.setLayoutManager(selectLayoutManager);
        rcv_selected.setLayoutManager(selectedLayoutManager);
        rcv_selected.setAdapter(selectedAdapter);
        rcv_select.setAdapter(selectAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listUserSelected.size() > 1){
                    if(groupchat_name.getText().toString().isEmpty()){
                        groupchat_name.setText("Nhóm mới");
                    }
                    createGroupChat(listUserSelected,groupchat_name.getText().toString().trim(),"");
                    finish();
                } else {
                    Toast.makeText(CreateGroupChat.this,"Vui lòng chọn ít nhất 2 thành viên",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_previos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CreateGroupChat.this,MessageBox.class);
//                startActivity(intent);
                finish();
            }
        });



    }
    public void select_itemClick(User user){
        // Xử lý sự kiện khi người dùng nhấn vào một mục
        boolean userExists = false;
        for (User i : listUserSelected) {
            if (i.getUserName().equals(user.getUserName())) {
                userExists = true;
                break;
            }
        }
        if (userExists) {
            for(User i : listUserSelected){
                if (i.getUserName().equals(user.getUserName())) {
                    listUserSelected.remove(i);
                    break;
                }
            }
            Toast.makeText(CreateGroupChat.this, "Đã xóa "+ user.getUserName() +" khỏi danh sách", Toast.LENGTH_SHORT).show();
        } else {
            listUserSelected.add(user);
            Toast.makeText(CreateGroupChat.this, "Đã thêm "+ user.getUserName() +" vào danh sách", Toast.LENGTH_SHORT).show();
        }
//                 Gọi lại phương thức để cập nhật danh sách người dùng được chọn
        selectedAdapter.notifyDataSetChanged();
    }
    public void getAddUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(mUser.getUid()).child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Khởi tạo danh sách trước khi sử dụng
                List<String> listIdUser = new ArrayList<>();
                List<User> listUsers = new ArrayList<>();

                // Xóa danh sách để làm sạch trước khi thêm dữ liệu mới
                listIdUser.clear();
                listUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listIdUser.add(dataSnapshot.getKey());
                }

                if(listIdUser.size() < 1){
                    emptySearch.setVisibility(View.VISIBLE);
                }

                for (String id : listIdUser) {
                    usersRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userID = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);

                            User user = new User(userID, userName, avatar);
                            listUsers.add(user);

                            // Di chuyển notifyDataSetChanged() vào đây để đảm bảo gọi sau khi dữ liệu được thêm vào danh sách
                            selectAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                        }
                    });
                }

                // Ẩn emptySearch khi có dữ liệu
                emptySearch.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });



//        FirebaseUtil.allUserDatabaseReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listUsers.clear();
//                if (snapshot.exists()) {
//                    for (DataSnapshot snapshots : snapshot.getChildren()) {
//                        User user = UserUtil.getUserFromSnapshot(snapshots);
//
//
//                        listUsers.add(user);
//
//                    }
//                    emptySearch.setVisibility(View.GONE);
//                    selectAdapter.notifyDataSetChanged();
//                }
//                if(listUsers.size() < 1){
//                    emptySearch.setVisibility(View.VISIBLE);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
    public void SearchUserWithName(String name){
        Query query = usersRef.orderByChild("userName");
        // Thực hiện truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsers.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = UserUtil.getUserFromSnapshot(snapshot);
                        if (!user.getUserId().equals(FirebaseUtil.currentUserId()) && user.getUserName().toLowerCase().contains(name.toLowerCase())) {
                            listUsers.add(user);
                            Log.e("SearchUserWithPhone", "User: " + user.getUserName());
                        }
                    }
                    emptySearch.setVisibility(View.GONE);
                    selectAdapter.notifyDataSetChanged();
                }
                if(listUsers.size() < 1){
                    emptySearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("SearchUserWithPhone", "Error searching user with phone: " + databaseError.getMessage());
            }
        });
    }
    private void createGroupChat(List<User> listU, String groupname, String groupPicture){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        List<String> listIdUser = new ArrayList<>();
        listIdUser.add(FirebaseUtil.currentUserId());
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
        groupChatMap.put("name", groupname);
        // Đặt giá trị ban đầu cho tên nhóm

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
}
