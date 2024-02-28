package com.example.androidproject;

public class User {
    String userName;
    String email;
    String phone;
    String address;
    String passWord;

    public User(String userName, String email, String phone, String address, String passWord, String confirm) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.passWord = passWord;
        this.confirm = confirm;
    }
    public User(){

    }

    String confirm;

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

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
