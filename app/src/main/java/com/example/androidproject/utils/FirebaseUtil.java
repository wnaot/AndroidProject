package com.example.androidproject.utils;

import com.example.androidproject.model.FriendInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if(currentUserId() != null){
            return true;
        }
        return false;
    }
    public static DatabaseReference currentUserDetails() {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId());
    }

    public static DatabaseReference allUserDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }
    public static DatabaseReference allGroupChat(){
        return FirebaseDatabase.getInstance().getReference().child("GroupChats");
    }
    public static DatabaseReference currentFriendInvitation(){
        return FirebaseDatabase.getInstance().getReference().child("friendInvitations").child(currentUserId());
    }
    public static DatabaseReference allFriendInvitation(){
        return FirebaseDatabase.getInstance().getReference().child("friendInvitations");
    }
    public static DatabaseReference acceptFriendInvitation(){
        return FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId()).child("friendList");
    }
    public static DatabaseReference acceptOtherFriendInvitation(String otherUserId){
        return FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId).child("friendList");
    }
    public static void addFriendInvitation(String senderUserId, String receiverUserId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        FriendInvitation invitation = new FriendInvitation(senderUserId, receiverUserId,"pending",10);

        mDatabase.child("friendInvitations").child(receiverUserId).child(senderUserId).setValue(invitation);
    }
}
