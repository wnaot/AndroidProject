package com.example.androidproject.Model;

import java.util.List;

public class GroupChat {
    private String groupChatId;
    private String groupchatPicture;
    private List<String> members;
    private String name;
    private List<MessageGroup> messageGroups;

    public GroupChat(){

    }

    public GroupChat(String groupchatPicture, List<String> members, String name, List<MessageGroup> messageGroups, String groupChatId) {
        this.groupchatPicture = groupchatPicture;
        this.members = members;
        this.name = name;
        this.messageGroups = messageGroups;
        this.groupChatId = groupChatId;
    }

    public String getGroupchatPicture() {
        return groupchatPicture;
    }

    public void setGroupchatPicture(String groupchatPicture) {
        this.groupchatPicture = groupchatPicture;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public List<MessageGroup> getMessageGroups() {
        return messageGroups;
    }

    public void setMessageGroups(List<MessageGroup> messageGroups) {
        this.messageGroups = messageGroups;
    }
}
