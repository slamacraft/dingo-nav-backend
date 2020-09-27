package com.dingdo.msgHandler.service;


/**
 * 私聊消息处理接口
 */
public interface PrivateMsgService extends MsgHandleService {

    /**
     * 发送私聊信息
     *
     * @param robotId 机器人id
     * @param userId  用户id
     * @param msg     消息
     */
    void sendPrivateMsg(String robotId, String userId, String msg);
}
