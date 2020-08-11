package com.dingdo.extendService.otherService;


import com.dingdo.model.msgFromMirai.ReqMsg;

public interface ServiceFromApi {

    /**
     * 从api中调用
     * @return
     */
    String sendMsgFromApi(ReqMsg reqMsg);
}
