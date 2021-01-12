package com.dingdo.robot.botService;

import com.dingdo.robot.botDto.ReplyMsg;

/**
 * 消息发送的接口
 */
public interface IMsgSenderService extends IMsgHandleService {

    /**
     * 将消息从指定机器人发送给目标机器人
     */
    void sendMsg(String source, String target, ReplyMsg msg);
}
