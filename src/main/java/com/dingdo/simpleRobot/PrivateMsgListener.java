package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.factory.RobotMsgFactory;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

/**
 * 私聊消息监听器
 *
 * @author slamacraft
 * @date: 2020/8/10 15:23
 * @since JDK 1.8
 */
@Beans
public class PrivateMsgListener extends MsgListener{

    /**
     * 监听私信消息
     */
    @Listen(MsgGetTypes.privateMsg)
    public void privateMsgListen(PrivateMsg privateMsg, MsgSender sender) {
        String reply = super.getReplyFromRobot(RobotMsgFactory.createReqMsg(privateMsg));
        sendMsg(reply, privateMsg.getQQ(), sender.SENDER::sendPrivateMsg);
    }

//    @Listen(MsgGetTypes.privateMsg)
//    public void privateMsgListen(PrivateMsg privateMsg, MsgSender sendMsg) {
//        /*以下都是消息复读的方式*/
////        sendMsg.SENDER.sendPrivateMsg(msg.getQQ(), msg.getMsg());
////        sendMsg.SENDER.sendPrivateMsg(msg.getQQCode(), msg.getMsg());
////        sendMsg.reply(msg, msg.getMsg(), false);
//    }

}
