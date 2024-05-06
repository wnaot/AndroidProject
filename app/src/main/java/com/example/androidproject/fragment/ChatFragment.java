package com.example.androidproject.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.androidproject.Utils.AndroidUtil;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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

    private ArrayList<User> listUserChat;
    private List<String> listIDFriendChat;

    Chat lastChat;

    public ChatFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.chat_fragment, container, false);

        getUserInGroup();
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

                    if (mUser.getUid().equals(chat.getSenderID())) {
                        listIDFriendChat.add(chat.getReceiverID());
                    }
                    if (mUser.getUid().equals(chat.getReceiverID())) {
                        listIDFriendChat.add(chat.getSenderID());
                    }
                }
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
                    getUserChattedToChats(userId, userName, profilePicture);
                }
                // sắp xếp lại thời gian mới nhất
                itemUserAdapter = new ItemUserAdapter(listUserChat, getContext());
                rcvChat.setAdapter(itemUserAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getUserChattedToChats(final String userId, final String userName, final String profilePicture) {
        if (mUser == null) {
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            Chat chat = dataSnapshot1.getValue(Chat.class);

                            if (mUser != null && mUser.getUid() != null && chat != null && chat.getReceiverID() != null && chat.getSenderID() != null && userId != null) {
                                if (mUser.getUid().equals(chat.getReceiverID()) && userId.equals(chat.getSenderID()) ||
                                        userId.equals(chat.getReceiverID()) && mUser.getUid().equals(chat.getSenderID())) {
                                    listChat.add(chat);
                                }
                            }
                        }

                    if (!listChat.isEmpty()) {
                        int lastIndex = listChat.size() - 1;
                        lastChat = listChat.get(lastIndex);
                    }

                    User user = new User(userId, userName, profilePicture, lastChat);

                    for (String id : listIDFriendChat) {
                        if (id != null && user.getUserId() != null && user.getUserId().equals(id)) {
                            if (listUserChat.size() != 0) {
                                boolean userExists = false;
                                for (User user1 : listUserChat) {
                                    if (user1.getUserId() != null && user.getUserId().equals(user1.getUserId())) {
                                        userExists = true;
                                        break;
                                    }
                                }
                                if (!userExists) {
                                    listUserChat.add(user);
                                }
                            } else {
                                listUserChat.add(user);
                            }
                        }
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getUserInGroup(){
        FirebaseUtil.allGroupChat().child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
