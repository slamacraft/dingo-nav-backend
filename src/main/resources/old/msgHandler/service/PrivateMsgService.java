package com.dingdo.msgHandler.service;


import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 私聊消息处理接口
 */
public interface PrivateMsgService extends MsgHandleService{

    /**
     * 发送私聊信息
     * @param msg
     * @param userId
     */
    void sendPrivateMsg(String robotId, String userId, String msg);
}
