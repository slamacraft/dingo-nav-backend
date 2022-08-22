package com.dingdo.service;


import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.service.enums.StrategyEnum;

/**
 * 功能服务接口
 * 实现这个接口可以在abstractMsgService中注册这个服务
 *
 * @author slamacraft
 */
public interface MsgProcessor {

    /**
     * 返回封装完毕的回复信息
     *
     * @param reqMsg {@link ReqMsg}
     * @return {@link ReplyMsg}
     */
    ReplyMsg getReply(ReqMsg reqMsg);

    /**
     * 获取服务类型，每个服务自行添加到{@link StrategyEnum}枚举中
     *
     * @return
     */
    StrategyEnum getType();
}
