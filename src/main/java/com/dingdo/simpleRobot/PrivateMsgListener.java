package com.dingdo.simpleRobot;

import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.util.SpringContextUtils;
import com.forte.qqrobot.BotRuntime;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;

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
