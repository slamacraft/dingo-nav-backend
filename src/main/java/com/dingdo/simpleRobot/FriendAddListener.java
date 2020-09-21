package com.dingdo.simpleRobot;

import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.FriendAddRequest;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.sender.senderlist.SenderGetList;
import com.simbot.component.mirai.messages.Reply;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友添加事件监听器
 *
 * @author slamacraft
 * @date: 2020/8/25 11:22
 * @since JDK 1.8
 */
public class FriendAddListener {

    /**
     * 只允许群内的成员添加机器人为好友
     *
     * @param friendAddRequest  好友添加请求
     * @param sender    机器人送信器
     * @return  机器人响应结果
     */
    @Listen(MsgGetTypes.friendAddRequest)
    public Reply friendAddRequestListener(FriendAddRequest friendAddRequest, MsgSender sender) {
        String requestQQqq = friendAddRequest.getQQ();
        SenderGetList SenderGetter = sender.GETTER;

        List<String> groupMemberList = SenderGetter.getGroupList()
                .stream()
                .map(item -> SenderGetter.getGroupMemberList(item.getGroupCode())
                        .stream()
                        .map(GroupMember::getQQCode)
                        .collect(Collectors.toList())
                )
                .reduce((item1, item2) -> {
                    item1.addAll(item2);
                    return item1;
                })
                .get()
                .stream()
                .distinct()
                .collect(Collectors.toList());

        if (groupMemberList.contains(requestQQqq)) {
            return Reply.getAgreeReply();
        }

        return Reply.getIgnoreReply();
    }

}
