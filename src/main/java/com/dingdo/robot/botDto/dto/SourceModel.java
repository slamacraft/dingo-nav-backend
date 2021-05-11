package com.dingdo.robot.botDto.dto;

import com.dingdo.robot.botDto.Source;
import com.dingdo.robot.enums.MsgTypeEnum;

public class SourceModel implements Source {

    private MsgTypeEnum type;
    private String userId;
    private String nickname;
    private String selfId;
    private String groupId;
    private boolean friend;


    public MsgTypeEnum getType() {
        return type;
    }

    public void setType(MsgTypeEnum type) {
        this.type = type;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getSelfId() {
        return selfId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public boolean isFriend() {
        return friend;
    }
}
