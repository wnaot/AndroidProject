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
//    List<User> listUsers,listFriend;
//    DatabaseReference usersRef;
//    View mView;

    ArrayList<User> listUsersFriend;
    ArrayList<String> listIDFriend;
    DatabaseReference mData;
    FirebaseUser mUser;
    private ListFriendAdapter listFriendAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

//        String userId = FirebaseUtil.currentUserId();
//        mView = view; // Gán giá trị cho mView
//        mainActivity = (MainScreen) getActivity();
//        rcvContatc = view.findViewById(R.id.recycle_contract);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
//        rcvContatc.setLayoutManager(linearLayoutManager);
//
//        // Khởi tạo usersRef
//        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        listUsers = new ArrayList<>();
//        listFriend = new ArrayList<>();
//        listFriendAdapter = new ListFriendAdapter();
//        listFriendAdapter.setData(listFriend);
//        rcvContatc.setAdapter(listFriendAdapter);
//
//        FriendListData(userId);

        // NGUYEN VAN DUNG
        rcvContact = (RecyclerView) view.findViewById(R.id.recycle_contact);
        rcvContact.setLayoutManager(new LinearLayoutManager(getContext()));

        listIDFriend = new ArrayList<>();
        listUsersFriend = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference("Users");

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

                            User user = new User(userID, userName, avatar);
                            listUsersFriend.add(user);

                            listFriendAdapter = new ListFriendAdapter(listUsersFriend, getContext());
                            rcvContact.setAdapter(listFriendAdapter);
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
        return view;
    }
//    public void FriendListData(String userId){
//        Query query = usersRef.orderByChild("userId").equalTo(userId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Kiểm tra xem có dữ liệu trả về không
//                if (dataSnapshot.exists()) {
//                    // Duyệt qua tất cả các nút con trong DataSnapshot
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        User user = UserUtil.getUserFromSnapshot(snapshot);
//                        listUsers.add(user);
//                    }
//                    for (User user : listUsers) {
//                        Map<String, Boolean> friendListMap = user.getFriendList();
//                        for (String friendUserId : friendListMap.keySet()) {
//                            if (friendListMap.get(friendUserId)) {
//                                usersRef.child(friendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()) {
//                                            User user = UserUtil.getUserFromSnapshot(dataSnapshot);
//                                            listFriend.add(user);
//                                        }
//                                        listFriendAdapter.notifyDataSetChanged();
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                } else {
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý khi truy vấn bị hủy
//            }
//        });
//    }


}
