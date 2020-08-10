package com.dingdo.Component.componentBean;

import lombok.Data;

@Data
public class MsgBean {

    /**
     * 消息来源的qq号
     */
    private Long sourceId;

    /**
     * 消息来源的类型
     * group:群聊
     * private:私聊
     */
    private String sourceType;

    /**
     * 消息来源的qq名称
     */
    private String sourceNickName;

    /**
     * 消息发送的目标qq号
     */
    private Long targetId;

    /**
     * 消息发送的目标类型
     * group:群聊
     * private:私聊
     */
    private String targetType;

    /**
     * 消息体
     */
    private String msg;

}
