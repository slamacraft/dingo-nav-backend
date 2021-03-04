package com.dingdo.robot.mirai;

import com.dingdo.componets.conrtruction.base.ConstructionReply;
import com.dingdo.componets.conrtruction.base.Constructor;
import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.factory.BotDtoFactory;
import com.dingdo.robot.botService.GroupMsgService;
import com.dingdo.robot.botService.PrivateMsgService;
import com.dingdo.service.base.RepeatComponent;
import kotlin.Unit;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import scala.Option;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private Constructor constructor;

    private List<String> loginInfo;

    @PostConstruct
    public void run() throws InterruptedException {
        Map<Long, String> botUserPwInfo = getBotUserPwInfo();
        robotInitializer.run(botUserPwInfo);
        Thread.sleep(3000);
        robotInitializer.registeredFriendMsgEvent(this::privateEvent);
        robotInitializer.registeredGroupMsgEvent(this::groupEvent);
    }


    public Unit groupEvent(@NotNull GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        ReqMsg receive = BotDtoFactory.reqMsg(event);

        Option<ConstructionReply> runResult = constructor.executor(receive);
        if (runResult.filter(reply -> !reply.isFailure()).isDefined()) {
            runResult.get().reply();
            return null;
        }

        repeatComponent.repeat(receive);

        Optional<At> atBotOption = message.stream()
                .filter(item -> item instanceof At && ((At) item).getTarget() == event.getBot().getId())
                .map(item -> (At) item)
                .findFirst();

        if (!atBotOption.isPresent()) {
            return null;
        }

        ReplyMsg replyMsg = groupMsgService.handleMsg(receive);
        event.getGroup().sendMessage(replyMsg.getReplyMsg());
        return null;
    }


    public Unit privateEvent(FriendMessageEvent event) {
        ReqMsg receive = BotDtoFactory.reqMsg(event);

        Option<ConstructionReply> runResult = constructor.executor(receive);
        if (runResult.filter(reply -> !reply.isFailure()).isDefined()) {
            runResult.get().reply();
            return null;
        }

        ReplyMsg replyMsg = privateMsgService.handleMsg(receive);
        event.getFriend().sendMessage(replyMsg.getReplyMsg());
        return null;
    }


    @NotNull
    private Map<Long, String> getBotUserPwInfo() {
        Map<Long, String> result = new HashMap<>();
        // 从启动后加载的配置文件中加载登录机器人的账号密码
        loginInfo.forEach(bot -> {
            String[] split = bot.split(":");
            if (split.length != 2) {
                logger.warn("登录信息填写错误：" + bot);
                return;
            }

            result.put(Long.parseLong(split[0]), split[1]);
        });

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
