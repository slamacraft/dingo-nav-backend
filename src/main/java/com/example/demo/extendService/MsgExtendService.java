package com.example.demo.extendService;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

/**
 * 功能服务接口
 * 实现这个接口可以在abstractMsgService中注册这个服务
 */
public interface MsgExtendService {

    /**
     * 返回封装完毕的回复信息
     * @return
     */
    ReplyMsg sendReply(ReceiveMsg receiveMsg);

    /**
     * 返回处理完毕的回复信息
     * @return
     */
    String getReply(ReceiveMsg receiveMsg);
}
