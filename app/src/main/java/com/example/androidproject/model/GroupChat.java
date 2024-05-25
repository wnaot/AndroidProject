package com.example.androidproject.model;

import java.util.List;
import java.util.Map;

public class GroupChat {
    private String groupChatId;
    private String groupchatPicture;
    private List<String> members;
    private String name;
    private List<MessageGroup> messageGroups;

    private Map<String, Boolean> admin;

    public GroupChat(){

    }

    public GroupChat(String groupchatPicture, List<String> members, String name, List<MessageGroup> messageGroups, String groupChatId) {
        this.groupchatPicture = groupchatPicture;
        this.members = members;
        this.name = name;
        this.messageGroups = messageGroups;
        this.groupChatId = groupChatId;
    }

    public GroupChat(String groupChatId, String groupchatPicture, List<String> members, String name, List<MessageGroup> messageGroups, Map<String, Boolean> admin) {
        this.groupChatId = groupChatId;
        this.groupchatPicture = groupchatPicture;
        this.members = members;
        this.name = name;
        this.messageGroups = messageGroups;
        this.admin = admin;
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

    public Map<String, Boolean> getAdmin() {
        return admin;
    }

    public void setAdmin(Map<String, Boolean> admin) {
        this.admin = admin;
    }
}
