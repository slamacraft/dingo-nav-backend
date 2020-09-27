package com.dingdo.msgHandler.service;


import com.dingdo.msgHandler.model.ReqMsg;

public interface MsgHandleService {

    /**
     * 处理收到的请求
     *
     * @param reqMsg 收到的消息请求
     * @return 响应的消息
     */
    String handleMsg(ReqMsg reqMsg);
}
