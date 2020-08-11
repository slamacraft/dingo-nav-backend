package com.dingdo.extendService.knowledgeService;

import com.dingdo.extendService.MsgExtendService;

import com.dingdo.model.msgFromMirai.ReqMsg;

public interface ZhidaoService extends MsgExtendService {

    /**
     * 标准查询语句
     * @param reqMsg
     * @return
     */
    public String stdReplyFromBaidu(ReqMsg reqMsg);

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromBaidu(String words);

}
