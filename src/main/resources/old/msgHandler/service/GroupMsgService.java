package com.dingdo.msgHandler.service;

import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 群消息处理接口
 */
public interface GroupMsgService extends MsgHandleService{

    /**
     * 发送群消息
     * @param groupId
     * @param msg
     */
    void sendGroupMsg(String robotId, String groupId, String msg);
}
