package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.model.ReqMsg;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import org.apache.commons.lang3.StringUtils;

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
     * 监听私信消息并复读
     */
    @Listen(MsgGetTypes.privateMsg)
    public void privateMsgListen(PrivateMsg privateMsg, MsgSender sender) {
        String reply = super.getReplyFromRobot(new ReqMsg(privateMsg));
        if(StringUtils.isBlank(reply)){
            return;
        }
        while(reply.length() > 300){
            sender.SENDER.sendPrivateMsg(privateMsg, reply.substring(0, 300));
            reply = reply.substring(300);
        }
        sender.SENDER.sendPrivateMsg(privateMsg, reply);
    }

//    @Listen(MsgGetTypes.privateMsg)
//    public void privateMsgListen(PrivateMsg privateMsg, MsgSender sender) {
//        String msg = "[CQ:app,content={ \"app\": \"com.tencent.structmsg\"&#44; \"config\": { \"autosize\": true&#44; \"ctime\": 1600045277&#44; \"forward\": true&#44; \"token\": \"\"&#44; \"type\": \"normal\" }&#44; \"desc\": \"音乐\"&#44; \"meta\": { \"music\": { \"action\": \"\"&#44; \"android_pkg_name\": \"\"&#44; \"app_type\": 1&#44; \"appid\": 100495085&#44; \"desc\": \"RAM WIRE\"&#44; \"jumpUrl\": \"\"&#44; \"musicUrl\": \"http:\\/\\/music.163.com\\/song\\/media\\/outer\\/url?id=32317208&amp\"&#44; \"preview\": \"http://localhost:8081/test.jpg\"&#44; \"sourceMsgId\": \"0\"&#44; \"source_icon\": \"\"&#44; \"source_url\": \"\"&#44; \"tag\": \"点歌姬\"&#44; \"title\": \"僕らの手には何もないけど、 (尽管我们手中空无一物)\" } }&#44; \"prompt\": \"&#91;分享&#93;僕らの手には何もないけど、 (尽管我们手中空无一物)\"&#44; \"ver\": \"0.0.0.1\"&#44; \"view\": \"music\" }]";
//        System.out.println(privateMsg.getMsg());
//        sender.SENDER.sendPrivateMsg(privateMsg, msg);
//        /*以下都是消息复读的方式*/
////        sender.SENDER.sendPrivateMsg(msg.getQQ(), msg.getMsg());
////        sender.SENDER.sendPrivateMsg(msg.getQQCode(), msg.getMsg());
////        sender.reply(msg, msg.getMsg(), false);
//    }

}
