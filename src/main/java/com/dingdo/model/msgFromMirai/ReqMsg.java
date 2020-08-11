package com.dingdo.model.msgFromMirai;

import com.dingdo.enums.MsgTypeEnum;
import lombok.Data;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/10 16:07
 * @since JDK 1.8
 */
@Data
public class ReqMsg {


    public ReqMsg(Object msg) {
        MsgTypeEnum.createReqMsg(msg, this);
    }

    /**
     * 消息类型
     * private  私聊消息
     * group    群组消息
     * discuss  讨论组消息
     */
    private String messageType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送者 QQ 号
     */
    private String userId;

    /**
     * 所在群ID
     */
    private String groupId;

    /**
     * 讨论组 ID
     */
    private String discussId;

    /**
     * 消息内容
     * 特别地，数据类型 message 表示该参数是一个消息类型的参数。
     * 在上报数据中，message 的实际类型根据配置项 post_message_format 的不同而不同，
     * post_message_format 设置为 string 和 array 分别对应字符串和消息段数组；
     * 而在上报请求的回复中，message 类型的字段允许接受字符串、消息段数组、单个消息段对象三种类型的数据。
     */
    private String message;


    /**
     * 字体
     */
    private String font;

    /**
     * 时间戳
     */
    private Long time;

    /**
     * 上报的机器人QQ号
     */
    private String selfId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 群名片／备注
     */
    private String card;

    @Override
    public String toString() {
        return "ReqMsg{" +
                "messageType='" + messageType + '\'' +
                ", messageId='" + messageId + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", discussId='" + discussId + '\'' +
                ", message='" + message + '\'' +
                ", font='" + font + '\'' +
                ", time=" + time +
                ", selfId=" + selfId +
                ", nickname='" + nickname + '\'' +
                ", card='" + card + '\'' +
                '}';
    }
}
