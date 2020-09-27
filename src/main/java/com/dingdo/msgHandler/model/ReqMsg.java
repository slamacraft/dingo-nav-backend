package com.dingdo.msgHandler.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @date: 2020/8/10 16:07
 * @since JDK 1.8
 */
@ApiModel("qq机器人消息请求dto")
public class ReqMsg {

    /**
     * 消息类型
     * private  私聊消息
     * group    群组消息
     */
    @ApiModelProperty("消息类型：private-私聊, group-群聊")
    private String messageType;

    /**
     * 消息ID
     */
    @ApiModelProperty("消息唯一标识")
    private String messageId;

    /**
     * 发送者 QQ 号
     */
    @ApiModelProperty("发送者id")
    private String userId;

    /**
     * 所在群ID
     */
    @ApiModelProperty("发送者群id（非群聊下非必填）")
    private String groupId;

    /**
     * 消息内容
     * 特别地，数据类型 message 表示该参数是一个消息类型的参数。
     * 在上报数据中，message 的实际类型根据配置项 post_message_format 的不同而不同，
     * post_message_format 设置为 string 和 array 分别对应字符串和消息段数组；
     * 而在上报请求的回复中，message 类型的字段允许接受字符串、消息段数组、单个消息段对象三种类型的数据。
     */
    @ApiModelProperty("消息内容")
    private String message;

    /**
     * CQ码列表
     */
    @ApiModelProperty("CQ码列表(非必填)")
    private List<CQCode> cqCodeList;

    /**
     * 消息内容
     * 该内容为通过转译后的不含有CQ码的纯文本字符
     */
    @ApiModelProperty("消息内容去除CQ码后(非必填)")
    private String rawMessage;

    /**
     * 字体
     */
    @ApiModelProperty("字体(非必填)")
    private String font;

    /**
     * 时间戳
     */
    @ApiModelProperty("时间戳")
    private Long time;

    /**
     * 上报的机器人QQ号
     */
    @ApiModelProperty("上报的机器人QQ号")
    private String selfId;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称（非必填）")
    private String nickname;

    /**
     * 群名片／备注
     */
    @ApiModelProperty("群名片／备注（非群聊非必填）")
    private String card;


    @Override
    public String toString() {
        return "ReqMsg{" +
                "messageType='" + messageType + '\'' +
                ", messageId='" + messageId + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", message='" + message + '\'' +
                ", cqCodeList=" + cqCodeList +
                ", rawMessage='" + rawMessage + '\'' +
                ", font='" + font + '\'' +
                ", time=" + time +
                ", selfId='" + selfId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", card='" + card + '\'' +
                '}';
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CQCode> getCqCodeList() {
        return cqCodeList;
    }

    public void setCqCodeList(List<CQCode> cqCodeList) {
        this.cqCodeList = cqCodeList;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
