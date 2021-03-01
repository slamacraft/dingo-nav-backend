package com.example.old.msgHandler.service;

import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 消息转发服务接口
 */
public interface MsgService extends MsgHandleService{

    /**
     * 将接受到的消息反序列化为对象
     * @param request
     * @return
     */
    String receive(ReqMsg request);
}
