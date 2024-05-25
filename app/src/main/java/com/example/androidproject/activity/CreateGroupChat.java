package com.example.androidproject.activity;

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

import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utils.FirebaseUtil;
import com.example.androidproject.utils.UserUtil;
import com.example.androidproject.adapter.CreateGroupChat_Select_Adapter;
import com.example.androidproject.adapter.CreateGroupChat_Selected_Adapter;
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
        rcv_selected.setVisibility(View.GONE);
        selectedAdapter = new CreateGroupChat_Selected_Adapter(listUserSelected, this, new CreateGroupChat_Selected_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                if(listUserSelected.contains(user)){
                    listUserSelected.remove(user);
                    if(listUserSelected.isEmpty()){
                        rcv_selected.setVisibility(View.GONE);
                    }
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
                SearchUserWithName(query);
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
        rcv_select.setAdapter(selectAdapter);
        rcv_selected.setLayoutManager(selectedLayoutManager);
        rcv_selected.setAdapter(selectedAdapter);

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
        if(listUserSelected.isEmpty()){
            rcv_selected.setVisibility(View.VISIBLE);
        }
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
                    if(listUserSelected.isEmpty()){
                        rcv_selected.setVisibility(View.GONE);
                    }
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
                listIdUser.clear(); // Xóa danh sách id người dùng
                listUsers.clear(); // Xóa danh sách người dùng

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listIdUser.add(dataSnapshot.getKey());
                }

                // Hiển thị hoặc ẩn emptySearch textview tùy thuộc vào có dữ liệu hay không
                if (listIdUser.size() < 1) {
                    emptySearch.setVisibility(View.VISIBLE);
                } else {
                    emptySearch.setVisibility(View.GONE);
                }

                // Duyệt qua danh sách id người dùng và lấy thông tin người dùng
                for (String id : listIdUser) {
                    usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userID = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);
                            String status = snapshot.child("Status").getValue(String.class);
                            User user = new User(userID, userName, avatar, status);
                            listUsers.add(user);

                            // Gọi notifyDataSetChanged chỉ sau khi đã lấy tất cả thông tin người dùng
                            selectAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
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

        Map<String, Boolean> admin = new HashMap<>();
        admin.put(FirebaseUtil.currentUserId(),true);
        groupChatMap.put("groupchatPicture", "https://firebasestorage.googleapis.com/v0/b/productappchat.appspot.com/o/images%2Fdefault-group.png?alt=media&token=0dbd74d8-326e-4e34-b55f-3ecd76a73316"); // Đặt giá trị ban đầu cho hình ảnh nhóm
        groupChatMap.put("members", listIdUser);
        groupChatMap.put("name", groupname);
        groupChatMap.put("groupChatId",groupChatId);
        groupChatMap.put("admin",admin);
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

    // public void SearchUserWithName(String name){

    //     Query query = usersRef.orderByChild("userName");
    //     // Thực hiện truy vấn
    //     query.addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             listUsers.clear();
    //             if (dataSnapshot.exists()) {
    //                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
    //                     User user = UserUtil.getUserFromSnapshot(snapshot);
    //                     if (!user.getUserId().equals(FirebaseUtil.currentUserId()) && user.getUserName().toLowerCase().contains(name.toLowerCase())) {
    //                         listUsers.add(user);
    //                         Log.e("SearchUserWithPhone", "User: " + user.getUserName());
    //                     }
    //                 }
    //                 emptySearch.setVisibility(View.GONE);
    //                 selectAdapter.notifyDataSetChanged();
    //             }
    //             if(listUsers.size() < 1){
    //                 emptySearch.setVisibility(View.VISIBLE);
    //             }
    //         }

    //         @Override
    //         public void onCancelled(DatabaseError databaseError) {
    //             // Xử lý lỗi nếu có
    //             Log.e("SearchUserWithPhone", "Error searching user with phone: " + databaseError.getMessage());
    //         }
    //     });
    // }
    // private void createGroupChat(List<User> listU, String groupname, String groupPicture){
    //     DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChats");
    //     List<String> listIdUser = new ArrayList<>();
    //     listIdUser.add(FirebaseUtil.currentUserId());
    //     for(User i : listU){
    //         listIdUser.add(i.getUserId());
    //     }
    // // Tạo một key mới cho GroupChat
    //     String groupChatId = mDatabase.push().getKey();

    // // Tạo một HashMap để lưu thông tin của GroupChat
    //     Map<String, Object> groupChatMap = new HashMap<>();

    //     Map<String, Boolean> admin = new HashMap<>();
    //     admin.put(FirebaseUtil.currentUserId(),true);
    //     groupChatMap.put("groupchatPicture", "https://firebasestorage.googleapis.com/v0/b/productappchat.appspot.com/o/images%2Fdefault-group.png?alt=media&token=0dbd74d8-326e-4e34-b55f-3ecd76a73316"); // Đặt giá trị ban đầu cho hình ảnh nhóm
    //     groupChatMap.put("members", listIdUser);
    //     groupChatMap.put("name", groupname);
    //     groupChatMap.put("groupChatId",groupChatId);
    //     groupChatMap.put("admin",admin);
    //     // Đặt giá trị ban đầu cho tên nhóm

    // // Đưa dữ liệu vào Firebase Database
    //     mDatabase.child(groupChatId).setValue(groupChatMap)
    //             .addOnSuccessListener(new OnSuccessListener<Void>() {
    //                 @Override
    //                 public void onSuccess(Void aVoid) {
    //                     // Xử lý khi dữ liệu được thêm thành công vào Firebase Database
    //                     Toast.makeText(CreateGroupChat.this,"GroupChat added successfully",Toast.LENGTH_SHORT).show();
    //                     Log.d("CreateGroupChat", "GroupChat added successfully");
    //                 }
    //             })
    //             .addOnFailureListener(new OnFailureListener() {
    //                 @Override
    //                 public void onFailure(@NonNull Exception e) {
    //                     // Xử lý khi có lỗi xảy ra khi thêm dữ liệu vào Firebase Database
    //                     Toast.makeText(CreateGroupChat.this,"GroupChat added failed",Toast.LENGTH_SHORT).show();
    //                     Log.e("CreateGroupChat", "Error adding GroupChat to database", e);
    //                 }
    //             });

    // }

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
}
