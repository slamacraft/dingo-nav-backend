package com.dingo.extendService.knowledgeService;

import com.dingo.extendService.MsgExtendService;

public interface SougoService extends MsgExtendService {

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromSougo(String words);

}
