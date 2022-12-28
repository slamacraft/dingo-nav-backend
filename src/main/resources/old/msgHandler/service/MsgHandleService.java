package com.dingo.msgHandler.service;


import com.dingo.msgHandler.model.ReqMsg;

public interface MsgHandleService {

    /**
     * 
     * @param reqMsg    收到的消息请求
     * @return  响应的消息
     */
    String handleMsg(ReqMsg reqMsg);
}
