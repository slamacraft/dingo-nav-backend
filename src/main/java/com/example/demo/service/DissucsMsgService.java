package com.example.demo.service;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

public interface DissucsMsgService {

    ReplyMsg handleDissucsMsg(ReceiveMsg receiveMsg);
}
