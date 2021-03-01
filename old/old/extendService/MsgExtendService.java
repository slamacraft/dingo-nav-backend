package com.example.old.extendService;


import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 功能服务接口
 * 实现这个接口可以在abstractMsgService中注册这个服务
 */
public interface MsgExtendService {

    /**
     * 返回封装完毕的回复信息
     * @return
     */
    String sendReply(ReqMsg reqMsg);

    /**
     * 返回处理完毕的回复信息
     * @return
     */
    String getReply(ReqMsg reqMsg);
}
