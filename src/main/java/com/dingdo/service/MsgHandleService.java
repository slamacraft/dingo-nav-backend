package com.dingdo.service;

import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;

public interface MsgHandleService {

    /**
     * 消息的预处理层
     * 所有对消息的预处理，固定消息的返回都在这里
     * @param receiveMsg
     * @return
     */
    ReplyMsg handleMsg(ReceiveMsg receiveMsg);
}
