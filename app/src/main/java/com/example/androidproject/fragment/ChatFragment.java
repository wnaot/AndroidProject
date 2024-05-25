package com.example.androidproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.adapter.HorizontalAdapter;
import com.example.androidproject.adapter.ItemUserAdapter;
import com.example.androidproject.activity.MainScreen;
import com.example.androidproject.model.Chat;
import com.example.androidproject.R;
import com.example.androidproject.model.User;
import com.example.androidproject.utils.FirebaseUtil;
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
    private RecyclerView hRecylerView;
    private SearchView sView;
    private View mView;
    private MainScreen mainActivity;
    private ItemUserAdapter itemUserAdapter;

    private HorizontalAdapter horizontalAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    private List<Chat> listChat;

    private ArrayList<User> listUserChat;
    private List<User> listFriend;
    private List<String> listIDFriendChat,listIDFriend;
    private TextView NoOnline;
    Chat lastChat;

    public ChatFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.chat_fragment, container, false);

        // Nguyen Van Dung
        rcvChat = (RecyclerView) mView.findViewById(R.id.recycle_chat);
        rcvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchView searchView = mView.findViewById(R.id.simpleSearchView);

        hRecylerView = (RecyclerView) mView.findViewById(R.id.horizontalRecyclerView);
        hRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listIDFriendChat = new ArrayList<>();

        NoOnline = mView.findViewById(R.id.NoOnline);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");

        listUserChat = new ArrayList<>();
        listChat = new ArrayList<>();

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
        listFriendHorizontal();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemUserAdapter = new ItemUserAdapter(listUserChat, getContext());
                rcvChat.setAdapter(itemUserAdapter);
                return true;
            }
        });

        return mView;
    }
    private void filter(String text) {
        ArrayList<User> searchResult = new ArrayList<>();
        for (User user : listUserChat) {
            if (user.getUserName().toLowerCase().contains(text.toLowerCase())) {
                searchResult.add(user);
            }
        }
        itemUserAdapter = new ItemUserAdapter(searchResult, getContext());
        rcvChat.setAdapter(itemUserAdapter);
    }
    private void readFriendChatted() {
        List<String> listIdFriend = new ArrayList<>();
        FirebaseUtil.currentUserDetails().child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listIdFriend.add(dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);
                    String status = dataSnapshot.child("Status").getValue(String.class);
                    if(listIdFriend.contains(userId)){
                        getUserChattedToChats(userId, userName, profilePicture,status);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listFriendHorizontal() {
        listIDFriend = new ArrayList<>();
        listFriend = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mDatabase.child(mUser.getUid()).child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDFriend.clear();
                listFriend.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listIDFriend.add(dataSnapshot.getKey());
                }
                for (String id : listIDFriend) {
                    mDatabase.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userId = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);
                            String status = snapshot.child("Status").getValue(String.class);
                            for (User user1 : listFriend) {
                                if (user1.getUserId().equals(userId)) {
                                    listFriend.remove(user1);
                                    break;
                                }
                            }
                            if(status.equals("online")){
                                User user = new User(id, userName, avatar, status);
                                listFriend.add(user);
                            }
                            if(listFriend.isEmpty()){
                                NoOnline.setVisibility(View.VISIBLE);
                            } else {
                                NoOnline.setVisibility(View.GONE);
                            }
                            horizontalAdapter = new HorizontalAdapter(listFriend, getContext());
                            hRecylerView.setAdapter(horizontalAdapter);
                            horizontalAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }


    private void getUserChattedToChats(final String userId, final String userName, final String profilePicture,final String status) {
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

                    User user = new User(userId, userName, profilePicture, lastChat, status);


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

                    itemUserAdapter = new ItemUserAdapter(listUserChat, getContext());
                    rcvChat.setAdapter(itemUserAdapter);


                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
