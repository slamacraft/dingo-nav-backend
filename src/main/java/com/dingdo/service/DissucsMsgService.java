package com.dingdo.service;

import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;

public interface DissucsMsgService {

    ReplyMsg handleDissucsMsg(ReceiveMsg receiveMsg);
}
