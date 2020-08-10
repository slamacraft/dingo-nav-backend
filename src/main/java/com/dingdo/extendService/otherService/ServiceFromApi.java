package com.dingdo.extendService.otherService;

import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;

public interface ServiceFromApi {

    /**
     * 从api中调用
     * @return
     */
    ReplyMsg sendMsgFromApi(ReceiveMsg receiveMsg);
}
