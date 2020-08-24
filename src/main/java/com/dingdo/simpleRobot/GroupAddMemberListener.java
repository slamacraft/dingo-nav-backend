package com.dingdo.simpleRobot;

import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
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
    public void addGroupMemberFor479867525(GroupMemberIncrease groupMemberIncrease, MsgSender sender) throws InterruptedException {
        if (!groupMemberIncrease.getGroupCode().equals("479867525")) {
            return;
        }
        String beOperatedQQ = groupMemberIncrease.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), atSenderOnBeginning("欢迎新佬！", beOperatedQQ));
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "进群记得改名片哦，格式「XXX」");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "如果不改名的话，据说会莫名其妙地消失\nヽ(*。>Д<)o゜");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "要记好了！\n(*￣3￣)╭");
    }

    @Listen(MsgGetTypes.groupMemberIncrease)
    public void addGroupMemberDefault(GroupMemberIncrease groupMemberIncrease, MsgSender sender) throws InterruptedException {
        String beOperatedQQ = groupMemberIncrease.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), atSenderOnBeginning("欢迎！", beOperatedQQ));
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
