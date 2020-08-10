package com.dingdo.service;

import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;

/**
 * 私聊消息处理接口
 */
public interface PrivateMsgService extends MsgHandleService{

    /**
     * 真正的消息处理层
     * 以及真正的服务转接层，对消息内容进行处理和转发
     * @param receiveMsg
     * @return
     */
    ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg);

    /**
     * 发送私聊信息
     * @param msg
     * @param userId
     */
    void sendPrivateMsg(Long userId, String msg);
}
