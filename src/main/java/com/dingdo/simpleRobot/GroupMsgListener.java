package com.dingdo.simpleRobot;

import com.dingdo.model.msgFromMirai.ReqMsg;
import com.forte.qqrobot.BotRuntime;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;

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
    public void groupMsgListener(GroupMsg groupMsg, MsgSender sender) {
//        BotRuntime.getRuntime().getBotManager().getBot("3087687530").getSender();
        sender.SENDER.sendPrivateMsg(groupMsg, super.getReplyFromRobot(new ReqMsg(groupMsg)));
    }

    private String sendPicture(String imgPath) {
        return KQCodeUtils.INSTANCE.toCq("image", imgPath);
    }

}
