package com.dingo.extendService.otherService;


import com.dingo.robot.botDto.ReplyMsg;
import com.dingo.robot.botDto.ReqMsg;

public interface ServiceFromApi {

    /**
     * 从api中调用
     * @return
     */
    ReplyMsg sendMsgFromApi(ReqMsg reqMsg);
}
