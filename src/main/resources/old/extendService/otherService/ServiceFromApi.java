package com.dingdo.extendService.otherService;


import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;

public interface ServiceFromApi {

    /**
     * 从api中调用
     * @return
     */
    ReplyMsg sendMsgFromApi(ReqMsg reqMsg);
}
