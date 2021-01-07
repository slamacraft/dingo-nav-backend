package com.dingdo.robot.botDto;


import com.dingdo.robot.enums.MsgTypeEnum;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/22 13:56
 * @since JDK 1.8
 */
public interface ReqMsg {

    /**
     * 获取聊天类型
     */
    MsgTypeEnum getType();

    /**
     * 消息的唯一标识符
     */
    String getId();

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

    /**
     * 消息内容
     */
    String getMsg();

    /**
     * 消息发送的时间戳
     */
    Long getTime();

    Object getSourceMsg();

}
