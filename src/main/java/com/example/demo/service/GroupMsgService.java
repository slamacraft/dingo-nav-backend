package com.example.demo.service;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

/**
 * 群消息处理接口
 */
public interface GroupMsgService extends MsgHandleService{

    /**
     * 真正的消息处理层
     * 以及真正的服务转接层，对消息内容进行处理和转发
     * @param receiveMsg
     * @return
     */
    ReplyMsg handleGroupMsg(ReceiveMsg receiveMsg);

    /**
     * 发送群消息
     * @param groupId
     * @param msg
     */
    void sendGroupMsg(Long groupId, String msg);
}
