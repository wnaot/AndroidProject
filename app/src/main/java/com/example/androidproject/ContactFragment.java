package com.example.androidproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.Map;


public class ContactFragment extends Fragment {
    private String userId;
    private MainScreen mainActivity;
    RecyclerView rcvContract;
    List<User> listUsers,listFriend;
    DatabaseReference usersRef;
    View mView;


    public static ContactFragment newInstance(String userId) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        mView = view; // Gán giá trị cho mView
        mainActivity = (MainScreen) getActivity();
        rcvContract = view.findViewById(R.id.recycle_contract); // Sửa từ mView thành view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        rcvContract.setLayoutManager(linearLayoutManager);

        // Khởi tạo usersRef
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendListData(userId);
        return view;
    }

    public void FriendListData(String userId){
        listUsers = new ArrayList<>();
        listFriend = new ArrayList<>();
        Query query = usersRef.orderByChild("userId").equalTo(userId);
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
                    for (User user : listUsers) {
                        Map<String, Boolean> friendListMap = user.getFriendList();
                        for (String friendUserId : friendListMap.keySet()) {
                            if (friendListMap.get(friendUserId)) {
                                usersRef.child(friendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Lấy thông tin của người dùng từ dataSnapshot
                                            String userId = dataSnapshot.child("userId").getValue(String.class);
                                            String userName = dataSnapshot.child("userName").getValue(String.class);
                                            String email = dataSnapshot.child("email").getValue(String.class);
                                            String phone = dataSnapshot.child("phone").getValue(String.class);
                                            String address = dataSnapshot.child("address").getValue(String.class);
                                            String profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);
                                            String lastActive = dataSnapshot.child("lastActive").getValue(String.class);
                                            HashMap<String, Boolean> friendListMap = new HashMap<>();
                                            HashMap<String, Boolean> blockListMap = new HashMap<>();
                                            for (DataSnapshot friendSnapshot : dataSnapshot.child("friendList").getChildren()) {
                                                friendListMap.put(friendSnapshot.getKey(), friendSnapshot.getValue(Boolean.class));
                                            }
                                            for (DataSnapshot blockSnapshot : dataSnapshot.child("blockList").getChildren()) {
                                                blockListMap.put(blockSnapshot.getKey(), blockSnapshot.getValue(Boolean.class));
                                            }
                                            User user = new User(userId,userName,email,phone,address,"",profilePicture,friendListMap,blockListMap,lastActive);
                                            listFriend.add(user);
                                        } else {

                                        }
                                        updateFriendListOnRecyclerView();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý khi truy vấn bị hủy
                                    }
                                });
                            }
                        }
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi truy vấn bị hủy
            }
        });
    }
    public void updateFriendListOnRecyclerView() {

        ListFriendAdapter listFriendAdapter = new ListFriendAdapter();
        listFriendAdapter.setData(listFriend);
        rcvContract.setAdapter(listFriendAdapter);
    }

}
