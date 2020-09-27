package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.factory.RobotMsgFactory;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

/**
 * 群消息监听器
 *
 * @author slamacraft
 * @date: 2020/8/10 15:28
 * @since JDK 1.8
 */
@Beans
public class GroupMsgListener extends MsgListener {

    @Listen(MsgGetTypes.groupMsg)
    public void groupMsgListener(GroupMsg groupMsg, MsgSender sender) {
        String reply = super.getReplyFromRobot(RobotMsgFactory.createReqMsg(groupMsg));
        sendMsg(reply, groupMsg.getGroup(), sender.SENDER::sendPrivateMsg);
    }

}
