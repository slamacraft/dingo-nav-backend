package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.model.ReqMsg;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/10 15:23
 * @since JDK 1.8
 */
@Beans
public class PrivateMsgListener extends MsgListener{

    /**
     * 监听私信消息并复读
     */
    @Listen(MsgGetTypes.privateMsg)
    public void privateMsgListen(PrivateMsg privateMsg, MsgSender sender) {
        String reply = super.getReplyFromRobot(new ReqMsg(privateMsg));
        while(reply.length() > 300){
            sender.SENDER.sendPrivateMsg(privateMsg, reply.substring(0, 300));
            reply = reply.substring(300);
        }
        sender.SENDER.sendPrivateMsg(privateMsg, reply);
        /*以下都是消息复读的方式*/
//        sender.SENDER.sendPrivateMsg(msg.getQQ(), msg.getMsg());
//        sender.SENDER.sendPrivateMsg(msg.getQQCode(), msg.getMsg());
//        sender.reply(msg, msg.getMsg(), false);
    }

}
