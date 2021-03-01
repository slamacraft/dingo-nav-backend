package com.dingdo.robot.botService.impl;

import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botService.GroupMsgService;
import com.dingdo.robot.mirai.MiraiRobotInitializer;
import com.dingdo.service.externalApi.SizhiApi;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMsgServiceImpl implements GroupMsgService {

    @Autowired
    private SizhiApi sizhiApi;
//    private final BotManager botManager;


    @Override
    public ReplyMsg handleMsg(ReqMsg reqMsg) {
        return sizhiApi.getReply(reqMsg);
    }


    @Override
    public void sendMsg(String source, String target, ReplyMsg msg) {
        Bot bot = MiraiRobotInitializer.INSTANCE.getBotInfo(Long.parseLong(source));
        if (bot == null) return;
        Group group = bot.getGroup(Long.parseLong(target));
        if (group == null) return;
        if (msg.getReply() != null && msg.getReply() instanceof Message) {
            group.sendMessage((Message) msg.getReply());
        } else {
            group.sendMessage(msg.getReplyMsg());
        }
    }
}
