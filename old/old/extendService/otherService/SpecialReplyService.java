package com.example.old.extendService.otherService;

import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/20 11:21
 * @since JDK 1.8
 */
public interface SpecialReplyService {

    /**
     * 复读群里的消息
     * @param reqMsg
     */
    void rereadGroupMsg(ReqMsg reqMsg);

    /**
     * 获取昨天随机一个句子
     *
     * @param reqMsg
     * @return
     */
    String getRandomGroupMsgYesterday(ReqMsg reqMsg);

}
