package com.dingdo.msgHandler.service;

import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 消息转发服务接口
 */
public interface MsgService extends MsgHandleService {

    /**
     * 将接受到的消息反序列化为对象
     *
     * @param reqMsg 请求消息
     * @return 请求结果
     */
    String receive(ReqMsg reqMsg);
}
