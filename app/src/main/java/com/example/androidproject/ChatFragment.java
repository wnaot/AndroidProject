package com.example.androidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {
    private RecyclerView rcvChat;
    private SearchView sView;
    private View mView;
    private MainScreen mainActivity;
    private ItemUserAdapter itemUserAdapter;

    private  HorizontalAdapter horizontalAdapter;

    private DatabaseReference mDatabase;
    private List<User> userList,friendUsers;
    public ChatFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chat_fragment, container, false);
        userList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userId");
            loadListData(userId);
        }
        return mView;
    }
    private void loadListData(String userId){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    User user = dataSnapshot.getValue(User.class);
//                    if (user != null) {
//                        userList.add(user);
//                        updateUI(userList);
//                            Map<String, Boolean> friendList = user.getFriendList();
//                            List<User> friendUsers = new ArrayList<>();
//                            for (Map.Entry<String, Boolean> entry : friendList.entrySet()) {
//                                String friendUserId = entry.getKey();
//                                Boolean isFriend = entry.getValue();
//                                if (isFriend) {
//                                    mDatabase.child(friendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()) {
//                                                User friendUser = dataSnapshot.getValue(User.class);
//                                                if (friendUser != null) {
//                                                    friendUsers.add(friendUser);
//                                                    // Nếu đã lấy dữ liệu của tất cả các bạn bè, cập nhật giao diện
//                                                    if (friendUsers.size() == friendList.size()) {
//                                                        ///updateUI(friendUsers);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                            // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
//                                        }
//                                    });
//                                }
//                            }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
//            }
//        });
    }
    private void updateUI(List<User> friendUsers) {
        mainActivity = (MainScreen) getActivity();
        if (mainActivity != null) {
            sView = mView.findViewById(R.id.simpleSearchView);

            rcvChat = mView.findViewById(R.id.recycle_chat);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
            rcvChat.setLayoutManager(linearLayoutManager);

            itemUserAdapter = new ItemUserAdapter();
            itemUserAdapter.setData(friendUsers);

            rcvChat.setAdapter(itemUserAdapter);

            RecyclerView rcvOnline = mView.findViewById(R.id.horizontalRecyclerView);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
            rcvOnline.setLayoutManager(horizontalLayoutManager);

            horizontalAdapter = new HorizontalAdapter();
            horizontalAdapter.setData(friendUsers);

            rcvOnline.setAdapter(horizontalAdapter);
        }
    }


}
