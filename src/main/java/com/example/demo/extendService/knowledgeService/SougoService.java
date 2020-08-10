package com.example.demo.extendService.knowledgeService;

import com.example.demo.extendService.MsgExtendService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

public interface SougoService extends MsgExtendService {

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromSougo(String words);

}
