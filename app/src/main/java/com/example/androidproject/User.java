package com.example.androidproject;


import java.util.Map;
public class User {
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String profilePicture;
    private Map<String, Boolean> friendList;
    private Map<String, Boolean> blockList;
    private String lastActive;
    private String fcmToken;
    // Constructor

    public User(){

    }

    public User(String userId, String userName, String email, String phone, String address, String password, String profilePicture, Map<String, Boolean> friendList, Map<String, Boolean> blockList, String lastActive, String fcmToken) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.profilePicture = profilePicture;
        this.friendList = friendList;
        this.blockList = blockList;
        this.lastActive = lastActive;
        this.fcmToken = fcmToken;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Map<String, Boolean> getFriendList() {
        return friendList;
    }

    public void setFriendList(Map<String, Boolean> friendList) {
        this.friendList = friendList;
    }

    public Map<String, Boolean> getBlockList() {
        return blockList;
    }

    public void setBlockList(Map<String, Boolean> blockList) {
        this.blockList = blockList;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
