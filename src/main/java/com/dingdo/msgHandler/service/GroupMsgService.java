package com.dingdo.msgHandler.service;

/**
 * 群消息处理接口
 */
public interface GroupMsgService extends MsgHandleService {

    /**
     * 发送群消息
     *
     * @param robotId 机器人id
     * @param groupId 群号
     * @param msg     消息
     */
    void sendGroupMsg(String robotId, String groupId, String msg);
}
