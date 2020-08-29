package com.dingdo.msgHandler.service;


import com.dingdo.model.msgFromMirai.ReqMsg;

public interface MsgHandleService {

    /**
     * 消息的预处理层
     * 所有对消息的预处理，固定消息的返回都在这里
     * @param reqMsg
     * @return
     */
    String handleMsg(ReqMsg reqMsg);
}
