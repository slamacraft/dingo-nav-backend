package com.dingdo.msgHandler.service;


import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 私聊消息处理接口
 */
public interface PrivateMsgService extends MsgHandleService{

    /**
     * 真正的消息处理层
     * 以及真正的服务转接层，对消息内容进行处理和转发
     * @param reqMsg
     * @return
     */
    String handlePrivateMsg(ReqMsg reqMsg);

    /**
     * 发送私聊信息
     * @param msg
     * @param userId
     */
    void sendPrivateMsg(String robotId, String userId, String msg);
}
