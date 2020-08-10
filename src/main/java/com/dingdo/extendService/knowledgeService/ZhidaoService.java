package com.dingdo.extendService.knowledgeService;

import com.dingdo.extendService.MsgExtendService;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;

public interface ZhidaoService extends MsgExtendService {

    /**
     * 标准查询语句
     * @param receiveMsg
     * @return
     */
    public ReplyMsg stdReplyFromBaidu(ReceiveMsg receiveMsg);

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromBaidu(String words);

}
