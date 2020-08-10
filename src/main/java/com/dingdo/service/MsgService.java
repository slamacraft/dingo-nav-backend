package com.dingdo.service;

import com.dingdo.model.msgFromCQ.ReplyMsg;

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
    ReplyMsg receive(HttpServletRequest httpServletRequest);
}
