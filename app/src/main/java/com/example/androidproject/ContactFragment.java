package com.example.androidproject;

import android.media.MediaPlayer;
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

import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ContactFragment extends Fragment {
    private MainScreen mainActivity;
    RecyclerView rcvContract;
    List<User> listUsers,listFriend;
    DatabaseReference usersRef;
    View mView;
    private ListFriendAdapter listFriendAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

        String userId = FirebaseUtil.currentUserId();
        mView = view; // Gán giá trị cho mView
        mainActivity = (MainScreen) getActivity();
        rcvContract = view.findViewById(R.id.recycle_contract);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        rcvContract.setLayoutManager(linearLayoutManager);

        // Khởi tạo usersRef
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        listUsers = new ArrayList<>();
        listFriend = new ArrayList<>();
        listFriendAdapter = new ListFriendAdapter();
        listFriendAdapter.setData(listFriend);
        rcvContract.setAdapter(listFriendAdapter);

        FriendListData(userId);
        return view;
    }
    public void FriendListData(String userId){
        Query query = usersRef.orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu trả về không
                if (dataSnapshot.exists()) {
                    // Duyệt qua tất cả các nút con trong DataSnapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = UserUtil.getUserFromSnapshot(snapshot);
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
                                            User user = UserUtil.getUserFromSnapshot(dataSnapshot);
                                            listFriend.add(user);
                                        }
                                        listFriendAdapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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


}
