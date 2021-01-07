package com.dingdo.robot.botDto;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/3 15:00
 * @since JDK 1.8
 */
public interface ReplyMsg {

    /**
     * 获取机器人的回答消息文本
     *
     * @return
     */
    String getReplyMsg();

  /**
   * 获取机器人回答的消息体原文，由具体的实例决定
   * @return
   */
    Object getReply();
}
