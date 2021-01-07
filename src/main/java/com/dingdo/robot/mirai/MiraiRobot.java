package com.dingdo.robot.mirai;

import com.dingdo.mirai.MiraiRobotInitializer;
import com.dingdo.robot.botDto.ReplyMsg;
import com.dingdo.robot.botDto.ReqMsg;
import com.dingdo.robot.botDto.factory.BotDtoFactory;
import com.dingdo.robot.botService.GroupMsgService;
import com.dingdo.robot.botService.PrivateMsgService;
import kotlin.Unit;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import scala.beans.BeanProperty;

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
@ConfigurationProperties(prefix = "auth.application")
public class MiraiRobot {

    private final MiraiRobotInitializer robotInitializer = MiraiRobotInitializer.INSTANCE;

    @Autowired
    private PrivateMsgService privateMsgService;

    @Autowired
    private GroupMsgService groupMsgService;

    @BeanProperty
    private List<String> qqLoginInfo;


    @PostConstruct
    public void run() throws InterruptedException {
        Map<Long, String> botUserPwInfo = getBotUserPwInfo();
        robotInitializer.run(botUserPwInfo);
        Thread.sleep(3000);
        robotInitializer.registeredFriendMsgEvent(this::privateEvent);
        robotInitializer.registeredGroupMsgEvent(this::groupEvent);
    }

    public static void main(String[] args) throws InterruptedException {
        MiraiRobot miraiRobot = new MiraiRobot();
        miraiRobot.run();
    }


    public Unit groupEvent(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        At at = message.first(At.Key);
        if(at == null || at.getTarget() != event.getBot().getId()){
            return null;
        }
        ReqMsg receive = BotDtoFactory.reqMsg(event);
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
        // todo 从配置文件中获取机器人账号密码
        Map<Long, String> result = new HashMap<>();
//        result.put(3087687530L, "13574159100Q");
        result.put(2270374713L, "13574159100p");
        return result;
    }

    public Bot getBotInfo(long userId) {
        return robotInitializer.getBotInfo(userId);
    }

    public List<Bot> getBotList() {
        return robotInitializer.getBotList();
    }

}
