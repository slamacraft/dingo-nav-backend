package com.dingdo.extendService;


import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 功能服务接口
 * 实现这个接口可以在abstractMsgService中注册这个服务
 */
public interface MsgExtendService {

    /**
     * 通过apiService请求额外的功能
     * @param reqMsg    请求消息
     * @return  请求结果
     */
    String sendReply(ReqMsg reqMsg);
}
