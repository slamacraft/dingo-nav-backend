package com.example.old.common.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.example.old.common.annotation.VerifiAnnotation;
import com.example.old.common.exception.CheckException;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.mvc.entities.RobotManagerEntity;
import com.dingdo.mvc.service.RobotManagerService;
import com.forte.qqrobot.beans.messages.result.inner.Friend;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.bot.BotManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验方法切面
 */
@Aspect
@Component
public class VerifiAspect {

    private final BotManager botManager;
    private final RobotManagerService robotManagerService;

    public VerifiAspect(BotManager botManager, RobotManagerService robotManagerService) {
        this.botManager = botManager;
        this.robotManagerService = robotManagerService;
    }


    @Pointcut("@annotation(com.dingdo.common.annotation.VerifiAnnotation)")
    public void verifiCut() {
    }


    /**
     * 校验指令请求者的权限等级
     *
     * @param joinPoint 切点
     * @param verifiAnnotation  校验注解
     * @see VerificationEnum
     */
    @Before("verifiCut() && @annotation(verifiAnnotation)")
    public void userVerification(JoinPoint joinPoint, VerifiAnnotation verifiAnnotation) {
        Object[] args = joinPoint.getArgs();
        ReqMsg reqMsg = (ReqMsg) Arrays.stream(args)
                .filter(item -> item instanceof ReqMsg)
                .findFirst()
                .get();

        VerificationEnum level = verifiAnnotation.level();

        if(checkVerification(reqMsg, level)){
            throw new CheckException(level.getErrorMsg());
        }
    }


    /**
     * 查询用户的权限级别
     * @param userId    用户id
     * @return  用户权限entity
     */
    public RobotManagerEntity getRobotManager(String userId){
        return robotManagerService.getById(userId);
    }

    /**
     * 校验用户权限级别
     *
     * @param reqMsg    请求消息
     * @param level     权限等级
     * @return
     */
    public boolean checkVerification(ReqMsg reqMsg, VerificationEnum level, RobotManagerEntity... managerEntities) {
        switch (level) {
            case ROOT:{
                if(ArrayUtil.isEmpty(managerEntities)){
                    return !isRoot(reqMsg);
                }
                return !isRoot(managerEntities[0]);
            }
            case DEVELOPER: {
                if (ArrayUtil.isEmpty(managerEntities)) {
                    return !isDeveloper(reqMsg);
                }
                return !isDeveloper(managerEntities[0]);
            }
            case MANAGER:
                return !isManager(reqMsg);
            case FRIEND:
                return !isFriend(reqMsg);
        }
        return true;
    }


    /**
     * 是否是超级管理员
     * @param robotManagerEntity    权限entity
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
    public boolean isRoot(RobotManagerEntity robotManagerEntity) {
        if(robotManagerEntity == null){
            return false;
        }
        if(robotManagerEntity.getLevel() == 0){
            return true;
        }
        return false;
    }


    /**
     * 是否是超级管理员
     * @param reqMsg    请求消息
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
    public boolean isRoot(ReqMsg reqMsg) {
        RobotManagerEntity robotManagerEntity = robotManagerService.getById(reqMsg.getUserId());
        return isRoot(robotManagerEntity);
    }


    /**
     * 是否是开发者
     * @param reqMsg    请求消息
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
    public boolean isDeveloper(ReqMsg reqMsg) {
        return isDeveloper(robotManagerService.getById(reqMsg.getUserId()));
    }

    /**
     * 是否是开发者
     * @param robotManagerEntity    权限实体
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
    public boolean isDeveloper(RobotManagerEntity robotManagerEntity) {
        if (robotManagerEntity != null) {
            return true;
        }
        return isRoot(robotManagerEntity);
    }


    /**
     * 是否是群管理
     * @param reqMsg    请求消息
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
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

    /**
     * 是否是好友
     * @param reqMsg    请求消息
     * @return  如果是，返回<tt>true</tt>
     *          否则<tt>false</tt>
     */
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
