package com.dingdo.simpleRobot;

import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/11 17:02
 * @since JDK 1.8
 */
public class GroupAddMemberListener {

    @Listen(MsgGetTypes.groupMemberIncrease)
    public void addGroupMember(GroupMemberIncrease groupMemberIncrease, MsgSender sender) throws InterruptedException {
        String beOperatedQQ = groupMemberIncrease.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberIncrease, atSenderOnBeginning(beOperatedQQ, "欢迎新佬！"));
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease, "进群记得改名片哦，格式「XXX」");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease, "如果不改名的话，据说会莫名其妙地消失\nヽ(*。>Д<)o゜");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease, "要记好了！\n(*￣3￣)╭");
    }

    /**
     * 在句首at某人
     *
     * @param reply
     * @param userId
     */
    private String atSenderOnBeginning(String reply, String userId) {
        return "[CQ:at,qq=" + userId + "]" + reply;
    }

}
