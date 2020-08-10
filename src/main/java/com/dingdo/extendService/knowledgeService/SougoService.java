package com.dingdo.extendService.knowledgeService;

import com.dingdo.extendService.MsgExtendService;

public interface SougoService extends MsgExtendService {

    /**
     * 从百度知道获取答案
     * @return
     */
    public String getReplyFromSougo(String words);

}
