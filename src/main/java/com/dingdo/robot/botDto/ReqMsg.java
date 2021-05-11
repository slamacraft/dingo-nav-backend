package com.dingdo.robot.botDto;


/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/22 13:56
 * @since JDK 1.8
 */
public interface ReqMsg {

    /**
     * 消息的唯一标识符
     */
    String getId();

    Source getSource();

    /**
     * 消息内容
     */
    String getMsg();

    /**
     * 消息发送的时间戳
     */
    Long getTime();

    Object getSourceMsg();
}
