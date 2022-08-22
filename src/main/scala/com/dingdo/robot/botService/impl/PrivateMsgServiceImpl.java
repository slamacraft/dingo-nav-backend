package com.dingdo.robot.botService.impl;


import com.dingdo.robot.mirai.MiraiRobotInitializer;
import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botService.PrivateMsgService;
import com.dingdo.service.externalApi.SizhiApi;
import net.mamoe.mirai.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateMsgServiceImpl implements PrivateMsgService {

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
        bot.getGroup(Long.parseLong(target)).sendMessage(msg.getReplyMsg());
    }
}
