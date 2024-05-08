package com.example.androidproject.Model;

public class MessageGroup {
    private String SenderID;
    private String messageText;
    private String time;

    public MessageGroup() {
    }
    public MessageGroup(String senderID, String messageText, String time) {
        SenderID = senderID;
        this.messageText = messageText;
        this.time = time;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }
    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
