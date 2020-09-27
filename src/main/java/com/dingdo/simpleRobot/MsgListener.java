package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.MsgService;
import com.dingdo.util.CQCodeUtil;
import com.dingdo.util.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @date: 2020/8/11 10:06
 * @since JDK 1.8
 */
public class MsgListener {

    private static MsgService msgService = SpringContextUtils.getBean(MsgService.class);

    public String getReplyFromRobot(ReqMsg reqMsg) {
        if(msgService == null){
            msgService = SpringContextUtils.getBean(MsgService.class);
            return "";
        }
        return msgService.handleMsg(reqMsg);
    }


    /**
     * 发送消息
     * @param reply 发送的消息
     * @param target    发送的目标id，可以是群id也可以是qq号
     * @param sender    机器人消息发送器函数接口
     *                  {@link RobotSender}
     */
    protected void sendMsg(String reply, String target, RobotSender sender){
        if(StringUtils.isBlank(reply)){
            return;
        }

        if(CQCodeUtil.isCQCode(reply)){
            sender.sendMsg(target, reply);
            return;
        }

        while(reply.length() > 300){
            sender.sendMsg(target, reply.substring(0, 300));
            reply = reply.substring(300);
        }
        sender.sendMsg(target, reply);
    }

}
