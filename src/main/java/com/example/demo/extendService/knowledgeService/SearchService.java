package com.example.demo.extendService.knowledgeService;

import com.example.demo.extendService.MsgExtendService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

/**
 * 进行网上搜索的服务接口（使用百度百科）
 */
public interface SearchService extends MsgExtendService {

    /**
     * 标准搜索方法
     * @param receiveMsg
     * @return
     */
    ReplyMsg stdSearch(ReceiveMsg receiveMsg);
}
