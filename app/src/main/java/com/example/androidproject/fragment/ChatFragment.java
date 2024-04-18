package com.example.androidproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.HorizontalAdapter;
import com.example.androidproject.ItemUserAdapter;
import com.example.androidproject.MainScreen;
import com.example.androidproject.Model.Chat;
import com.example.androidproject.R;
import com.example.androidproject.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView rcvChat;
    private SearchView sView;
    private View mView;
    private MainScreen mainActivity;
    private ItemUserAdapter itemUserAdapter;

    private HorizontalAdapter horizontalAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
//    private List<User> userList,friendUsers;

    private List<Chat> listChat;

    private List<User> listUserChat;
    private List<String> listIDFriendChat;

    Chat lastChat;

    public ChatFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.chat_fragment, container, false);
//        userList = new ArrayList<>();
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String userId = bundle.getString("userId");
//            loadListData(userId);
//        }
        // Nguyen Van Dung
        rcvChat = (RecyclerView) mView.findViewById(R.id.recycle_chat);
        rcvChat.setLayoutManager(new LinearLayoutManager(getContext()));

        listIDFriendChat = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDFriendChat.clear();
                // thêm các ID bạn bè đã chat vào
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getSenderID().equals(mUser.getUid())) {
                        listIDFriendChat.add(chat.getReceiverID());
                    }
                    if(chat.getReceiverID().equals(mUser.getUid())) {
                        listIDFriendChat.add(chat.getSenderID());
                    }
                }
                //
                readFriendChatted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return mView;
    }

    private void readFriendChatted() {
        listUserChat = new ArrayList<>();
        listChat = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);



                    // Đã có id người bạn đã chat
                    // Check node id chat cuối cùng là mình với bạn mình để gán vào Object Chat


                    codeHiHi(userId, userName, profilePicture);


                }
                itemUserAdapter = new ItemUserAdapter(listUserChat, getContext());
                rcvChat.setAdapter(itemUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void codeHiHi(String userId, String userName, String profilePicture) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    Chat chat  = dataSnapshot1.getValue(Chat.class);

                    if(chat.getReceiverID().equals(mUser.getUid()) && chat.getSenderID().equals(userId) ||
                            chat.getReceiverID().equals(userId) && chat.getSenderID().equals(mUser.getUid())) {
                        listChat.add(chat);
                    }

                }

                if (!listChat.isEmpty()) {
                    int lastIndex = listChat.size() - 1;
                    lastChat = listChat.get(lastIndex);
                    Toast.makeText(getContext(), "" + lastChat.getMessageText(), Toast.LENGTH_SHORT).show();
                }

                User user = new User(userId, userName, profilePicture, lastChat);

                for (String id : listIDFriendChat) {
                    if(user.getUserId().equals(id)) {
                        if(listUserChat.size() != 0) {
                            boolean userExists = false;
                            for (User user1 : listUserChat) {
                                if(user.getUserId().equals(user1.getUserId())) {
                                    userExists = true;
                                    break;
                                }
                            }
                            if(!userExists) {
                                listUserChat.add(user);
                            }
                        }
                        else {
                            listUserChat.add(user);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
//    private void updateUI(List<User> friendUsers) {
//        mainActivity = (MainScreen) getActivity();
//        if (mainActivity != null) {
//            sView = mView.findViewById(R.id.simpleSearchView);
//
//            rcvChat = mView.findViewById(R.id.recycle_chat);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
//            rcvChat.setLayoutManager(linearLayoutManager);
//
//            itemUserAdapter = new ItemUserAdapter();
//            itemUserAdapter.setData(friendUsers);
//
//            rcvChat.setAdapter(itemUserAdapter);
//
//            RecyclerView rcvOnline = mView.findViewById(R.id.horizontalRecyclerView);
//            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
//            rcvOnline.setLayoutManager(horizontalLayoutManager);
//
//            horizontalAdapter = new HorizontalAdapter();
//            horizontalAdapter.setData(friendUsers);
//
//            rcvOnline.setAdapter(horizontalAdapter);
//        }
//    }


}
