package com.dingdo.service;

import com.dingdo.model.msgFromMirai.ReqMsg;

/**
 * 群消息处理接口
 */
public interface GroupMsgService extends MsgHandleService{

    /**
     * 真正的消息处理层
     * 以及真正的服务转接层，对消息内容进行处理和转发
     * @param reqMsg
     * @return
     */
    String handleGroupMsg(ReqMsg reqMsg);

    /**
     * 发送群消息
     * @param groupId
     * @param msg
     */
    void sendGroupMsg(String robotId, String groupId, String msg);
}
