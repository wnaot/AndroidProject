package com.example.androidproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.ListFriendAdapter;
import com.example.androidproject.MainScreen;
import com.example.androidproject.R;
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContactFragment extends Fragment {
    private MainScreen mainActivity;
    RecyclerView rcvContact;


    ArrayList<User> listUsersFriend;
    ArrayList<String> listIDFriend;
    DatabaseReference mData;
    FirebaseUser mUser;
    private ListFriendAdapter listFriendAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);


        // NGUYEN VAN DUNG
        rcvContact = (RecyclerView) view.findViewById(R.id.recycle_contact);
        rcvContact.setLayoutManager(new LinearLayoutManager(getContext()));

        listIDFriend = new ArrayList<>();
        listUsersFriend = new ArrayList<>();



        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference("Users");

        loadData();
        return view;
    }
    public void loadData(){
        mData.child(mUser.getUid()).child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDFriend.clear();
                listUsersFriend.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listIDFriend.add(dataSnapshot.getKey());
                }

                for (String id : listIDFriend) {

                    mData.child(id).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String userID = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);
                            String status = snapshot.child("Status").getValue(String.class);
                            if(listUsersFriend.contains(userID)){
                                listUsersFriend.remove(1);
                            }
                            User user = new User(userID, userName, avatar,status);
                            for (User user1 : listUsersFriend) {
                                if (user1.getUserId().equals(userID)) {
                                    listUsersFriend.remove(user1);
                                    break;
                                }
                            }
                            listUsersFriend.add(user);
                            if (listUsersFriend.size() == listIDFriend.size()) {
                                listFriendAdapter = new ListFriendAdapter(listUsersFriend, getContext());
                                rcvContact.setAdapter(listFriendAdapter);
                                listFriendAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
