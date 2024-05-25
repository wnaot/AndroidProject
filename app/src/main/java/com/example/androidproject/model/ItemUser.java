package com.example.androidproject.model;

public class ItemUser {
    private int itemImg;
    private String itemName;
    private String itemChat;

    private Chat chat;

    public ItemUser() {
    }

    public ItemUser(int itemImg, String itemName, String itemChat, Chat chat) {
        this.itemImg = itemImg;
        this.itemName = itemName;
        this.itemChat = itemChat;
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
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

}
