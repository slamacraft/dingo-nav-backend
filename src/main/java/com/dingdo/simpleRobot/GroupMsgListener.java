package com.dingdo.simpleRobot;

import com.dingdo.model.msgFromMirai.ReqMsg;
import com.forte.qqrobot.BotRuntime;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/10 15:28
 * @since JDK 1.8
 */
public class GroupMsgListener extends MsgListener{

    @Listen(MsgGetTypes.groupMsg)
    public void groupMsgListener(GroupMsg groupMsg, MsgSender sender) throws InterruptedException {
        String reply = super.getReplyFromRobot(new ReqMsg(groupMsg));
        if(StringUtils.isBlank(reply)){
            return;
        }
        while(reply.length() > 300){
            sender.SENDER.sendGroupMsg(groupMsg, reply.substring(0, 300));
            reply = reply.substring(300);
            Thread.sleep(1000);
        }
        sender.SENDER.sendGroupMsg(groupMsg, reply);
    }

    private String sendPicture(String imgPath) {
        return KQCodeUtils.INSTANCE.toCq("image", imgPath);
    }

}
