package com.dingdo.msgHandler.service;

import com.dingdo.msgHandler.model.ReqMsg;

import javax.servlet.http.HttpServletRequest;

/**
 * 消息转发服务接口
 */
public interface MsgService {

    /**
     * 将接受到的消息反序列化为对象
     * @param httpServletRequest
     * @return
     */
    String receive(HttpServletRequest httpServletRequest);

    String handleMsg(ReqMsg reqMsg);
}
