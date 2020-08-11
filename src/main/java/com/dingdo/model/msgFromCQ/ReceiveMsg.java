package com.dingdo.model.msgFromCQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 发送请求的实体类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
@Data
class reqMsg {
    /**
     * 上报类型
     * message	收到消息
     * notice	群、讨论组变动等通知类事件
     * request	加好友请求、加群请求／邀请
     */
    private String post_type;

    /**
     * 消息类型
     * private  私聊消息
     * group    群组消息
     * discuss  讨论组消息
     */
    private String message_type;

    /**
     * 消息子类型
     * 私聊消息中，如果是好友则是 friend，如果从群或讨论组来的临时会话则分别是 group、discuss,其他则为other
     * 群组消息中，正常消息是 normal，匿名消息是 anonymous，系统提示（如「管理员已禁止群内匿名聊天」）是 notice
     */
    private String sub_type;

    /**
     * 消息ID
     */
    private Long message_id;

    /**
     * 发送者 QQ 号
     */
    private Long user_id;

    /**
     * 所在群ID
     */
    private Long group_id;

    /**
     * 讨论组 ID
     */
    private Long discuss_id;

    /**
     * 匿名信息，如果不是匿名消息则为 null
     */
    private Object anonymous;

    /**
     * 消息内容
     * 特别地，数据类型 message 表示该参数是一个消息类型的参数。
     * 在上报数据中，message 的实际类型根据配置项 post_message_format 的不同而不同，
     * post_message_format 设置为 string 和 array 分别对应字符串和消息段数组；
     * 而在上报请求的回复中，message 类型的字段允许接受字符串、消息段数组、单个消息段对象三种类型的数据。
     */
    private String message;

    /**
     * 原始消息内容
     */
    private String raw_message;

    /**
     * 字体
     */
    private Long font;

    /**
     * 时间戳
     */
    private Long time;

    /**
     * 上报的机器人QQ号
     */
    private Long self_id;

    /**
     * 发送者
     */
    private Sender sender;

    @Override
    public String toString() {
        return "reqMsg{" +
                "post_type='" + post_type + '\'' +
                ", message_type='" + message_type + '\'' +
                ", sub_type='" + sub_type + '\'' +
                ", message_id=" + message_id +
                ", user_id=" + user_id +
                ", group_id=" + group_id +
                ", discuss_id=" + discuss_id +
                ", anonymous=" + anonymous +
                ", message='" + message + '\'' +
                ", raw_message='" + raw_message + '\'' +
                ", font=" + font +
                ", time=" + time +
                ", self_id=" + self_id +
                ", sender=" + sender +
                '}';
    }
}

