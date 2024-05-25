package com.example.androidproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.FriendInvitation;
import com.example.androidproject.model.User;
import com.example.androidproject.utils.FirebaseUtil;
import com.example.androidproject.utils.UserUtil;
import com.example.androidproject.adapter.SearchUserAdapter;
import com.example.androidproject.fragment.ContactFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchUser extends AppCompatActivity {
    private RecyclerView rcvUserAdd;
    private SearchUserAdapter searchUserAdapter;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private TextView btnFriend,btnFriendInvitation;
    private DatabaseReference usersRef;

    private List<User> listUsers;
    private boolean isFocused = false;
    private TextView emptySearch,countFriendInvitation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        rcvUserAdd = findViewById(R.id.recycle_user);
        SearchView searchView = findViewById(R.id.SearchViewAdd);
        btnFriend = findViewById(R.id.btnFriend);
        btnFriendInvitation = findViewById(R.id.btnFriendInvitation);
        emptySearch = findViewById(R.id.searchNull);
        countFriendInvitation = findViewById(R.id.img_on);

        fragmentArrayList.add(new ContactFragment());

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("haha",query);
                SearchUserWithPhone(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi nội dung của trường tìm kiếm thay đổi
                return false;
            }
        });
        btnFriendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchUser.this, FriendInvitationActivity.class);
                startActivity(intent);
            }
        });
        // Khởi tạo layout manager và set cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUserAdd.setLayoutManager(linearLayoutManager);
        listUsers = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter();
        searchUserAdapter.setData(listUsers);
        rcvUserAdd.setAdapter(searchUserAdapter);

        listenForFriendInvitations();

    }
    public void SearchUserWithPhone(String phone){
        Query query = usersRef.orderByChild("phone").equalTo(phone);
        // Thực hiện truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsers.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = UserUtil.getUserFromSnapshot(snapshot);
                        if (!user.getUserId().equals(FirebaseUtil.currentUserId())) {
                            listUsers.add(user);
                        }
                        else{
                            emptySearch.setVisibility(View.VISIBLE);
                        }
                    }
                    searchUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("SearchUserWithPhone", "Error searching user with phone: " + databaseError.getMessage());
            }
        });
    }
    void sendFriendInvitation(String message){
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsers.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = UserUtil.getUserFromSnapshot(snapshot);
                        try{
                            JSONObject jsonObject  = new JSONObject();

                            JSONObject notificationObj = new JSONObject();
                            notificationObj.put("title",user.getUserName());
                            notificationObj.put("body",message);

                            JSONObject dataObj = new JSONObject();
                            dataObj.put("userId",user.getUserId());

                            jsonObject.put("notification",notificationObj);
                            jsonObject.put("data",dataObj);

                            //jsonObject.put("to",otherUser.getFcmToken());

                            callApi(jsonObject);
                        }catch (Exception e){

                        }


                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("SearchUserWithPhone", "Error searching user with phone: " + databaseError.getMessage());
            }
        });
    }

    void sendNotification(String message){
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = UserUtil.getUserFromSnapshot(snap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","AAAAikwt8ac:APA91bGG0Bya4SgXQtZ23p6EXIatM5n3cVnc57NYDWjZr6Nul_AXO5rn7cuzeGDFnXKjVfn9M_hGSElOWQ4bfcy1AJrOuh2o9KYdvG53yCoOXidwRXvNd1mHHG3DN77Nf83do6u_GNgz")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
    private void listenForFriendInvitations() {
        List<FriendInvitation> listDetaiFriendInvitation = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("friendInvitations").child(FirebaseUtil.currentUserId());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listDetaiFriendInvitation.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String friendInvitationId = childSnapshot.getKey();
                    FirebaseUtil.currentFriendInvitation().child(friendInvitationId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String receiverId = snapshot.child("receiverId").getValue(String.class);
                                String resendId = snapshot.child("senderId").getValue(String.class);
                                String status = snapshot.child("status").getValue(String.class);
                                if (status.equals("pending")) {
                                    FriendInvitation fI = new FriendInvitation(resendId, receiverId, status, 10);
                                    listDetaiFriendInvitation.add(fI);

                                }
                                int invitationCount = listDetaiFriendInvitation.size();
                                if(invitationCount==0){
                                    countFriendInvitation.setVisibility(View.GONE);
                                }
                                String invitationCountString = String.valueOf(invitationCount);
                                countFriendInvitation.setText(invitationCountString);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                            Log.e("FriendInvitationId", "Error: " + error.getMessage());
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
