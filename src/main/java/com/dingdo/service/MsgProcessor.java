package com.dingdo.service;


import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.service.enums.StrategyEnum;

/**
 * 功能服务接口
 * 实现这个接口可以在abstractMsgService中注册这个服务
 */
public interface MsgProcessor {

    /**
     * 返回封装完毕的回复信息
     * @return
     */
    ReplyMsg getReply(ReqMsg reqMsg);

    StrategyEnum getType();
}
