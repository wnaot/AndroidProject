package com.example.androidproject.Utils;

import com.example.androidproject.Model.User;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class UserUtil {

    public static User getUserFromSnapshot(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            // Lấy thông tin của người dùng từ DataSnapshot
            String userId = dataSnapshot.child("userId").getValue(String.class);
            String userName = dataSnapshot.child("userName").getValue(String.class);
            String email = dataSnapshot.child("email").getValue(String.class);
            String phone = dataSnapshot.child("phone").getValue(String.class);
            String address = dataSnapshot.child("address").getValue(String.class);
            String profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);
            String lastActive = dataSnapshot.child("lastActive").getValue(String.class);

            HashMap<String, Boolean> friendListMap = new HashMap<>();
            HashMap<String, Boolean> blockListMap = new HashMap<>();
            for (DataSnapshot friendSnapshot : dataSnapshot.child("friendList").getChildren()) {
                friendListMap.put(friendSnapshot.getKey(), friendSnapshot.getValue(Boolean.class));
            }
            for (DataSnapshot blockSnapshot : dataSnapshot.child("blockList").getChildren()) {
                blockListMap.put(blockSnapshot.getKey(), blockSnapshot.getValue(Boolean.class));
            }
            String fcmToken = dataSnapshot.child("fcmToken").getValue(String.class);
            return new User(userId, userName, email, phone, address, "", profilePicture, friendListMap, blockListMap, lastActive,fcmToken);
        } else {
            return null;
        }
    }
}
