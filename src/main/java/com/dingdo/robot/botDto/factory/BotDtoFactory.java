package com.dingdo.robot.botDto.factory;

import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.dto.SourceModel;
import com.dingdo.robot.botDto.dto.ReplyMsgModel;
import com.dingdo.robot.botDto.dto.ReqMsgModel;
import com.dingdo.robot.enums.MsgTypeEnum;
import com.dingdo.robot.mirai.MiraiRobotInitializer;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.Objects;
import java.util.Optional;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/22 14:13
 * @since JDK 1.8
 */
public final class BotDtoFactory {

    private BotDtoFactory(){}

    ///////////////////////// mirai //////////////////////////
    public static ReqMsg reqMsg(GroupMessageEvent groupMessageEvent) {
        ReqMsgModel reqMsgModel = new ReqMsgModel();
        Optional<String> msg = groupMessageEvent.getMessage().stream()
                .filter(item -> item instanceof PlainText)
                .map(SingleMessage::contentToString)
                .reduce((t1, t2) -> t1 + t2);

        SourceModel msgSourceModel = new SourceModel();
        msgSourceModel.setType(MsgTypeEnum.GROUP);
        msgSourceModel.setNickname(groupMessageEvent.getSenderName());
        msgSourceModel.setUserId(String.valueOf(groupMessageEvent.getSender().getId()));
        msgSourceModel.setSelfId(String.valueOf(groupMessageEvent.getBot().getId()));
        msgSourceModel.setGroupId(String.valueOf(groupMessageEvent.getGroup().getId()));

        reqMsgModel.setSource(msgSourceModel);
        reqMsgModel.setMsg(msg.orElse(""));
        reqMsgModel.setSourceMsg(groupMessageEvent.getMessage());
        reqMsgModel.setTime((long) groupMessageEvent.getTime());

        Bot bot = MiraiRobotInitializer.INSTANCE.getBotInfo(groupMessageEvent.getBot().getId());
        Objects.requireNonNull(bot, "bot不存在");

        msgSourceModel.setFriend(Objects.nonNull(bot.getFriend(groupMessageEvent.getSender().getId())));

        return reqMsgModel;
    }

    public static ReqMsg reqMsg(FriendMessageEvent friendMessageEvent) {
        ReqMsgModel reqMsgModel = new ReqMsgModel();
        Optional<String> msg = friendMessageEvent.getMessage().stream()
                .filter(item -> item instanceof PlainText)
                .map(SingleMessage::contentToString)
                .reduce((t1, t2) -> t1 + t2);

        SourceModel msgSourceModel = new SourceModel();
        msgSourceModel.setType(MsgTypeEnum.PRIVATE);
        msgSourceModel.setNickname(friendMessageEvent.getSenderName());
        msgSourceModel.setUserId(String.valueOf(friendMessageEvent.getSender().getId()));
        msgSourceModel.setSelfId(String.valueOf(friendMessageEvent.getBot().getId()));

        reqMsgModel.setSource(msgSourceModel);
        reqMsgModel.setMsg(msg.orElse(""));
        reqMsgModel.setSourceMsg(friendMessageEvent.getMessage());
        reqMsgModel.setTime((long) friendMessageEvent.getTime());

        Bot bot = MiraiRobotInitializer.INSTANCE.getBotInfo(friendMessageEvent.getBot().getId());
        Objects.requireNonNull(bot, "bot不存在");

        msgSourceModel.setFriend(Objects.nonNull(bot.getFriend(friendMessageEvent.getSender().getId())));

        return reqMsgModel;
    }

    public static ReplyMsg replyMsg(String message){
        ReplyMsgModel reply = new ReplyMsgModel();
        reply.setReplyMsg(message);
        return reply;
    }

}
