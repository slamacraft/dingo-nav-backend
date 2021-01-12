package com.dingdo.robot.botService;


import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;

public interface IMsgHandleService {

    /**
     * 
     * @param reqMsg    收到的消息请求
     * @return  响应的消息
     */
    ReplyMsg handleMsg(ReqMsg reqMsg);
}
