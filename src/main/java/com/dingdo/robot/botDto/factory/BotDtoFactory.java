package com.dingdo.robot.botDto.factory;

import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
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

        reqMsgModel.setMsg(msg.orElse(""));
        reqMsgModel.setSourceMsg(groupMessageEvent.getMessage());
        reqMsgModel.setType(MsgTypeEnum.GROUP);
        reqMsgModel.setNickname(groupMessageEvent.getSenderName());
        reqMsgModel.setUserId(String.valueOf(groupMessageEvent.getSender().getId()));
        reqMsgModel.setSelfId(String.valueOf(groupMessageEvent.getBot().getId()));
        reqMsgModel.setGroupId(String.valueOf(groupMessageEvent.getGroup().getId()));
        reqMsgModel.setTime((long) groupMessageEvent.getTime());

        Bot bot = MiraiRobotInitializer.INSTANCE.getBotInfo(groupMessageEvent.getBot().getId());
        Objects.requireNonNull(bot, "bot不存在");

        reqMsgModel.setFriendFlag(Objects.nonNull(bot.getFriend(groupMessageEvent.getSender().getId())));

        return reqMsgModel;
    }

    public static ReqMsg reqMsg(FriendMessageEvent friendMessageEvent) {
        ReqMsgModel reqMsgModel = new ReqMsgModel();
        Optional<String> msg = friendMessageEvent.getMessage().stream()
                .filter(item -> item instanceof PlainText)
                .map(SingleMessage::contentToString)
                .reduce((t1, t2) -> t1 + t2);

        reqMsgModel.setMsg(msg.orElse(""));
        reqMsgModel.setSourceMsg(friendMessageEvent.getMessage());
        reqMsgModel.setType(MsgTypeEnum.PRIVATE);
        reqMsgModel.setNickname(friendMessageEvent.getSenderName());
        reqMsgModel.setUserId(String.valueOf(friendMessageEvent.getSender().getId()));
        reqMsgModel.setSelfId(String.valueOf(friendMessageEvent.getBot().getId()));
        reqMsgModel.setTime((long) friendMessageEvent.getTime());

        Bot bot = MiraiRobotInitializer.INSTANCE.getBotInfo(friendMessageEvent.getBot().getId());
        Objects.requireNonNull(bot, "bot不存在");

        reqMsgModel.setFriendFlag(Objects.nonNull(bot.getFriend(friendMessageEvent.getSender().getId())));

        return reqMsgModel;
    }

    public static ReplyMsg replyMsg(String message){
        ReplyMsgModel reply = new ReplyMsgModel();
        reply.setReplyMsg(message);
        return reply;
    }

}
