package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchUser extends AppCompatActivity {
    private RecyclerView rcvUserAdd;
    private SearchUserAdapter searchUserAdapter;

    private DatabaseReference usersRef;

    private List<User> listUsers;
    private boolean isFocused = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        rcvUserAdd = findViewById(R.id.recycle_user);
        SearchView searchView = findViewById(R.id.SearchViewAdd);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchUser.this, "Search"+ query, Toast.LENGTH_SHORT).show();

                SearchUserWithPhone(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi nội dung của trường tìm kiếm thay đổi
                return false;
            }
        });
        // Khởi tạo layout manager và set cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUserAdd.setLayoutManager(linearLayoutManager);


    }
    public void SearchUserWithPhone(String phone){
        listUsers = new ArrayList<>();
        Query query = usersRef.orderByChild("phone").equalTo(phone);
        // Thực hiện truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu trả về không
                if (dataSnapshot.exists()) {
                    // Duyệt qua tất cả các nút con trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Lấy thông tin của người dùng từ DataSnapshot
                        String userId = snapshot.child("userId").getValue(String.class);
                        String userName = snapshot.child("userName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String address = snapshot.child("address").getValue(String.class);
                        String profilePicture = snapshot.child("profilePicture").getValue(String.class);
                        String lastActive = snapshot.child("lastActive").getValue(String.class);

                        HashMap<String, Boolean> friendListMap = new HashMap<>();
                        HashMap<String, Boolean> blockListMap = new HashMap<>();
                        for (DataSnapshot friendSnapshot : snapshot.child("friendList").getChildren()) {
                            friendListMap.put(friendSnapshot.getKey(), friendSnapshot.getValue(Boolean.class));
                        }
                        for (DataSnapshot blockSnapshot : snapshot.child("blockList").getChildren()) {
                            blockListMap.put(blockSnapshot.getKey(), blockSnapshot.getValue(Boolean.class));
                        }
                        User user = new User(userId,userName,email,phone,address,"",profilePicture,friendListMap,blockListMap,lastActive);
                        listUsers.add(user);
                    }
                    searchUserAdapter = new SearchUserAdapter();
                    searchUserAdapter.setData(listUsers);
                    rcvUserAdd.setAdapter(searchUserAdapter);
                } else {
                    // Không tìm thấy người dùng nào có số điện thoại cụ thể
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi truy vấn bị hủy
            }
        });
    }
    // Phương thức lấy danh sách item user (có thể đưa vào một lớp util)
}
