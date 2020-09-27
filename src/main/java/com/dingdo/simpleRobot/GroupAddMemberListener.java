package com.dingdo.simpleRobot;

import com.dingdo.util.CQCodeUtil;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

/**
 * 群主成员新增监听器
 *
 * @author slamacraft
 * @date: 2020/8/11 17:02
 * @since JDK 1.8
 */
public class GroupAddMemberListener {

    @Filter(group = {"479867525"})
    @Listen(MsgGetTypes.groupMemberIncrease)
    public void addGroupMemberFor479867525(GroupMemberIncrease groupMemberIncrease, MsgSender sender) throws InterruptedException {
        String beOperatedQQ = groupMemberIncrease.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), CQCodeUtil.atTarget("欢迎新佬！", beOperatedQQ));
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "进群记得改名片哦，格式「XXX」");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "如果不改名的话，据说会莫名其妙地消失\nヽ(*。>Д<)o゜");
        Thread.sleep(1000);
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroupCode(), "要记好了！\n(*￣3￣)╭");
    }
}
