package com.example.androidproject;

public class ItemUser {
    private int itemImg;
    private String itemName;
    private String itemChat;
    private String itemTime;

    public ItemUser(int itemImg, String itemName, String itemChat, String itemTime) {
        this.itemImg = itemImg;
        this.itemName = itemName;
        this.itemChat = itemChat;
        this.itemTime = itemTime;
    }

    public int getItemImg() {
        return itemImg;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemChat() {
        return itemChat;
    }

    public void setItemChat(String itemChat) {
        this.itemChat = itemChat;
    }

    public String getItemTime() {
        return itemTime;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }
}
