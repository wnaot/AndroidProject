package com.example.androidproject.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidproject.model.User;

import java.util.HashMap;
import java.util.Map;

public class AndroidUtil {
    public static  void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public static void passUserModelAsIntent(Intent intent, User model){
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("username",model.getUserName());
        intent.putExtra("email",model.getEmail());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("address",model.getAddress());
        intent.putExtra("profilePicture",model.getProfilePicture());
        intent.putExtra("lastActive",model.getLastActive());
        intent.putExtra("fcmToken",model.getFcmToken());
        if (model.getFriendList() != null) {
            for (Map.Entry<String, Boolean> entry : model.getFriendList().entrySet()) {
                intent.putExtra("friend_" + entry.getKey(), entry.getValue());
            }
        }
        if (model.getBlockList() != null) {
            for (Map.Entry<String, Boolean> entry : model.getBlockList().entrySet()) {
                intent.putExtra("block_" + entry.getKey(), entry.getValue());
            }
        }
    }
    public static User getUserModelFromIntent(Intent intent){
        User user = new User();
        user.setUserId(intent.getStringExtra("userId"));
        user.setUserName(intent.getStringExtra("username"));
        user.setEmail(intent.getStringExtra("email"));
        user.setPhone(intent.getStringExtra("phone"));
        user.setAddress(intent.getStringExtra("address"));
        user.setProfilePicture(intent.getStringExtra("profilePicture"));
        user.setLastActive(intent.getStringExtra("lastActive"));
        user.setFcmToken(intent.getStringExtra("fcmToken"));
        Bundle extras = intent.getExtras();
        if (extras != null) {
            HashMap<String, Boolean> friendList = new HashMap<>();
            for (String key : extras.keySet()) {
                if (key.startsWith("friend_")) {
                    String friendId = key.substring("friend_".length());
                    boolean isFriend = extras.getBoolean(key);
                    friendList.put(friendId, isFriend);
                }
            }
            user.setFriendList(friendList);
        }
        if (extras != null) {
            HashMap<String, Boolean> blockList = new HashMap<>();
            for (String key : extras.keySet()) {
                if (key.startsWith("block_")) {
                    String blockId = key.substring("block_".length());
                    boolean isBlock = extras.getBoolean(key);
                    blockList.put(blockId, isBlock);
                }
            }
            user.setBlockList(blockList);
        }
        return user;
    }

}
