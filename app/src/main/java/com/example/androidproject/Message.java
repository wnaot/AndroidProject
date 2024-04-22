package com.example.androidproject;

public class Message {
    private String ReceiverID;
    private String SenderID;
    private String messageText;
    private String time;

    public Message() {

    }

    public Message (String messageText) {
        this.messageText = messageText;
    }

    public Message(String receiverID, String senderID, String messageText, String time) {
        ReceiverID = receiverID;
        SenderID = senderID;
        this.messageText = messageText;
        this.time= time;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
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
