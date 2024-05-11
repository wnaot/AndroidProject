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

public class AddUserInGroup extends AppCompatActivity {
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
    String groupChatId;
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

        groupchat_name.setVisibility(View.GONE);
        rcv_selected.setVisibility(View.GONE);

        Intent intent = getIntent();
        groupChatId = intent.getStringExtra("groupChatId");


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
                if(listUserSelected.size() > 0){
                    createGroupChat(listUserSelected);
                    Intent intent = new Intent(AddUserInGroup.this,MemberActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddUserInGroup.this,"Vui lòng chọn thêm ít nhất 1 thành viên",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_previos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            Toast.makeText(AddUserInGroup.this, "Đã xóa "+ user.getUserName() +" khỏi danh sách", Toast.LENGTH_SHORT).show();
        } else {
            listUserSelected.add(user);
            Toast.makeText(AddUserInGroup.this, "Đã thêm "+ user.getUserName() +" vào danh sách", Toast.LENGTH_SHORT).show();
        }
//                 Gọi lại phương thức để cập nhật danh sách người dùng được chọn
        selectedAdapter.notifyDataSetChanged();
    }
    public void getAddUser(){
        FirebaseUtil.allGroupChat().child(groupChatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> listMembers = new ArrayList<>();
                for (DataSnapshot memberSnapshot : snapshot.child("members").getChildren()) {
                    String member = memberSnapshot.getValue(String.class);
                    listMembers.add(member);
                }
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
                                    if(!listMembers.contains(userID)){
                                        User user = new User(userID, userName, avatar, status);
                                        listUsers.add(user);
                                    }
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
    private void createGroupChat(List<User> listUser){
        FirebaseUtil.allGroupChat().child(groupChatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> listMembersNew = new ArrayList<>();
                for (DataSnapshot memberSnapshot : snapshot.child("members").getChildren()) {
                    String member = memberSnapshot.getValue(String.class);
                    listMembersNew.add(member);
                }
                FirebaseUtil.allGroupChat().child(groupChatId).child("members").removeValue();
                for(User i : listUser){
                    listMembersNew.add(i.getUserId());
                }
                Map<String, Object> groupChatMap = new HashMap<>();
                groupChatMap.put("members", listMembersNew);
                FirebaseUtil.allGroupChat().child(groupChatId).updateChildren(groupChatMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update thành công
                                Log.d("AddMembersToGroup", "Thêm thành viên vào nhóm thành công");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi xảy ra lỗi
                                Log.e("AddMembersToGroup", "Lỗi khi thêm thành viên vào nhóm", e);
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
