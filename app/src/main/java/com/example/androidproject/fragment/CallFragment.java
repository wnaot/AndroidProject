package com.example.androidproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.GroupAdapter;
import com.example.androidproject.ItemUserAdapter;
import com.example.androidproject.Model.Chat;
import com.example.androidproject.Model.GroupChat;
import com.example.androidproject.Model.MessageGroup;
import com.example.androidproject.Model.User;
import com.example.androidproject.R;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class CallFragment extends Fragment {

    private RecyclerView rcvGroup;
    private GroupAdapter groupAdapter;
    private List<GroupChat> groupChatList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_fragment, container, false);
        rcvGroup = view.findViewById(R.id.recycle_groups);
        rcvGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        groupChatList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupChatList, getContext());
        rcvGroup.setAdapter(groupAdapter);
        return view;
    }

    // private void loadListGroups() {
    //     FirebaseUtil.allGroupChat().addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(@NonNull DataSnapshot snapshot) {
    //             groupChatList.clear();
    //             for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
    //                 // Lấy giá trị của mỗi nhóm chat từ DataSnapshot
    //                 String groupChatId = groupSnapshot.child("groupChatId").getValue(String.class);
    //                 String groupchatPicture = groupSnapshot.child("groupchatPicture").getValue(String.class);
    //                 String groupChatName = groupSnapshot.child("name").getValue(String.class);
    //                 List<String> members = new ArrayList<>();
    //                 for (DataSnapshot memberSnapshot : groupSnapshot.child("members").getChildren()) {
    //                     // Thêm các thành viên vào danh sách
    //                     String memberId = memberSnapshot.getValue(String.class);
    //                     members.add(memberId);
    //                 }
    //                 List<MessageGroup> messageGroups = new ArrayList<>();
    //                 for (DataSnapshot messageSnapshot : groupSnapshot.child("messageGroups").getChildren()) {
    //                     // Lấy thông tin tin nhắn trong nhóm
    //                     String messageId = messageSnapshot.getKey();
    //                     String messageText = messageSnapshot.child("messageText").getValue(String.class);
    //                     String senderID = messageSnapshot.child("senderID").getValue(String.class);
    //                     String time = messageSnapshot.child("time").getValue(String.class);
    //                     // Tạo đối tượng MessageGroup và thêm vào danh sách
    //                     MessageGroup messageGroup = new MessageGroup(messageText, senderID, time);
    //                     messageGroups.add(messageGroup);
    //                 }
    //                 // Kiểm tra xem người dùng hiện tại có là thành viên của nhóm không
    //                 if (members.contains(FirebaseUtil.currentUserId())) {
    //                     GroupChat groupChat = new GroupChat(groupchatPicture, members , groupChatName , messageGroups , groupChatId);
    //                     groupChatList.add(groupChat);
    //                 }
    //             }
    //             // Cập nhật RecyclerView sau khi đã lấy dữ liệu
    //             groupAdapter.notifyDataSetChanged();
    //         }

    //         @Override
    //         public void onCancelled(@NonNull DatabaseError error) {
    //             // Xử lý khi có lỗi
    //         }
    //     });
    // }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            // Gọi lại phương thức loadListGroups() để tải lại dữ liệu khi có thay đổi
            loadListGroups();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // Xử lý khi có lỗi
        }
    };

    private void loadListGroups() {
        FirebaseUtil.allGroupChat().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    // Lấy giá trị của mỗi nhóm chat từ DataSnapshot
                    String groupChatId = groupSnapshot.child("groupChatId").getValue(String.class);
                    String groupchatPicture = groupSnapshot.child("groupchatPicture").getValue(String.class);
                    String groupChatName = groupSnapshot.child("name").getValue(String.class);
                    List<String> members = new ArrayList<>();
                    for (DataSnapshot memberSnapshot : groupSnapshot.child("members").getChildren()) {
                        // Thêm các thành viên vào danh sách
                        String memberId = memberSnapshot.getValue(String.class);
                        members.add(memberId);
                    }
                    List<MessageGroup> messageGroups = new ArrayList<>();
                    for (DataSnapshot messageSnapshot : groupSnapshot.child("messageGroups").getChildren()) {
                        // Lấy thông tin tin nhắn trong nhóm
                        String messageId = messageSnapshot.getKey();
                        String messageText = messageSnapshot.child("messageText").getValue(String.class);
                        String senderID = messageSnapshot.child("senderID").getValue(String.class);
                        String time = messageSnapshot.child("time").getValue(String.class);
                        // Tạo đối tượng MessageGroup và thêm vào danh sách
                        MessageGroup messageGroup = new MessageGroup(messageText, senderID, time);
                        messageGroups.add(messageGroup);
                    }
                    // Kiểm tra xem người dùng hiện tại có là thành viên của nhóm không
                    if (members.contains(FirebaseUtil.currentUserId())) {
                        GroupChat groupChat = new GroupChat(groupchatPicture, members , groupChatName , messageGroups , groupChatId);
                        groupChatList.add(groupChat);
                    }
                }
                // Cập nhật RecyclerView sau khi đã lấy dữ liệu
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUtil.allGroupChat().addValueEventListener(valueEventListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        FirebaseUtil.allGroupChat().removeEventListener(valueEventListener);
    }
}
