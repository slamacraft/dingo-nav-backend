package com.dingdo.robot.botDto.factory;

import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.dto.ReplyMsgModel;
import com.dingdo.robot.botDto.dto.ReqMsgModel;
import com.dingdo.robot.enums.MsgTypeEnum;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.PlainText;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/22 14:13
 * @since JDK 1.8
 */
public final class BotDtoFactory {

    private BotDtoFactory(){}

    ///////////////////////// mirai //////////////////////////
    public static ReqMsg reqMsg(GroupMessageEvent groupMessageEvent){
        ReqMsgModel reqMsgModel = new ReqMsgModel();
        PlainText text = groupMessageEvent.getMessage().first(PlainText.Key);
        String msg = text != null
                ? text.contentToString()
                : "干嘛";
        reqMsgModel.setMsg(msg);
        reqMsgModel.setSourceMsg(groupMessageEvent.getMessage());
        reqMsgModel.setType(MsgTypeEnum.GROUP);
        reqMsgModel.setNickname(groupMessageEvent.getSenderName());
        reqMsgModel.setUserId(String.valueOf(groupMessageEvent.getSender().getId()));
        reqMsgModel.setSelfId(String.valueOf(groupMessageEvent.getBot().getId()));
        reqMsgModel.setGroupId(String.valueOf(groupMessageEvent.getGroup().getId()));
        reqMsgModel.setTime((long) groupMessageEvent.getTime());

        return reqMsgModel;
    }

    public static ReqMsg reqMsg(FriendMessageEvent friendMessageEvent){
        ReqMsgModel reqMsgModel = new ReqMsgModel();
        StringBuilder msg = new StringBuilder();
        friendMessageEvent.getMessage().forEachContent(item->{
            msg.append(item.contentToString());
            return null;
        });
        reqMsgModel.setMsg(msg.toString());
        reqMsgModel.setSourceMsg(friendMessageEvent.getMessage());
        reqMsgModel.setType(MsgTypeEnum.GROUP);
        reqMsgModel.setNickname(friendMessageEvent.getSenderName());
        reqMsgModel.setUserId(String.valueOf(friendMessageEvent.getSender().getId()));
        reqMsgModel.setSelfId(String.valueOf(friendMessageEvent.getBot().getId()));
        reqMsgModel.setTime((long) friendMessageEvent.getTime());

        return reqMsgModel;
    }

    public static ReplyMsg replyMsg(String message){
        ReplyMsgModel reply = new ReplyMsgModel();
        reply.setReplyMsg(message);
        return reply;
    }

}
