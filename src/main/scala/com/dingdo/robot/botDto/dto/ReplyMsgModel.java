package com.dingdo.robot.botDto.dto;

import com.dingdo.robot.botDto.ReplyMsg;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/3 15:06
 * @since JDK 1.8
 */
public class ReplyMsgModel implements ReplyMsg {

    private String replyMsg;
    private Object reply;

    public ReplyMsgModel() {
    }

    public ReplyMsgModel(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public ReplyMsgModel(String replyMsg, Object reply) {
        this.replyMsg = replyMsg;
        this.reply = reply;
    }

    @Override
    public String getReplyMsg() {
        return replyMsg;
    }

    @Override
    public Object getReply() {
        return reply;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public void setReply(Object reply) {
        this.reply = reply;
    }
}
