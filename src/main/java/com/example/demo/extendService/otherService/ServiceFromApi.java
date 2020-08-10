package com.example.demo.extendService.otherService;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

public interface ServiceFromApi {

    /**
     * 从api中调用
     * @return
     */
    ReplyMsg sendMsgFromApi(ReceiveMsg receiveMsg);
}
