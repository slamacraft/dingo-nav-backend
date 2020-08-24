package com.dingdo.common.aspect;

import com.dingdo.common.exception.CheckException;

import com.dingdo.model.msgFromMirai.ReqMsg;
import com.forte.qqrobot.beans.messages.result.GroupMemberInfo;
import com.forte.qqrobot.beans.messages.result.GroupMemberList;
import com.forte.qqrobot.bot.BotManager;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class VerifiAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BotManager botManager;

    @Pointcut("@annotation(com.dingdo.common.annotation.VerifiAnnotation)")
    public void verifiCut() {
    }

    @Before("verifiCut()")
    public void userVerification(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object item : args) {
            if (item instanceof ReqMsg) {
                ReqMsg reqMsg = (ReqMsg) item;
                String userId = reqMsg.getUserId();
                String password = (String) redisTemplate.opsForValue().get("ManagerServiceImpl$" + userId);
                if (StringUtils.isBlank(password)) {
                    String botId = reqMsg.getSelfId();
                    String groupId = reqMsg.getGroupId();
                    GroupMemberInfo groupMemberInfo = botManager.getBot(botId)
                            .getSender()
                            .GETTER
                            .getGroupMemberInfo(groupId, userId);
                    if (groupMemberInfo.getPowerType().isMember()) {
                        throw new CheckException("该指令为管理员指令，请先登录");
                    }
                }
                // 刷新自动下线时间
                redisTemplate.opsForValue().set("ManagerServiceImpl$" + userId, password, 30, TimeUnit.MINUTES);
            }
        }
    }

}
