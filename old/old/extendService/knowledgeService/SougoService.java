package com.example.old.extendService.knowledgeService;

import com.example.old.extendService.MsgExtendService;

public interface SougoService extends MsgExtendService {

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromSougo(String words);

}
