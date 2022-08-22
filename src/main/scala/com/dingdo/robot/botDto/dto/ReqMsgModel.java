package com.dingdo.robot.botDto.dto;

import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.enums.MsgTypeEnum;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/3 15:02
 * @since JDK 1.8
 */
public class ReqMsgModel implements ReqMsg {
    private MsgTypeEnum type;
    private String id;
    private String userId;
    private String nickname;
    private String selfId;
    private String groupId;
    private String msg;
    private Long time;
    private Object sourceMsg;

    public ReqMsgModel() {
    }

    public void setType(MsgTypeEnum type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setSourceMsg(Object sourceMsg) {
        this.sourceMsg = sourceMsg;
    }

    @Override
    public MsgTypeEnum getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
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
    public String getMsg() {
        return msg;
    }

    @Override
    public Long getTime() {
        return time;
    }

    @Override
    public Object getSourceMsg() {
        return sourceMsg;
    }
}
