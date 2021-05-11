package com.dingdo.robot.botDto;

import com.dingdo.robot.enums.MsgTypeEnum;

public interface Source {

    MsgTypeEnum getType();

    /**
     * 发送这条消息的用户qq号
     */
    String getUserId();

    /**
     * 发送这条消息的用户名称
     */
    String getNickname();

    /**
     * 接收到这条消息的机器人qq号
     */
    String getSelfId();

    /**
     * 群id（非群聊时没有）
     */
    String getGroupId();


    boolean isFriend();

}
