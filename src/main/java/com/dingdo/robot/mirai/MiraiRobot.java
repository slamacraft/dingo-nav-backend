package com.dingdo.robot.mirai;

import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.factory.BotDtoFactory;
import com.dingdo.robot.botService.GroupMsgService;
import com.dingdo.robot.botService.PrivateMsgService;
import com.dingdo.service.base.RepeatComponent;
import kotlin.Unit;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/10/12 9:56
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "bots.core")
public class MiraiRobot {

    private static final Logger logger = Logger.getLogger(MiraiRobot.class);

    private final MiraiRobotInitializer robotInitializer = MiraiRobotInitializer.INSTANCE;

    @Autowired
    private PrivateMsgService privateMsgService;

    @Autowired
    private GroupMsgService groupMsgService;

    @Autowired
    private RepeatComponent repeatComponent;

    private List<String> loginInfo;

    @PostConstruct
    public void run() throws InterruptedException {
        Map<Long, String> botUserPwInfo = getBotUserPwInfo();
        robotInitializer.run(botUserPwInfo);
        Thread.sleep(3000);
        robotInitializer.registeredFriendMsgEvent(this::privateEvent);
        robotInitializer.registeredGroupMsgEvent(this::groupEvent);
    }


    public Unit groupEvent(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        ReqMsg receive = BotDtoFactory.reqMsg(event);
        repeatComponent.repeat(receive);
        At at = message.first(At.Key);
        if(at == null || at.getTarget() != event.getBot().getId()){
            return null;
        }
        ReplyMsg replyMsg = groupMsgService.handleMsg(receive);
        event.getGroup().sendMessage(replyMsg.getReplyMsg());
        return null;
    }


    public Unit privateEvent(FriendMessageEvent event) {
        ReqMsg receive = BotDtoFactory.reqMsg(event);
        ReplyMsg replyMsg = privateMsgService.handleMsg(receive);
        event.getFriend().sendMessage(replyMsg.getReplyMsg());
        return null;
    }


    private Map<Long, String> getBotUserPwInfo() {
        Map<Long, String> result = new HashMap<>();
        // 从启动后加载的配置文件中加载登录机器人的账号密码
        for(String bot:loginInfo){
            String[] split = bot.split(":");
            if(split.length != 2){
                logger.warn("登录信息填写错误：" + bot);
                continue;
            }

            result.put(Long.parseLong(split[0]), split[1]);
        }

        return result;
    }

    public Bot getBotInfo(long userId) {
        return robotInitializer.getBotInfo(userId);
    }

    public List<Bot> getBotList() {
        return robotInitializer.getBotList();
    }


    public List<String> getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(List<String> loginInfo) {
        this.loginInfo = loginInfo;
    }
}
