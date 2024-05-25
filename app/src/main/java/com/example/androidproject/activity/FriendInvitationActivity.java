package com.example.androidproject.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.FriendInvitation;
import com.example.androidproject.utils.FirebaseUtil;
import com.example.androidproject.adapter.FriendInvitationAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendInvitationActivity extends AppCompatActivity {
    TextView countInvitation,txtEmpty;
    private RecyclerView recyclerView;
    private List<FriendInvitation> friendInvitationsList;
    private FriendInvitationAdapter friendInvitationAdapter;
    List<FriendInvitation> listDetaiFriendInvitation;
    List<String> listUserId;
    private DatabaseReference mDatabase;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invitation);

        recyclerView = findViewById(R.id.recycle_invitation);
        countInvitation = findViewById(R.id.count_invitation);
        txtEmpty= findViewById(R.id.textEmpty);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        listUserId = new ArrayList<>();
        listDetaiFriendInvitation = new ArrayList<>();

        friendInvitationAdapter = new FriendInvitationAdapter();
        friendInvitationAdapter.setData(listDetaiFriendInvitation);

        recyclerView.setAdapter(friendInvitationAdapter);
        listenForFriendInvitations();

    }
    private void listenForFriendInvitations() {
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
                                if(status.equals("pending")){
                                    FriendInvitation fI = new FriendInvitation(resendId, receiverId, status, 10);
                                    listDetaiFriendInvitation.add(fI);
                                    int invitationCount = listDetaiFriendInvitation.size();
                                    String invitationCountString = String.valueOf(invitationCount);
                                    countInvitation.setText(invitationCountString);
                                }
                                else{
                                    txtEmpty.setVisibility(View.VISIBLE);
                                    countInvitation.setText("0");
                                }
                                friendInvitationAdapter.notifyDataSetChanged();
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
                // Xử lý lỗi nếu có
                Log.e("FriendInvitationId", "Error: " + error.getMessage());
            }
        });
    }
}
