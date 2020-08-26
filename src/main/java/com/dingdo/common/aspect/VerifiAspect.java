package com.dingdo.common.aspect;

import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.common.exception.CheckException;
import com.dingdo.dao.RobotManagerDao;
import com.dingdo.entities.RobotManagerEntity;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.forte.qqrobot.beans.messages.result.inner.Friend;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.bot.BotManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class VerifiAspect {

    @Autowired
    private BotManager botManager;

    @Autowired(required = false)
    private RobotManagerDao robotManagerDao;


    @Pointcut("@annotation(com.dingdo.common.annotation.VerifiAnnotation)")
    public void verifiCut() {
    }


    /**
     * 校验指令请求者的权限等级
     *
     * @param joinPoint
     * @param verifiAnnotation
     * @see VerificationEnum
     */
    @Before("verifiCut() && @annotation(verifiAnnotation)")
    public void userVerification(JoinPoint joinPoint, VerifiAnnotation verifiAnnotation) {
        Object[] args = joinPoint.getArgs();
        ReqMsg reqMsg = (ReqMsg) Arrays.stream(args)
                .filter(item -> item instanceof ReqMsg)
                .findFirst()
                .get();

        if (reqMsg == null) {
            throw new CheckException("服务请求错误");
        }

        VerificationEnum level = verifiAnnotation.level();

        if(!checkVerification(reqMsg, level)){
            throw new CheckException(level.getErrorMsg());
        }
    }


    /**
     * 校验用户权限级别
     *
     * @param reqMsg
     * @param level
     * @return
     */
    public boolean checkVerification(ReqMsg reqMsg, VerificationEnum level) {
        switch (level) {
            case ROOT:
                return isRoot(reqMsg);
            case DEVELOPER:
                return isDeveloper(reqMsg);
            case MANAGER:
                return isManager(reqMsg);
            case FRIEND:
                return isFriend(reqMsg);
        }
        return false;
    }

    public boolean isRoot(ReqMsg reqMsg) {
        return false;
    }

    public boolean isDeveloper(ReqMsg reqMsg) {
        RobotManagerEntity robotManagerEntity = robotManagerDao.selectById(reqMsg.getUserId());
        if (robotManagerEntity == null) {
            return true;
        }
        return isRoot(reqMsg);
    }

    public boolean isManager(ReqMsg reqMsg) {
        if (!"group".equals(reqMsg.getMessageType())) {
            return false;
        }
        List<String> groupAdminIdList = botManager.getBot(reqMsg.getSelfId())
                .getSender()
                .GETTER
                .getGroupMemberList(reqMsg.getGroupId())
                .stream()
                .filter(item -> !item.getPower().isMember())
                .map(GroupMember::getQQCode)
                .collect(Collectors.toList());

        if (groupAdminIdList.contains(reqMsg.getUserId())) {
            return true;
        }
        return isDeveloper(reqMsg);
    }

    public boolean isFriend(ReqMsg reqMsg) {
        List<String> friendIdList = Arrays.stream(botManager.getBot(reqMsg.getSelfId())
                .getSender()
                .GETTER
                .getFriendList()
                .getAllFriends()
        ).map(Friend::getQQCode).collect(Collectors.toList());

        if (friendIdList.contains(reqMsg.getUserId())) {
            return true;
        }

        return isManager(reqMsg);
    }

}
